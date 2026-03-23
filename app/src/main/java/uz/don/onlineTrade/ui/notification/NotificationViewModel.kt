package uz.don.onlineTrade.ui.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.don.onlineTrade.data.remote.models.getNotifications.Content
import uz.don.onlineTrade.domain.NotificationsUseCase
import uz.don.onlineTrade.utils.pager.NotificationPaginator
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(

    private val notificationsUseCase: NotificationsUseCase
) : ViewModel() {


    fun resetPager() {
        paginator.reset()
        pagerState = NotificationScreenState()
    }

    var pagerState by mutableStateOf(NotificationScreenState())

    private var paginator = NotificationPaginator(
        initialKey = pagerState.page,
        onLoadUpdated = {
            pagerState = pagerState.copy(isLoading = it)
        },
        onRequest = { nextPage ->
            notificationsUseCase.getItems(
                page = nextPage,
                pageSize = 20,
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


    fun loadNextItems() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }


}

data class NotificationScreenState(
    val isLoading: Boolean = false,
    val items: List<Content> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)