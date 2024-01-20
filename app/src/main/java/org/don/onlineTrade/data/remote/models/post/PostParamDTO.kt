package org.don.onlineTrade.data.remote.models.post

data class PostParamDTO(
    private val code: String,
    private val label_uz: String,
    private val label_ru: String,
    private val type: String,
    private val post_value: List<PostValueDTO>,
    private val param_unit: PostUnitDTO
)

data class PostValueDTO(
    private val key: String,
    private val label_uz: String,
    private val label_ru: String,
)
data class PostUnitDTO(
    private val code: String,
    private val label: String
)