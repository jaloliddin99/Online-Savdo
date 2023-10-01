package org.don.onlineTrade.ui.detailsPage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.presentUseCase.PresentProductUseCase
import org.don.onlineTrade.ui.home.PresentProductState
import javax.inject.Inject

@HiltViewModel
class PresentViewModel @Inject constructor(
    private val presentProductUseCase: PresentProductUseCase
): ViewModel() {

    private val _state = mutableStateOf(PresentProductState())
    val state: State<PresentProductState> = _state

    fun getProductDetail(
        id: Int,
        token: String,
        language: String
    ) {
        presentProductUseCase(
            id,
            token,
            language
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = PresentProductState(registerMain = result.data)
                }

                is Resource.Error -> {
                    _state.value = PresentProductState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = PresentProductState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


}