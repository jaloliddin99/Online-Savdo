package org.don.onlineTrade.ui.auth.forgotPassword

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.don.onlineTrade.data.remote.models.LoginBody
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.auth.ForgotPasswordUseCase
import org.don.onlineTrade.domain.useCase.auth.ResetNewPasswordUseCase
import org.don.onlineTrade.ui.auth.login.LoginState
import org.don.onlineTrade.ui.home.ForgotPasswordState
import org.don.onlineTrade.ui.home.ResetNewPasswordState
import javax.inject.Inject

@HiltViewModel
class FPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetNewPasswordUseCase: ResetNewPasswordUseCase
):ViewModel() {

    private val _state = mutableStateOf(ForgotPasswordState())
    val state: State<ForgotPasswordState> = _state

    fun forgotPassword(
        email: String
    ) {
        forgotPasswordUseCase(
            email
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data?.success == true){
                        _state.value = ForgotPasswordState(main = result.data)
                    }else {
                        _state.value = ForgotPasswordState(
                            error = result.message ?: "An unexpected error occured"
                        )
                    }
                }

                is Resource.Error -> {
                    _state.value = ForgotPasswordState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = ForgotPasswordState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


    private val _stateR = mutableStateOf(ResetNewPasswordState())
    val stateR: State<ResetNewPasswordState> = _stateR

    fun resetNewPassword(
        email: String, code: Int, password: String
    ) {
        resetNewPasswordUseCase(
            email, code, password
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _stateR.value = ResetNewPasswordState(main = result.data)
                }

                is Resource.Error -> {
                    _stateR.value = ResetNewPasswordState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _stateR.value = ResetNewPasswordState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

}