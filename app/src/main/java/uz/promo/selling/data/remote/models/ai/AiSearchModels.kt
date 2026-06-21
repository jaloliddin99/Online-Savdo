package uz.promo.selling.data.remote.models.ai

/**
 * Data payload of GET /api/v1/ai/search (wrapped in GenericModel). Only [parsed]
 * is consumed by the app — the 'results' field is intentionally omitted so Gson
 * ignores it (the app drives its own paging search from the parsed filters).
 */
data class AiSearchData(
    val parsed: ParsedSearchDTO? = null
)

data class ParsedSearchDTO(
    val keywords: String? = null,
    val categoryId: Long? = null,
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val sort: String? = null
)
