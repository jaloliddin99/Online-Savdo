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

    // Applied promo code (validated against the backend) and its percent.
    var promoCode by mutableStateOf<String?>(null)
        private set
    var promoPercent by mutableStateOf(0)
        private set
    var promoChecking by mutableStateOf(false)
        private set

    /** Validates a promo code; onError gets the server message (or null on network failure). */
    fun applyPromo(code: String, termMonths: Int?, onError: (String?) -> Unit) {
        if (code.isBlank() || promoChecking) return
        viewModelScope.launch {
            promoChecking = true
            try {
                val res = api.validatePromo(
                    uz.promo.selling.data.remote.models.payments.PromoValidateBody(
                        code = code, type = "premium", termMonths = termMonths
                    )
                )
                if (res.success) {
                    promoCode = res.data.code
                    promoPercent = res.data.percent
                } else {
                    clearPromo()
                    onError(res.message)
                }
            } catch (_: Exception) {
                clearPromo()
                onError(null)
            }
            promoChecking = false
        }
    }

    fun clearPromo() {
        promoCode = null
        promoPercent = 0
    }

    /** Creates a premium order; onResult gets the checkout URL or (null + server error message). */
    fun createOrder(termMonths: Int, provider: String, onResult: (String?, String?) -> Unit) {
        isOrdering = true
        viewModelScope.launch {
            var errorMsg: String? = null
            val url = try {
                val res = api.createPremiumOrder(PremiumOrderBody(termMonths, provider, promoCode))
                if (res.success) {
                    SharedPref.pendingPremiumOrderId = res.data.orderId
                    res.data.paymentUrl
                } else {
                    errorMsg = res.message
                    null
                }
            } catch (_: Exception) {
                null
            }
            isOrdering = false
            onResult(url, errorMsg)
            if (url != null) pollPendingOrder()
        }
    }

    /** Shows the "waiting for confirmation" banner while a pending order is polled. */
    var awaitingPayment by mutableStateOf(false)
        private set

    /** Flips true once the provider confirms the payment; screen shows the dialog. */
    var paymentConfirmed by mutableStateOf(false)
        private set

    fun consumePaymentConfirmed() {
        paymentConfirmed = false
    }

    private var pollJob: kotlinx.coroutines.Job? = null

    /**
     * Polls the pending order every 5s (up to ~5 min) until the provider callback
     * lands, then reloads the membership so the screen flips to "member". Survives
     * process death: the order id is persisted, and the screen calls this again on
     * every resume. No-op when nothing is pending.
     */
    fun pollPendingOrder() {
        val orderId = SharedPref.pendingPremiumOrderId
        if (orderId <= 0L || pollJob?.isActive == true) return
        pollJob = viewModelScope.launch {
            awaitingPayment = true
            repeat(60) {
                val paid = try {
                    val res = api.getPaymentOrderStatus(orderId)
                    res.success && res.data.status == 1
                } catch (_: Exception) {
                    false
                }
                if (paid) {
                    SharedPref.pendingPremiumOrderId = 0L
                    awaitingPayment = false
                    paymentConfirmed = true
                    load() // refresh premiumUntil + boostCredits
                    return@launch
                }
                kotlinx.coroutines.delay(5000)
            }
            // Give up: the order stayed unpaid (user likely abandoned checkout).
            SharedPref.pendingPremiumOrderId = 0L
            awaitingPayment = false
        }
    }
}
