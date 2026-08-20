package uz.promo.selling.data.remote.models.leak

data class Values(
    val id: Int,
    val key: String,
    val label_uz: String,
    val label_ru: String,
    val label_en: String? = null,
    val disabled: Boolean
)
