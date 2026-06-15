package uz.promo.selling.data.remote.models.getPublicProducts

data class Content(
    val createdDate: String,
    val id: Int,
    // Nullable: posts with no images (e.g. some OLX imports) send null here.
    val image: Image? = null,
    val likes: Int,
    val price: String,
    val priceUnit: String,
    val addressName: String,
    val addressDescription: String,
    val title: String,
    val status: Int,
    val condition: String?=null,
    val isPrioritized: Boolean,
    val viewCount: Int = 0
)
