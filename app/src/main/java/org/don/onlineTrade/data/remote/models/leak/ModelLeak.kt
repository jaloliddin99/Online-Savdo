package org.don.onlineTrade.data.remote.models.leak
data class ModelLeak(
    val children: List<Int>,
    val code: String,
    val icon: Icon,
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