package uz.promo.selling.ui.main.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.utils.SharedPref
import javax.inject.Inject

/**
 * Chat/search/my-ads preferences live on the server (the backend checks them
 * before pushing). News is an FCM topic broadcast, so opting out happens on
 * the device via topic unsubscription.
 */
@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val api: ApiInterface
) : ViewModel() {

    var chat by mutableStateOf(true)
        private set
    var search by mutableStateOf(true)
        private set
    var myAds by mutableStateOf(true)
        private set
    var news by mutableStateOf(SharedPref.newsNotifications)
        private set
    var isLoading by mutableStateOf(false)
        private set

    init {
        load()
    }

    private fun load() {
        if (SharedPref.deviceToken.isBlank()) return
        isLoading = true
        viewModelScope.launch {
            try {
                val res = api.getNotificationPrefs(SharedPref.deviceToken)
                if (res.success) {
                    chat = res.data.chat
                    search = res.data.search
                    myAds = res.data.myAds
                }
            } catch (_: Exception) {
            }
            isLoading = false
        }
    }

    fun setPref(key: String, value: Boolean) {
        when (key) {
            "chat" -> chat = value
            "search" -> search = value
            "myAds" -> myAds = value
        }
        viewModelScope.launch {
            try {
                api.updateNotificationPrefs(SharedPref.deviceToken, mapOf(key to value))
            } catch (_: Exception) {
                // Revert on failure so the UI stays truthful.
                when (key) {
                    "chat" -> chat = !value
                    "search" -> search = !value
                    "myAds" -> myAds = !value
                }
            }
        }
    }

    fun setNewsEnabled(enabled: Boolean) {
        news = enabled
        SharedPref.newsNotifications = enabled
        if (enabled) {
            FirebaseMessaging.getInstance().subscribeToTopic("all")
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
        }
    }
}
