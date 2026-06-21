package uz.promo.selling.data.remote.models.ai

import uz.promo.selling.data.remote.models.post.PostParamDTO

/**
 * Response of POST /api/v1/ai/listing-draft. The backend wraps everything in the
 * standard ModelSuccess envelope; [data] holds the actual draft + ready-to-submit
 * post params (same shape the create-post call expects).
 */
data class AiDraftEnvelope(
    val success: Boolean? = null,
    val message: String? = null,
    val data: AiListingDraftResponse? = null
)

data class AiListingDraftResponse(
    val draft: AiListingDraft? = null,
    val postParams: List<PostParamDTO>? = null
)

data class AiListingDraft(
    val title: String? = null,
    val description: String? = null,
    val categoryId: Long? = null,
    val categoryLabel: String? = null,
    val brand: String? = null,
    val condition: String? = null,
    val attributes: List<AiAttribute>? = null
)

data class AiAttribute(
    val name: String? = null,
    val value: String? = null
)
