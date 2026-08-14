package uz.promo.selling.data.remote.models.payments

import androidx.annotation.Keep

data class BoostTariff(
    val hours: Int,
    val price: Long
)

data class BoostOrderBody(
    val postId: Long,
    val hours: Int,
    val provider: String,
    val promoCode: String? = null
)

data class BoostOrderRes(
    val orderId: Long,
    val paymentUrl: String,
    val amount: Long,
    val hours: Int,
    val originalAmount: Long? = null,
    val discountPercent: Int? = null
)

/** Body for POST payments/promo/validate; type is "boost" or "premium". */
@Keep
data class PromoValidateBody(
    val code: String,
    val type: String,
    val hours: Int? = null,
    val termMonths: Int? = null
)

@Keep
data class PromoValidateRes(
    val code: String = "",
    val percent: Int = 0,
    val originalAmount: Long? = null,
    val discountedAmount: Long? = null
)

/** Polled after the checkout browser round-trip; status 1 = paid. */
data class OrderStatusRes(
    val orderId: Long,
    val status: Int,
    val hours: Int?,
    val termMonths: Int?,
    val amount: Long
)
