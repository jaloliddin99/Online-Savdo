package uz.promo.selling.ui.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.inbox.UserNotificationItem
import uz.promo.selling.utils.SharedPref
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val api: ApiInterface
) : ViewModel() {

    var items by mutableStateOf<List<UserNotificationItem>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var endReached by mutableStateOf(false)
        private set
    var loaded by mutableStateOf(false)
        private set

    private var page = 0

    fun loadNext() {
        if (isLoading || endReached || SharedPref.deviceToken.isBlank()) return
        isLoading = true
        viewModelScope.launch {
            try {
                val res = api.getUserNotifications(SharedPref.deviceToken, page)
                if (res.success) {
                    items = items + res.data.content
                    endReached = res.data.last
                    page++
                }
            } catch (_: Exception) {
                endReached = true
            }
            isLoading = false
            loaded = true
        }
    }

    /** Opening the tab clears the unread badge. */
    fun markAllRead() {
        if (SharedPref.deviceToken.isBlank()) return
        viewModelScope.launch {
            try {
                api.markNotificationsRead(SharedPref.deviceToken)
            } catch (_: Exception) {
            }
        }
    }
}
