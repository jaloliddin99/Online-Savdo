package uz.promo.selling.data.remote.models.getProfile

data class UpdatePasswordModel(
    val currentPassword: String,
    val newPassword: String
)
