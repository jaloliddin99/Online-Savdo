package uz.don.onlineTrade.ui.main.myPosts

import androidx.compose.runtime.State
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.don.onlineTrade.data.paging.MyPostsPagingSource
import uz.don.onlineTrade.data.remote.ApiInterface
import uz.don.onlineTrade.data.remote.models.getPublicProducts.Content
import uz.don.onlineTrade.domain.state.Resource
import uz.don.onlineTrade.domain.useCase.postNewProduct.PrioritizePostUseCase
import uz.don.onlineTrade.ui.main.home.MyProfileScreen
import uz.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@HiltViewModel
class MyPostViewModel @Inject constructor(
    private val prioritizePostUseCase: PrioritizePostUseCase,
    private val apiInterface: ApiInterface
) : ViewModel() {

    private val _state = mutableStateOf(MyProfileScreen())
    val state: State<MyProfileScreen> = _state

    private val _selectedStatus = MutableStateFlow<Int?>(null)
    val selectedStatus: StateFlow<Int?> = _selectedStatus.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val myPostsFlow: Flow<PagingData<Content>> = _selectedStatus
        .flatMapLatest { status ->
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { MyPostsPagingSource(apiInterface, status) }
            ).flow
        }.cachedIn(viewModelScope)

    fun setStatusFilter(status: Int?) {
        _selectedStatus.value = status
    }

    fun updateValues(
        token: String = SharedPref.deviceToken,
        postId: Long,
        period: Int
    ) {
        prioritizePostUseCase(
            token, postId, period
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = MyProfileScreen(getProfile = result.data)
                }
                is Resource.Error -> {
                    _state.value = MyProfileScreen(
                        error = result.message ?: "An unexpected error occured"
                    )
                }
                is Resource.Loading -> {
                    _state.value = MyProfileScreen(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}
