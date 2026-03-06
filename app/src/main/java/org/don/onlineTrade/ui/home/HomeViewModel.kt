package org.don.onlineTrade.ui.home

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.domain.repository.LocationTracker
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.MyPostsUseCase
import org.don.onlineTrade.domain.useCase.NearPostsUseCase
import org.don.onlineTrade.domain.useCase.ProductsPagerUseCase
import org.don.onlineTrade.domain.useCase.allCategoriesUseCase.AllCategoriesUseCase
import org.don.onlineTrade.utils.SharedPref
import org.don.onlineTrade.utils.pager.DefaultPaginator
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryUseCase: AllCategoriesUseCase,
    private val productsPagerUseCase: ProductsPagerUseCase,
    private val myPostsUseCase: MyPostsUseCase,
    private val locationTrackerRepository: LocationTracker,
    private val nearPostsUseCase: NearPostsUseCase
) :
    ViewModel() {


    private val _state = mutableStateOf(HomeScreenState())
    val state: State<HomeScreenState> = _state


    fun getAllCategories(
        token: String = SharedPref.deviceToken,
        language: String = SharedPref.language,
    ) {
        categoryUseCase(
            token,
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = HomeScreenState(categoryList = result.data)
                }

                is Resource.Error -> {
                    _state.value = HomeScreenState(
                        error = result.message ?: "An unexpected error occured"
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
        token: String = SharedPref.deviceToken,
        language: String = SharedPref.language,
    ) {
        categoryUseCase.parentCategories(
            token,
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data
                    data?.sortBy { it.position }
                    _state.value = HomeScreenState(parentCategoryList = data)
                }

                is Resource.Error -> {
                    _state.value = HomeScreenState(
                        error = result.message ?: "An unexpected error occured"
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



    fun resetPager() {
        paginator.reset()
        pagerState = ScreenState()
    }

    var pagerState by mutableStateOf(ScreenState())

    private var paginator = DefaultPaginator(
        initialKey = pagerState.page,
        onLoadUpdated = {
            pagerState = pagerState.copy(isLoading = it)
        },
        onRequest = { nextPage,
                      query: String?,
                      categoryId: Int?,
                      minPrice: Int?,
                      maxPrice: Int?,
                      startDate: String?,
                      endDate: String?,
                      regionId: Int,
                      districtId: Int,
                      isMyPosts: Boolean ->
            if (isMyPosts) {
                myPostsUseCase.getItems(
                    page = nextPage,
                    pageSize = 20,
                )
            } else {
                productsPagerUseCase.getItems(
                    page = nextPage,
                    pageSize = 20,
                    categoryId = categoryId,
                    query = query,
                    startDate = startDate,
                    endDate = endDate,
                    regionId = regionId,
                    districtId = districtId,
                    fromPrice = minPrice,
                    toPrice = maxPrice
                    )
            }
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


    fun loadNextItems(
        query: String? = null,
        categoryId: Int? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        startDate: String? = null,
        endDate: String? = null,
        regionId: Int = -1,
        districtId: Int = -1,
        isMyPosts: Boolean = false
    ) {
        viewModelScope.launch {
            paginator.loadNextItems(
                query = query,
                categoryId = categoryId,
                minPrice = minPrice,
                maxPrice = maxPrice,
                isMyPosts = isMyPosts,
                startDate = startDate,
                endDate = endDate,
                regionId = regionId,
                districtId = districtId
            )
        }
    }


    fun locationObserve() = viewModelScope.launch {
        locationTrackerRepository.getCurrentLocation().collectLatest {
            stopLocationUpdates()
            getNearPosts(lat = it.latitude, lon = it.longitude)
            //_state.value = _state.value.copy(getLocation = true)
        }
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
        token: String = SharedPref.deviceToken,
        language: String = SharedPref.language,
        lat: Double,
        lon: Double
    ) {
        nearPostsUseCase(
            token,
            lat,
            lon,
            language,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _stateNear.value = HomeScreenState2(getNearPost = result.data)
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

data class ScreenState(
    val isLoading: Boolean = false,
    val items: List<Content> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)