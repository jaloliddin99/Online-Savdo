package uz.promo.selling.ui.auth.google

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.VerificationRes
import uz.promo.selling.utils.SharedPref
import javax.inject.Inject

data class GoogleAuthState(
    val isLoading: Boolean = false,
    val result: VerificationRes? = null,
    val error: String = "",
    val needsPhone: Boolean = false
)

@HiltViewModel
class GoogleAuthViewModel @Inject constructor(
    private val apiInterface: ApiInterface
) : ViewModel() {

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
                            val needsPhone = try {
                                apiInterface.getProfile(SharedPref.deviceToken)
                                    .data.phoneNumber.isNullOrBlank()
                            } catch (_: Exception) {
                                false
                            }

                            _state.value = GoogleAuthState(
                                result = response,
                                needsPhone = needsPhone
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

    fun completeProfile(phoneNumber: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                apiInterface.completeProfile(
                    token = SharedPref.deviceToken,
                    body = mapOf("phoneNumber" to phoneNumber)
                )
                _state.value = _state.value.copy(isLoading = false, needsPhone = false)
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update profile"
                )
            }
        }
    }
}
