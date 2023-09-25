package org.don.onlineTrade.ui.region

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.regionUseCase.RegionsUseCase
import org.don.onlineTrade.ui.home.HomeScreenState
import org.don.onlineTrade.ui.home.RegionsScreenState
import org.don.onlineTrade.ui.home.TOKEN
import javax.inject.Inject

@HiltViewModel
class RegionsViewModel @Inject constructor(
    private val regionsUseCase: RegionsUseCase
): ViewModel() {


    private val _state = mutableStateOf(RegionsScreenState())
    val state: State<RegionsScreenState> = _state

    init {
        getAllCategories(
            token = TOKEN,
            language = "uz"
        )
    }

    private fun getAllCategories(
        token: String,
        language: String,
    ) {
        regionsUseCase(
            token,
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = RegionsScreenState(regions = result.data)
                }

                is Resource.Error -> {
                    _state.value = RegionsScreenState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = RegionsScreenState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


}