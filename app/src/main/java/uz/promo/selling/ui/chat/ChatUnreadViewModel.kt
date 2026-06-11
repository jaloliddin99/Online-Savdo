package uz.promo.selling.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.utils.SharedPref
import javax.inject.Inject

/** Provides the total unread message count for the home toolbar badge. */
@HiltViewModel
class ChatUnreadViewModel @Inject constructor(
    private val api: ApiInterface
) : ViewModel() {

    var count by mutableIntStateOf(0)
        private set

    fun refresh() {
        if (SharedPref.deviceToken.isBlank()) {
            count = 0
            return
        }
        viewModelScope.launch {
            try {
                val res = api.getChatUnreadCount()
                if (res.success) count = res.data.count.toInt()
            } catch (_: Exception) {
            }
        }
    }
}
