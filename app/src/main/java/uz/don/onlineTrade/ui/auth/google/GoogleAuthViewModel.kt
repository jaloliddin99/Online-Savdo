package uz.don.onlineTrade.ui.auth.google

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.don.onlineTrade.data.remote.ApiInterface
import uz.don.onlineTrade.data.remote.models.VerificationRes
import uz.don.onlineTrade.utils.SharedPref
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

                            // Check if user has phone number by trying to get profile
                            val profileResponse = apiInterface.getProfile(SharedPref.deviceToken)
                            val needsPhone = profileResponse.data.phoneNumber.isNullOrBlank()

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

    fun completeProfile(phoneNumber: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                apiInterface.completeProfile(
                    token = SharedPref.deviceToken,
                    body = mapOf("phoneNumber" to phoneNumber)
                )
                _state.value = _state.value.copy(isLoading = false, needsPhone = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update profile"
                )
            }
        }
    }
}
