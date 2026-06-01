package uz.promo.selling.data.remote.models.getProfile

data class User(
    val email: String,
    val id: Int,
    val name: String,
    val role: String,
    val phoneNumber: String? = null,
    val profileUrl: String? = null
)