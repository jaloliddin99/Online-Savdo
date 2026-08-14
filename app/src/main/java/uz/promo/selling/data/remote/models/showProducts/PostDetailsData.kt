package uz.promo.selling.data.remote.models.showProducts

data class PostDetailsData(
    val category: Category,
    val createdDate: String,
    val description: String,
    val addressName: String,
    val addressDescription: String,
    val id: Int,
    val images: List<Image>,
    val isLiked: Boolean,
    val latitude: Double,
    val likes: Int,
    val longitude: Double,
    val title: String,
    val user: User,
    val status: Int = 1,
    val viewCount: Int = 0,
    // Non-null only while a recent price reduction should be shown struck through.
    // The server applies the visibility window, so there's no date logic here.
    val previousPrice: Double? = null,
    val previousPriceCurrency: String? = null
)
