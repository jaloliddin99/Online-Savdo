package uz.promo.selling.ui.main.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.promo.selling.data.paging.ProductsPagingSource
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.getPublicProducts.Content
import uz.promo.selling.domain.repository.LocationTracker
import uz.promo.selling.domain.state.Resource
import uz.promo.selling.domain.useCase.NearPostsUseCase
import uz.promo.selling.domain.useCase.allCategoriesUseCase.AllCategoriesUseCase
import uz.promo.selling.domain.useCase.presentUseCase.LikeDislikeUseCase
import uz.promo.selling.utils.SharedPref
import javax.inject.Inject

data class ProductsParams(
    val categoryId: Int? = null,
    val query: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val fromPrice: Int? = null,
    val toPrice: Int? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val radius: Int? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryUseCase: AllCategoriesUseCase,
    private val apiInterface: ApiInterface,
    private val locationTrackerRepository: LocationTracker,
    private val nearPostsUseCase: NearPostsUseCase,
    private val likeDislikeUseCase: LikeDislikeUseCase
) :
    ViewModel() {

    private val _state = mutableStateOf(HomeScreenState())
    val state: State<HomeScreenState> = _state

    // Optimistic like state for home cards. The public list API doesn't include
    // the caller's like state, so this tracks toggles made in this session and
    // syncs each entry with the server's authoritative answer when it arrives.
    val likedPosts = mutableStateMapOf<Int, Boolean>()

    /** Flips the like optimistically, fires the toggle call, returns the new state. */
    fun toggleLike(postId: Int): Boolean {
        val newValue = !(likedPosts[postId] ?: false)
        likedPosts[postId] = newValue
        likeDislikeUseCase(
            id = postId,
            language = SharedPref.language,
            token = SharedPref.deviceToken
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    result.data?.data?.isLiked?.let { likedPosts[postId] = it }
                }
                is Resource.Error -> {
                    // Server rejected it — revert the optimistic flip.
                    likedPosts[postId] = !newValue
                }
                is Resource.Loading -> Unit
            }
        }.launchIn(viewModelScope)
        return newValue
    }

    fun getAllCategories(
        language: String = SharedPref.language,
    ) {
        categoryUseCase(
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(categoryList = result.data, isLoading = false, error = "")
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false
                    )
                }

                is Resource.Loading -> {
                    if (_state.value.categoryList == null) {
                        _state.value = _state.value.copy(isLoading = true, error = "")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getAllParentCategories(
        language: String = SharedPref.language,
    ) {
        categoryUseCase.parentCategories(
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    data?.sortBy { it.position }
                    _state.value = _state.value.copy(parentCategoryList = data, isLoading = false, error = "")
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false
                    )
                }

                is Resource.Loading -> {
                    if (_state.value.parentCategoryList == null) {
                        _state.value = _state.value.copy(isLoading = true, error = "")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }


    private val _productsParams = MutableStateFlow(ProductsParams())

    val productsFlow: Flow<PagingData<Content>> = _productsParams
        .flatMapLatest { params ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    // The backend is offset-based (offset = page * size). Paging 3's default
                    // initialLoadSize is 3 * pageSize (60), so the first request asks for
                    // size=60 while every append asks for size=20 — the offsets then overlap
                    // (page=1&size=20 -> offset 20, already inside the first page), producing
                    // duplicate items. The grid never fills with new content, so it keeps
                    // firing append after append (page 6, 7, 8, ...) without the user scrolling.
                    // Keeping initialLoadSize == pageSize makes size constant across all loads.
                    initialLoadSize = 20,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    ProductsPagingSource(
                        apiInterface = apiInterface,
                        categoryId = params.categoryId,
                        query = params.query,
                        startDate = params.startDate,
                        endDate = params.endDate,
                        fromPrice = params.fromPrice,
                        toPrice = params.toPrice,
                        lat = params.lat,
                        lon = params.lon,
                        radius = params.radius,
                    )
                }
            ).flow
        }.cachedIn(viewModelScope)

    init {
        getAllParentCategories()
    }


    fun locationObserve() = viewModelScope.launch {
        locationTrackerRepository.getCurrentLocation().collectLatest {
            stopLocationUpdates()
            getNearPosts(lat = it.latitude, lon = it.longitude)
        }
    }

    // Coordinates the last "near you" fetch used, so repeated resumes don't refetch.
    private var lastNearKey: String? = null

    /**
     * Loads "near you" for the location the user picked on the map. No-op until a
     * location has been picked (the device-location flow handles that case) and
     * skips refetching when the picked location hasn't changed. Safe to call on
     * every Home resume.
     */
    fun refreshNearPostsForSelectedLocation() {
        if (!SharedPref.hasPickedLocation) return
        val lat = SharedPref.latitude.toDoubleOrNull() ?: return
        val lon = SharedPref.longitude.toDoubleOrNull() ?: return
        val key = "$lat,$lon"
        if (key == lastNearKey) return
        lastNearKey = key
        stopLocationUpdates()
        getNearPosts(lat = lat, lon = lon)
    }

    fun startLocationUpdates() {
        locationTrackerRepository.startLocationUpdate()
    }

    private fun stopLocationUpdates() {
        locationTrackerRepository.stopLocationUpdate()
    }

    private val _stateNear = mutableStateOf(HomeScreenState2())
    val stateNear: State<HomeScreenState2> = _stateNear
    private fun getNearPosts(
        language: String = SharedPref.language,
        lat: Double,
        lon: Double
    ) {
        nearPostsUseCase(
            lat,
            lon,
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _stateNear.value = HomeScreenState2(getNearPost = result.data?.data)
                }

                is Resource.Error -> {
                    _stateNear.value = HomeScreenState2(error = result.message.toString())
                }

                is Resource.Loading -> {
                    _stateNear.value = _stateNear.value.copy(isLoading = true, error = "")
                }
            }
        }.launchIn(viewModelScope)
    }


}

