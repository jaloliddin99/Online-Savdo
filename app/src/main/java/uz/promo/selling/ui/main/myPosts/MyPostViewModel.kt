package uz.promo.selling.ui.main.myPosts

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import uz.promo.selling.data.paging.MyPostsPagingSource
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.getPublicProducts.Content
import uz.promo.selling.domain.state.Resource
import uz.promo.selling.domain.useCase.postNewProduct.PrioritizePostUseCase
import uz.promo.selling.ui.main.home.MyProfileScreen
import uz.promo.selling.utils.SharedPref
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
                    // Offset-based backend: keep size constant across loads so page offsets
                    // don't overlap and trigger runaway appends. See HomeViewModel.
                    initialLoadSize = 20,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { MyPostsPagingSource(apiInterface, status) }
            ).flow
        }.cachedIn(viewModelScope)

    fun setStatusFilter(status: Int?) {
        _selectedStatus.value = status
    }

    var tariffs by androidx.compose.runtime.mutableStateOf<List<uz.promo.selling.data.remote.models.payments.BoostTariff>>(emptyList())
        private set

    // Included premium boost credits (from the profile), for the "free promote" path.
    var boostCredits by androidx.compose.runtime.mutableStateOf(0)
        private set

    fun loadTariffs() {
        if (tariffs.isNotEmpty()) return
        viewModelScope.launch {
            try {
                val res = apiInterface.getBoostTariffs()
                if (res.success) tariffs = res.data.sortedBy { it.hours }
            } catch (_: Exception) {
            }
        }
    }

    /** Loads the member's remaining boost credits from the profile. */
    fun loadBoostCredits() {
        if (SharedPref.deviceToken.isBlank()) return
        viewModelScope.launch {
            try {
                boostCredits = apiInterface.getProfile(SharedPref.deviceToken).data.boostCredits
            } catch (_: Exception) {
            }
        }
    }

    /** Promotes a post using one included credit (no payment). */
    fun promoteWithCredit(postId: Long, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _state.value = MyProfileScreen(isLoading = true)
            val ok = try {
                apiInterface.promoteWithCredit(mapOf("postId" to postId)).success
            } catch (e: Exception) {
                false
            }
            _state.value = MyProfileScreen()
            if (ok && boostCredits > 0) boostCredits -= 1
            onDone(ok)
        }
    }

    /** Creates a payment order; onResult gets the checkout URL or null. */
    fun createBoostOrder(postId: Long, hours: Int, provider: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            _state.value = MyProfileScreen(isLoading = true)
            val url = try {
                val res = apiInterface.createBoostOrder(
                    token = SharedPref.deviceToken,
                    body = uz.promo.selling.data.remote.models.payments.BoostOrderBody(postId, hours, provider)
                )
                if (res.success) {
                    SharedPref.pendingBoostOrderId = res.data.orderId
                    res.data.paymentUrl
                } else null
            } catch (e: Exception) {
                null
            }
            _state.value = MyProfileScreen()
            onResult(url)
            if (url != null) pollPendingOrder()
        }
    }

    /** Shows the "waiting for confirmation" banner while a pending order is polled. */
    var awaitingPayment by androidx.compose.runtime.mutableStateOf(false)
        private set

    /** Flips true once the provider confirms the payment; screen shows the dialog. */
    var paymentConfirmed by androidx.compose.runtime.mutableStateOf(false)
        private set

    fun consumePaymentConfirmed() {
        paymentConfirmed = false
    }

    private var pollJob: kotlinx.coroutines.Job? = null

    /**
     * Polls the pending order every 5s (up to ~5 min) until the provider callback
     * lands. Survives process death: the order id is persisted, and the screen
     * calls this again on every resume. No-op when nothing is pending.
     */
    fun pollPendingOrder() {
        val orderId = SharedPref.pendingBoostOrderId
        if (orderId <= 0L || pollJob?.isActive == true) return
        pollJob = viewModelScope.launch {
            awaitingPayment = true
            repeat(60) {
                val paid = try {
                    val res = apiInterface.getPaymentOrderStatus(orderId)
                    res.success && res.data.status == 1
                } catch (_: Exception) {
                    false
                }
                if (paid) {
                    SharedPref.pendingBoostOrderId = 0L
                    awaitingPayment = false
                    paymentConfirmed = true
                    return@launch
                }
                kotlinx.coroutines.delay(5000)
            }
            // Give up: the order stayed unpaid (user likely abandoned checkout).
            SharedPref.pendingBoostOrderId = 0L
            awaitingPayment = false
        }
    }

    fun markPostSold(postId: Long, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _state.value = MyProfileScreen(isLoading = true)
            val ok = try {
                apiInterface.markPostSold(SharedPref.deviceToken, postId).success
            } catch (e: Exception) {
                false
            }
            _state.value = MyProfileScreen()
            onDone(ok)
        }
    }

    fun activatePost(postId: Long, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _state.value = MyProfileScreen(isLoading = true)
            val ok = try {
                apiInterface.activatePost(SharedPref.deviceToken, postId).success
            } catch (e: Exception) {
                false
            }
            _state.value = MyProfileScreen()
            onDone(ok)
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
