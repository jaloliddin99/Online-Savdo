package org.don.onlineTrade.ui.auth.login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.don.onlineTrade.data.remote.models.RegisterMain
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.loginUseCase.LoginUseCase
import org.don.onlineTrade.utils.SharedPref
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
):ViewModel() {

    fun signInAsGuest(
        onSignInComplete: () -> Unit,
    ) {
        onSignInComplete()
    }



    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state



    fun registerUser(
        email: String,
        password: String,
    ) {
        loginUseCase(
            email,
            password,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.let { assignDate(it) }
                    _state.value = LoginState(registerMain = result.data)
                }

                is Resource.Error -> {
                    _state.value = LoginState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = LoginState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


}

fun assignDate(data: RegisterMain){
    SharedPref.expirationTime = data.expired_at?:0
    SharedPref.deviceToken = "Bearer ${data.token ?: ""}"
    SharedPref.loginTime = Date().time
    SharedPref.deviceLoggedIn = true
}