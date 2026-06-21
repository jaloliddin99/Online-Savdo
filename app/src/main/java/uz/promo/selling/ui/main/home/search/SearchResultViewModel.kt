package uz.promo.selling.ui.main.home.search

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
import uz.promo.selling.data.paging.SearchPagingSource
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.ai.ParsedSearchDTO
import uz.promo.selling.data.remote.models.category.Category
import uz.promo.selling.data.remote.models.category.CategoryParent
import uz.promo.selling.data.remote.models.getPublicProducts.Content
import uz.promo.selling.ui.main.home.search.filter.FilterClass
import uz.promo.selling.utils.SharedPref
import javax.inject.Inject

data class SearchParams(
    val query: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val radius: Int = 0,
    val categoryIds: List<Long> = emptyList(),
    val startDate: String? = null,
    val endDate: String? = null,
    val priceMin: Int? = null,
    val priceMax: Int? = null,
    val sort: String? = null,
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

    var allCategories by mutableStateOf<Category?>(null)
        private set

    var isCategoriesLoading by mutableStateOf(false)
        private set

    var isAiSearching by mutableStateOf(false)
        private set

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val result = apiInterface.getAllParentCategories(
                    SharedPref.language
                )
                categories = result.sortedBy { it.position }
            } catch (_: Exception) { }
        }
    }

    fun loadAllCategories() {
        if (allCategories != null) return
        isCategoriesLoading = true
        viewModelScope.launch {
            try {
                allCategories = apiInterface.getAllCategories(
                    SharedPref.language
                )
            } catch (_: Exception) { }
            isCategoriesLoading = false
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
                        // Offset-based backend: keep size constant across loads so page
                        // offsets don't overlap and trigger runaway appends. See HomeViewModel.
                        initialLoadSize = 20,
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
                            priceMin = params.priceMin,
                            priceMax = params.priceMax,
                            sort = params.sort,
                        )
                    }
                ).flow
            }
        }.cachedIn(viewModelScope)

    fun search(params: SearchParams) {
        hasSearched = true
        _searchParams.value = params
    }

    /**
     * Calls GET /ai/search ONCE (page=0,size=1) only to read the parsed filters.
     * The 'results' field is ignored — the caller drives the existing paging
     * search with these filters. Delivers null on any error / disabled response.
     */
    fun aiSearch(
        query: String,
        lat: Double,
        lon: Double,
        radius: Int,
        onResult: (ParsedSearchDTO?) -> Unit
    ) {
        viewModelScope.launch {
            isAiSearching = true
            try {
                val response = apiInterface.aiSearch(
                    query = query,
                    lat = lat,
                    lon = lon,
                    radius = radius
                )
                onResult(response.data.parsed)
            } catch (_: Exception) {
                onResult(null)
            }
            isAiSearching = false
        }
    }
}
