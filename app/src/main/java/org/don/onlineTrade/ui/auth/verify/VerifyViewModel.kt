package org.don.onlineTrade.ui.auth.verify

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.auth.verify.VerifyUseCase
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val verifyUseCase: VerifyUseCase,
) : ViewModel(){



    private val _state = mutableStateOf(VerificationState())
    val state: State<VerificationState> = _state



    fun verify(
        code: Int,
        email: String
    ) {
        verifyUseCase(
            code, email
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = VerificationState(registerMain = result.data)
                }

                is Resource.Error -> {
                    _state.value = VerificationState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = VerificationState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}