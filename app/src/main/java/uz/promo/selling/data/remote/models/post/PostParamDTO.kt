package uz.promo.selling.data.remote.models.post

import uz.promo.selling.ui.main.add.dynamic.DynamicViewData


data class PostParamDTO(
    val code: String,
    val label_uz: String,
    val label_ru: String,
    val type: String,
    val post_value: List<PostValueDTO>,
    val param_unit: PostUnitDTO?=null
)
data class PostValueDTO(
    val key: String,
    val label_uz: String,
    val label_ru: String,
)
data class PostUnitDTO(
    val code: String,
    val label: String
)
fun DynamicViewData.toPostDto(): PostParamDTO{
    return PostParamDTO(
        code,
        label_uz,
        label_ru,
        type,
        post_value,
        unit
    )
}