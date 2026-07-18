package uz.promo.selling.ui.auth.google

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.VerificationRes
import uz.promo.selling.utils.FileManager.getFileFromUri
import uz.promo.selling.utils.SharedPref
import javax.inject.Inject

data class GoogleAuthState(
    val isLoading: Boolean = false,
    val result: VerificationRes? = null,
    val error: String = "",
    // True when the account still needs the complete-profile step (missing name,
    // lastname or phone). Google/Apple rarely supply all three.
    val needsProfile: Boolean = false,
    // Values Google already gave us, used to prefill the complete-profile form
    // so the user confirms rather than retypes.
    val prefillName: String = "",
    val prefillLastname: String = "",
    val prefillPhotoUrl: String? = null
)

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(
    private val apiInterface: ApiInterface,
    private val application: Application
) : AndroidViewModel(application) {

    private val _state = mutableStateOf(GoogleAuthState())
    val state: State<GoogleAuthState> = _state

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _state.value = GoogleAuthState(isLoading = true)
            val tokenResult = GoogleAuthHelper.signIn(context)

            tokenResult.fold(
                onSuccess = { idToken ->
                    try {
                        val response = apiInterface.googleAuth(mapOf("idToken" to idToken))
                        if (response.status) {
                            SharedPref.deviceToken = "Bearer ${response.token}"
                            SharedPref.refreshToken = response.refreshToken ?: ""
                            SharedPref.loginTime = System.currentTimeMillis()

                            // Bind this device's FCM token to the account. Best-effort:
                            // a Firebase/DI failure here must not block a successful login.
                            try {
                                uz.promo.selling.utils.FcmTokenSync.sync(context)
                            } catch (_: Exception) {
                            }

                            // Whether the user still needs to add a phone number is a
                            // secondary check — it must NOT gate navigation. Previously a
                            // failure (or empty error message) here left the user
                            // authenticated but stuck on the login screen with nothing
                            // shown. If we can't read the profile, just send them Home;
                            // they can add a phone later from their profile.
                            // Read back what Google seeded on the server so the
                            // complete-profile form can be prefilled.
                            val profile = try {
                                apiInterface.getProfile(SharedPref.deviceToken).data
                            } catch (_: Exception) {
                                null
                            }

                            val needsProfile = profile != null && (
                                    profile.phoneNumber.isNullOrBlank() ||
                                            profile.name.isBlank() ||
                                            profile.lastname.isNullOrBlank()
                                    )

                            _state.value = GoogleAuthState(
                                result = response,
                                needsProfile = needsProfile,
                                prefillName = profile?.name.orEmpty(),
                                prefillLastname = profile?.lastname.orEmpty(),
                                prefillPhotoUrl = profile?.profileUrl
                            )
                        } else {
                            _state.value = GoogleAuthState(error = response.message)
                        }
                    } catch (e: Exception) {
                        _state.value = GoogleAuthState(error = e.message ?: "Google auth failed")
                    }
                },
                onFailure = { e ->
                    _state.value = GoogleAuthState(error = e.message ?: "Google sign-in cancelled")
                }
            )
        }
    }

    /**
     * The complete-profile route gets its own ViewModel instance (it is scoped to
     * that nav back-stack entry), so the prefill captured during sign-in is not
     * visible there. Re-read it from the server instead.
     */
    fun loadPrefill() {
        viewModelScope.launch {
            val profile = try {
                apiInterface.getProfile(SharedPref.deviceToken).data
            } catch (_: Exception) {
                return@launch
            }
            _state.value = _state.value.copy(
                prefillName = profile.name,
                prefillLastname = profile.lastname.orEmpty(),
                prefillPhotoUrl = profile.profileUrl
            )
        }
    }

    fun completeProfile(
        name: String,
        lastname: String,
        phoneNumber: String,
        photoUri: Uri? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                apiInterface.completeProfile(
                    token = SharedPref.deviceToken,
                    body = mapOf(
                        "name" to name,
                        "lastname" to lastname,
                        "phoneNumber" to phoneNumber
                    )
                )

                // The photo is optional, and a failed upload must not strand the
                // user on this screen — their name and phone are already saved.
                if (photoUri != null) {
                    try {
                        uploadProfileImage(photoUri)
                    } catch (_: Exception) {
                    }
                }

                _state.value = _state.value.copy(isLoading = false, needsProfile = false)
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update profile"
                )
            }
        }
    }

    private suspend fun uploadProfileImage(photoUri: Uri) {
        val file = application.contentResolver.getFileFromUri(photoUri, application) ?: return
        val compressed = Compressor.compress(application, file)
        if (!compressed.exists()) return

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                compressed.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), compressed)
            )
            .build()

        apiInterface.updateProfileImage(SharedPref.deviceToken, requestBody)
    }
}
