package uz.promo.selling.data.remote.models.payments

data class BoostTariff(
    val hours: Int,
    val price: Long
)

data class BoostOrderBody(
    val postId: Long,
    val hours: Int,
    val provider: String
)

data class BoostOrderRes(
    val orderId: Long,
    val paymentUrl: String,
    val amount: Long,
    val hours: Int
)

/** Polled after the checkout browser round-trip; status 1 = paid. */
data class OrderStatusRes(
    val orderId: Long,
    val status: Int,
    val hours: Int?,
    val termMonths: Int?,
    val amount: Long
)
