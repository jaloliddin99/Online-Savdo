package uz.promo.selling.ui.main.add

import uz.promo.selling.data.remote.models.category.CategoryItem
import uz.promo.selling.data.remote.models.post.PostValueDTO
import uz.promo.selling.data.remote.models.showProducts.PostDetailsData
import uz.promo.selling.ui.main.add.dynamic.DynamicViewData
import uz.promo.selling.ui.map.MapScreenData

/**
 * Everything the create wizard needs to reopen an existing post for editing.
 * Mirrors the web app's `EditInit`, which reuses its create wizard the same way.
 *
 * [existingImageCount] is only used to decide whether new photos are required:
 * the update endpoint keeps the post's current images when no files are sent.
 */
data class EditPostInit(
    val postId: Int,
    val category: CategoryItem,
    val map: MapScreenData,
    val title: String,
    val description: String,
    val params: Map<String, DynamicViewData>,
    val existingImageCount: Int
)

/**
 * Builds the wizard's edit seed from a loaded post.
 *
 * The saved parameter values arrive as [uz.promo.selling.data.remote.models.showProducts.PostParam],
 * which carries a single localized `label` rather than the label_uz/label_ru pair the
 * wizard works in, so the same label is used for both. Values are marked valid so a
 * user who changes nothing can still submit.
 */
fun PostDetailsData.toEditInit(): EditPostInit = EditPostInit(
    postId = id,
    category = CategoryItem(id = category.id, title = category.title),
    map = MapScreenData(
        lat = latitude,
        lon = longitude,
        addressName = addressName,
        addressDescription = addressDescription
    ),
    title = title,
    description = description,
    params = category.post_param.associate { param ->
        param.code to DynamicViewData(
            isRequired = false,
            isValid = true,
            code = param.code,
            label_uz = param.label,
            label_ru = param.label,
            type = param.type,
            post_value = param.post_value.map {
                PostValueDTO(key = it.key, label_uz = it.label, label_ru = it.label)
            }.ifEmpty {
                listOf(PostValueDTO(key = "", label_uz = "", label_ru = ""))
            },
            unit = param.param_unit?.let {
                uz.promo.selling.data.remote.models.post.PostUnitDTO(it.code, it.label)
            }
        )
    },
    existingImageCount = images.size
)
