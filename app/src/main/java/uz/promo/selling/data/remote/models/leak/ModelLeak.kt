package uz.promo.selling.data.remote.models.leak
data class ModelLeak(
    val children: List<Int>,
    val code: String,
    // Server sends this as an object for some categories and a string for others.
    // It's never used in the app, so keep it as a tolerant Any? to avoid Gson
    // "Expected BEGIN_OBJECT but was STRING" failures when loading category detail.
    val icon: Any? = null,
    val id: Int,
    val is_addable: Boolean,
    val is_business: Boolean,
    val is_offer_seekable: Boolean,
    val label_uz: String,
    val label_ru: String?,
    val max_photos: Int,
    val parameters: List<Parameter>,
    val parent: Int,
    val path: String,
    val position: Int,
    val type: String
)