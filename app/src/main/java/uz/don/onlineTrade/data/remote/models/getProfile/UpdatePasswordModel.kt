package uz.don.onlineTrade.data.remote.models.getProfile

data class UpdatePasswordModel(
    val currentPassword: String,
    val newPassword: String
)
