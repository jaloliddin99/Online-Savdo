package uz.promo.selling.data.remote.models.ai

/**
 * Data payload of GET /api/v1/ai/price-suggestion (wrapped in GenericModel).
 * recommended/low/high are null and sampleSize is 0 when there aren't enough
 * comparable listings (under 5) to build a suggestion.
 */
data class PriceSuggestionDTO(
    val recommended: Double? = null,
    val low: Double? = null,
    val high: Double? = null,
    val currency: String? = null,
    val sampleSize: Long = 0
)
