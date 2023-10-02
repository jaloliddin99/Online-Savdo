package org.don.onlineTrade.ui.home

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.domain.repository.NetworkRepository
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.ProductsPagerUseCase
import org.don.onlineTrade.domain.useCase.allCategoriesUseCase.AllCategoriesUseCase
import org.don.onlineTrade.utils.SharedPref
import org.don.onlineTrade.utils.pager.DefaultPaginator
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryUseCase: AllCategoriesUseCase,
    private val networkRepository: NetworkRepository,
    private val productsPagerUseCase: ProductsPagerUseCase
) :
    ViewModel() {

    fun collectProducts(
        categoryId: Int? = null,
        query: String? = null
    ): Flow<PagingData<Data>> = networkRepository.getPublicProducts(
        token = TOKEN,
        query = query,
        categoryId = categoryId,
        language = SharedPref.language,
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


    var pagerState by mutableStateOf(ScreenState())

    private val paginator = DefaultPaginator(
        initialKey = pagerState.page,
        onLoadUpdated = {
            pagerState = pagerState.copy(isLoading = it)
        },
        onRequest = { nextPage,
                      query: String?,
                      categoryId: Int?,
                      minPrice: Int?,
                      maxPrice: Int? ->
            productsPagerUseCase.getItems(
                page = nextPage,
                pageSize = 15,
                query = query,
                categoryId = categoryId,
                minPrice = minPrice,
                maxPrice = maxPrice
            )
        },
        getNextKey = {
            pagerState.page + 1
        },
        onError = {
            pagerState = pagerState.copy(error = it?.localizedMessage)
        },
        onSuccess = { items, newKey ->
            pagerState = pagerState.copy(
                items = pagerState.items + items,
                page = newKey,
                endReached = items.isEmpty()
            )
        }
    )

    init {
        loadNextItems()
    }

    fun loadNextItems(
        query: String? = null,
        categoryId: Int? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null
    ) {
        viewModelScope.launch {
            paginator.loadNextItems(
                query = query,
                categoryId = categoryId,
                minPrice = minPrice,
                maxPrice = maxPrice
            )
        }
    }


}

data class ScreenState(
    val isLoading: Boolean = false,
    val items: List<Data> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)

const val TOKEN = "qwertyuiopasdfghjklzxcvbnm123456"