package uz.promo.selling.ui.premium

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.payments.PostAnalyticsData
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val api: ApiInterface
) : ViewModel() {

    var data by mutableStateOf<PostAnalyticsData?>(null)
        private set
    var isLoading by mutableStateOf(true)
        private set
    // True when the backend rejected the request because the user isn't premium.
    var premiumRequired by mutableStateOf(false)
        private set

    init { load() }

    fun load() {
        isLoading = true
        viewModelScope.launch {
            try {
                val res = api.getPostAnalytics()
                if (res.success) {
                    data = res.data
                    premiumRequired = false
                } else {
                    premiumRequired = true
                }
            } catch (_: Exception) {
                premiumRequired = true
            }
            isLoading = false
        }
    }
}
