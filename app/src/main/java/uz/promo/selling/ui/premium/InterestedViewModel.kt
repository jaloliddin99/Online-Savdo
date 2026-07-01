package uz.promo.selling.ui.premium

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.payments.InterestedUser
import javax.inject.Inject

@HiltViewModel
class InterestedViewModel @Inject constructor(
    private val api: ApiInterface
) : ViewModel() {

    var users by mutableStateOf<List<InterestedUser>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var premiumRequired by mutableStateOf(false)
        private set

    fun load(postId: Long) {
        isLoading = true
        viewModelScope.launch {
            try {
                val res = api.getInterested(postId)
                if (res.success) {
                    users = res.data
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
