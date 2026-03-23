package uz.don.onlineTrade.data.remote.models.leak

data class Validation(
    val is_required: Boolean,
    val max: Int?,
    val min: Int?,
    val pattern: String?
)