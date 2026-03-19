package org.don.onlineTrade.ui.home.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.don.onlineTrade.data.paging.SearchPagingSource
import org.don.onlineTrade.data.remote.ApiInterface
import org.don.onlineTrade.data.remote.models.category.CategoryParent
import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.ui.home.search.filter.FilterClass
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

data class SearchParams(
    val query: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val radius: Int = 0,
    val categoryIds: List<Long> = emptyList(),
    val startDate: String? = null,
    val endDate: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val apiInterface: ApiInterface
) : ViewModel() {

    var hasSearched by mutableStateOf(false)
        private set

    var filter by mutableStateOf(FilterClass())

    var searchText by mutableStateOf("")

    var categories by mutableStateOf<List<CategoryParent>>(emptyList())
        private set

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val result = apiInterface.getAllParentCategories(
                    SharedPref.deviceToken,
                    SharedPref.language
                )
                categories = result.sortedBy { it.position }
            } catch (_: Exception) { }
        }
    }

    private val _searchParams = MutableStateFlow<SearchParams?>(null)

    val searchResults: Flow<PagingData<Content>> = _searchParams
        .flatMapLatest { params ->
            if (params == null) {
                flowOf(PagingData.empty())
            } else {
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        SearchPagingSource(
                            apiInterface = apiInterface,
                            query = params.query,
                            lat = params.lat,
                            lon = params.lon,
                            radius = params.radius,
                            categoryIds = params.categoryIds,
                            startDate = params.startDate,
                            endDate = params.endDate,
                        )
                    }
                ).flow
            }
        }.cachedIn(viewModelScope)

    fun search(params: SearchParams) {
        hasSearched = true
        _searchParams.value = params
    }
}
