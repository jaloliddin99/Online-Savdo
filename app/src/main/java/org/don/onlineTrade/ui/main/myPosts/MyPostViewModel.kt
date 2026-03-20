package org.don.onlineTrade.ui.main.myPosts

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.don.onlineTrade.data.remote.models.getPublicProducts.Content
import org.don.onlineTrade.domain.state.Resource
import org.don.onlineTrade.domain.useCase.MyPostsUseCase
import org.don.onlineTrade.domain.useCase.postNewProduct.PrioritizePostUseCase
import org.don.onlineTrade.ui.main.home.MyProfileScreen
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

data class MyPostsScreenState(
    val isLoading: Boolean = false,
    val items: List<Content> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)

@HiltViewModel
class MyPostViewModel @Inject constructor(
    private val prioritizePostUseCase: PrioritizePostUseCase,
    private val myPostsUseCase: MyPostsUseCase
) : ViewModel() {

    private val _state = mutableStateOf(MyProfileScreen())
    val state: State<MyProfileScreen> = _state

    var pagerState by mutableStateOf(MyPostsScreenState())
        private set

    private var isMakingRequest = false

    init {
        loadNextItems()
    }

    fun loadNextItems() {
        if (isMakingRequest || pagerState.endReached) return
        isMakingRequest = true
        pagerState = pagerState.copy(isLoading = true)

        viewModelScope.launch {
            val result = runCatching {
                myPostsUseCase.getItems(
                    page = pagerState.page,
                    pageSize = 20
                ).getOrThrow()
            }

            result.onSuccess { items ->
                pagerState = pagerState.copy(
                    items = pagerState.items + items,
                    page = pagerState.page + 1,
                    endReached = items.isEmpty(),
                    isLoading = false,
                    error = null
                )
            }.onFailure { e ->
                pagerState = pagerState.copy(
                    isLoading = false,
                    error = e.localizedMessage
                )
            }
            isMakingRequest = false
        }
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
