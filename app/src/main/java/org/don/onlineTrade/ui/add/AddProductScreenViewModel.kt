package org.don.onlineTrade.ui.add

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.currencies.CurrenciesUseCase
import org.don.onlineTrade.ui.home.AddProductScreenState
import org.don.onlineTrade.ui.home.RegionsScreenState
import org.don.onlineTrade.ui.home.TOKEN
import javax.inject.Inject

@HiltViewModel
class AddProductScreenViewModel @Inject constructor(
    private val currencyUseCase: CurrenciesUseCase
): ViewModel() {



    private val _state = mutableStateOf(AddProductScreenState())
    val state: State<AddProductScreenState> = _state

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
        currencyUseCase(
            token,
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = AddProductScreenState(regions = result.data)
                }

                is Resource.Error -> {
                    _state.value = AddProductScreenState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = AddProductScreenState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


}