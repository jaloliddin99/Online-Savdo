package uz.promo.selling.data.remote.models.getPublicProducts

data class Content(
    val createdDate: String,
    val id: Int,
    // Nullable: posts with no images (e.g. some OLX imports) send null here.
    val image: Image? = null,
    val likes: Int,
    // Nullable in practice: some posts (e.g. OLX imports) omit price/address.
    // Non-null would make the generated hashCode/equals NPE on those rows.
    val price: String? = null,
    val priceUnit: String? = null,
    val addressName: String? = null,
    val addressDescription: String? = null,
    // Coordinates for the search map (nullable — older/imported posts may lack them).
    val latitude: Double? = null,
    val longitude: Double? = null,
    val title: String,
    val status: Int,
    val condition: String?=null,
    val isPrioritized: Boolean,
    val viewCount: Int = 0
)
