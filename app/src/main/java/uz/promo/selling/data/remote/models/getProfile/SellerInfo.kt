package uz.promo.selling.data.remote.models.getProfile

import androidx.annotation.Keep

/** Public seller header shown on the seller profile screen. */
@Keep
data class SellerInfo(
    val id: Int,
    val name: String? = null,
    val phoneNumber: String? = null,
    val profileUrl: String? = null,
    val premium: Boolean = false
)
