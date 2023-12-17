package org.don.onlineTrade.ui.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.GetProfileUseCase
import org.don.onlineTrade.ui.home.GetProfileState
import org.don.onlineTrade.ui.home.HomeScreenState
import org.don.onlineTrade.ui.home.TOKEN
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
): ViewModel() {



    private val _state = mutableStateOf(GetProfileState())
    val state: State<GetProfileState> = _state

    fun refresh(){
        getProfile(
            token = SharedPref.deviceToken,
        )
    }

    init {
        getProfile(
            token = SharedPref.deviceToken,
        )
    }

    private fun getProfile(
        token: String,
    ) {
        getProfileUseCase(
            token,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = GetProfileState(getProfile = result.data)
                }

                is Resource.Error -> {
                    _state.value = GetProfileState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = GetProfileState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

}