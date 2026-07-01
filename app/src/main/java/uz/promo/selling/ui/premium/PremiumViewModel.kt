package uz.promo.selling.ui.premium

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.payments.PremiumOrderBody
import uz.promo.selling.data.remote.models.payments.PremiumPlan
import uz.promo.selling.utils.SharedPref
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val api: ApiInterface
) : ViewModel() {

    var plans by mutableStateOf<List<PremiumPlan>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var isOrdering by mutableStateOf(false)
        private set

    // Current membership status (from the profile).
    var premiumUntil by mutableStateOf<String?>(null)
        private set
    var boostCredits by mutableStateOf(0)
        private set

    val isPremium: Boolean
        get() = premiumUntil?.let {
            runCatching { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME).isAfter(LocalDateTime.now()) }
                .getOrDefault(false)
        } ?: false

    init {
        load()
    }

    fun load() {
        isLoading = true
        viewModelScope.launch {
            try {
                plans = api.getPremiumPlans().data.plans.sortedBy { it.termMonths }
            } catch (_: Exception) {
            }
            // Refresh membership status if logged in.
            if (SharedPref.deviceToken.isNotBlank()) {
                try {
                    val user = api.getProfile(SharedPref.deviceToken).data
                    premiumUntil = user.premiumUntil
                    boostCredits = user.boostCredits
                } catch (_: Exception) {
                }
            }
            isLoading = false
        }
    }

    /** Creates a premium order and returns the checkout URL, or null on failure. */
    fun createOrder(termMonths: Int, provider: String, onUrl: (String?) -> Unit) {
        isOrdering = true
        viewModelScope.launch {
            val url = try {
                val res = api.createPremiumOrder(PremiumOrderBody(termMonths, provider))
                if (res.success) res.data.paymentUrl else null
            } catch (_: Exception) {
                null
            }
            isOrdering = false
            onUrl(url)
        }
    }
}
