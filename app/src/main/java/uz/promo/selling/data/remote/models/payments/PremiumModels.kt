package uz.promo.selling.data.remote.models.payments

/** One prepaid Premium term option (from /payments/premium-plans). */
data class PremiumPlan(
    val termMonths: Int,
    val price: Long,
)

data class PremiumPlansData(
    val plans: List<PremiumPlan> = emptyList(),
    val boostCreditsPerMonth: Int = 0,
)

data class PremiumOrderBody(
    val termMonths: Int,
    val provider: String,
)

// --- Analytics & who's-interested ---

data class PostAnalyticsData(
    val totalViews: Long = 0,
    val totalLikes: Long = 0,
    val activeCount: Long = 0,
    val totalCount: Int = 0,
    val posts: List<PostStat> = emptyList(),
)

data class PostStat(
    val id: Long,
    val title: String? = null,
    val image: String? = null,
    val viewCount: Long = 0,
    val likes: Long = 0,
    val status: Int = 0,
)

data class InterestedUser(
    val userId: Int,
    val name: String? = null,
    val profileUrl: String? = null,
    /** "like", "message", or "both". */
    val source: String = "like",
)
