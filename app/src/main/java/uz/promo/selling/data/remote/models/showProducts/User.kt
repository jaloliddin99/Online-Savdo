package uz.promo.selling.data.remote.models.showProducts

data class User(
    val email: String,
    val id: Int,
    val name: String,
    val phoneNumber: String,
    val profileUrl: String,
    /** Active premium membership — shows the badge next to the seller name. */
    val premium: Boolean = false
)