package uz.don.selling.ui.auth.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.don.selling.data.remote.models.RegistrationBody
import uz.don.selling.domain.state.Resource
import uz.don.selling.domain.useCase.auth.registrationUseCase.RegistrationUseCase
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val registrationUseCase: RegistrationUseCase
) : ViewModel() {



    private val _state = mutableStateOf(RegistrationState())
    val state: State<RegistrationState> = _state


    fun registerUser(
        registrationBody: RegistrationBody
    ) {
        registrationUseCase(
            registrationBody
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = RegistrationState(
                        registerMain = result.data,
                        email = registrationBody.email
                    )
                }

                is Resource.Error -> {
                    _state.value = RegistrationState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = RegistrationState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}