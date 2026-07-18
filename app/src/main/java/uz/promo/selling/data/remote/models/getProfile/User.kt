package uz.promo.selling.data.remote.models.getProfile

data class User(
    val email: String,
    val id: Int,
    val name: String,
    val lastname: String? = null,
    val role: String,
    val phoneNumber: String? = null,
    val profileUrl: String? = null,
    // Premium membership: premiumUntil is an ISO date-time; boostCredits are the
    // included promote credits. isPremium is derived client-side.
    val premiumUntil: String? = null,
    val boostCredits: Int = 0,
)