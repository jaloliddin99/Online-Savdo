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

/** What the AI understood for a query, plus the query to actually run. */
data class AiSearchResolution(
    val parsed: ParsedSearchDTO,
    /** The AI's keywords, or "" when they matched nothing and were dropped. */
    val effectiveQuery: String,
    val categoryIds: List<Long>,
    val priceMin: Int?,
    val priceMax: Int?,
    val sort: String?,
    val droppedKeywords: Boolean,
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

    // --- Map mode: all found posts (paging is bypassed; capped for performance) ---
    var mapPosts by mutableStateOf<List<Content>>(emptyList())
        private set
    var isMapLoading by mutableStateOf(false)
        private set
    // The params of the most recent search, so map mode can refetch the same query.
    var currentParams by mutableStateOf<SearchParams?>(null)
        private set
    private var mapLoadedParams: SearchParams? = null

    init {
        loadCategories()
    }

    /**
     * Fetches every matching post (up to [MAP_POST_CAP]) for the map, since the
     * clustered map can't drive Paging. Re-fetches only when the query changed.
     */
    fun loadMapPosts(params: SearchParams) {
        if (params == mapLoadedParams && mapPosts.isNotEmpty()) return
        isMapLoading = true
        viewModelScope.launch {
            val all = mutableListOf<Content>()
            var page = 0
            try {
                while (all.size < MAP_POST_CAP) {
                    val data = apiInterface.searchPosts(
                        lang = SharedPref.language,
                        page = page,
                        size = MAP_PAGE_SIZE,
                        query = params.query,
                        lat = params.lat,
                        lon = params.lon,
                        radius = params.radius,
                        categoryIds = params.categoryIds.ifEmpty { null },
                        startDate = params.startDate,
                        endDate = params.endDate,
                        priceMin = params.priceMin,
                        priceMax = params.priceMax,
                        sort = params.sort,
                    ).data
                    all.addAll(data.content)
                    if (data.last || data.content.size < MAP_PAGE_SIZE) break
                    page++
                }
            } catch (_: Exception) {
            }
            // Only posts with coordinates can be plotted.
            mapPosts = all.asSequence()
                .filter { it.latitude != null && it.longitude != null }
                .take(MAP_POST_CAP)
                .toList()
            mapLoadedParams = params
            isMapLoading = false
        }
    }

    companion object {
        private const val MAP_POST_CAP = 500
        private const val MAP_PAGE_SIZE = 100
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
        currentParams = params
        _searchParams.value = params
    }

    /**
     * Parses [query] into structured filters (category/price/sort). If the AI's
     * keywords match nothing but a category or price was understood, the keywords
     * are dropped so the caller can search by category+price instead of dead-ending.
     * Returns null only when the AI parse itself fails.
     */
    suspend fun resolveAiSearch(
        query: String,
        lat: Double,
        lon: Double,
        radius: Int,
        baseCategoryIds: List<Long>,
        basePriceMin: Int?,
        basePriceMax: Int?,
        baseSort: String?,
    ): AiSearchResolution? {
        isAiSearching = true
        try {
            val parsed = try {
                apiInterface.aiSearch(query = query, lat = lat, lon = lon, radius = radius).data.parsed
            } catch (_: Exception) {
                null
            } ?: return null

            val keywords = parsed.keywords?.takeIf { it.isNotBlank() } ?: query
            val categoryIds = parsed.categoryId?.let { listOf(it) } ?: baseCategoryIds
            val priceMin = parsed.priceMin?.toInt() ?: basePriceMin
            val priceMax = parsed.priceMax?.toInt() ?: basePriceMax
            val sort = parsed.sort ?: baseSort
            val hasConstraints = categoryIds.isNotEmpty() || priceMin != null || priceMax != null

            // Probe page 0 with the keywords; if nothing matches but we have a
            // category/price, drop the keywords and search by those instead.
            var effectiveQuery = keywords
            var dropped = false
            if (keywords.isNotBlank() && hasConstraints) {
                val probeEmpty = try {
                    apiInterface.searchPosts(
                        lang = SharedPref.language, page = 0, size = 1, query = keywords,
                        lat = lat, lon = lon, radius = radius,
                        categoryIds = categoryIds.ifEmpty { null },
                        startDate = null, endDate = null,
                        priceMin = priceMin, priceMax = priceMax, sort = sort,
                    ).data.content.isEmpty()
                } catch (_: Exception) {
                    false
                }
                if (probeEmpty) {
                    effectiveQuery = ""
                    dropped = true
                }
            }

            return AiSearchResolution(parsed, effectiveQuery, categoryIds, priceMin, priceMax, sort, dropped)
        } finally {
            isAiSearching = false
        }
    }
}
