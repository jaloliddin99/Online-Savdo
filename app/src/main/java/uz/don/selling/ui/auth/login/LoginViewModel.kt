package uz.don.selling.ui.auth.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.don.selling.data.remote.models.LoginBody
import uz.don.selling.domain.state.Resource
import uz.don.selling.domain.useCase.auth.loginUseCase.LoginUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
):ViewModel() {


    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    fun registerUser(
        loginBody: LoginBody
    ) {
        loginUseCase(
           loginBody
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data?.success == true){
                        _state.value = LoginState(registerMain = result.data,
                            email = loginBody.email)
                    }else {
                        _state.value = LoginState(
                            error = result.message ?: "An unexpected error occured"
                        )
                    }
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
