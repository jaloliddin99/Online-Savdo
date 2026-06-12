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
