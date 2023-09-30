package org.don.onlineTrade.ui.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.allCategoriesUseCase.AllCategoriesUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryUseCase: AllCategoriesUseCase,
    private val networkRepository: NetworkRepository
) :
    ViewModel() {


    fun collectProducts(
        categoryId: Int?=null
    ): Flow<PagingData<Data>> = networkRepository.getPublicProducts(
        token = TOKEN,
        query = null,
        categoryId = categoryId,
        language = "uz",
        minPrice = null,
        maxPrice = null
    ).cachedIn(viewModelScope)


    private val _state = mutableStateOf(HomeScreenState())
    val state: State<HomeScreenState> = _state

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
        categoryUseCase(
            token,
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = HomeScreenState(registerMain = result.data)
                }

                is Resource.Error -> {
                    _state.value = HomeScreenState(
                        error = result.message ?: "An unexpected error occured"
                    )
                }

                is Resource.Loading -> {
                    _state.value = HomeScreenState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }




}

const val TOKEN = "qwertyuiopasdfghjklzxcvbnm123456"