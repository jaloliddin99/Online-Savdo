package org.don.onlineTrade.ui.saved

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.GetLikedProductUseCase
import org.don.onlineTrade.ui.home.HomeScreenState
import org.don.onlineTrade.ui.home.LikedProductsState
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val likedProductUseCase: GetLikedProductUseCase
): ViewModel() {


    private val _state = mutableStateOf(LikedProductsState())
    val state: State<LikedProductsState> = _state


    init {
        getAllCategories(
            token = SharedPref.deviceToken,
            language = SharedPref.language
        )
    }

    private fun getAllCategories(
        token: String,
        language: String,
    ) {
        likedProductUseCase(
            token,
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = LikedProductsState(registerMain = result.data)
                }

                is Resource.Error -> {
                    _state.value = LikedProductsState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = LikedProductsState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }


}