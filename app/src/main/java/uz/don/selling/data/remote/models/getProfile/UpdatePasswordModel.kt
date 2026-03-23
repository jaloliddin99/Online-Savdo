package uz.don.selling.data.remote.models.getProfile

data class UpdatePasswordModel(
    val currentPassword: String,
    val newPassword: String
)
