package uz.promo.selling.ui.main.add

import android.net.Uri
import uz.promo.selling.BuildConfig
import uz.promo.selling.data.remote.models.category.CategoryItem
import uz.promo.selling.data.remote.models.post.PostValueDTO
import uz.promo.selling.data.remote.models.showProducts.PostDetailsData
import uz.promo.selling.ui.main.add.dynamic.DynamicViewData
import uz.promo.selling.ui.map.MapScreenData

/**
 * Everything the create wizard needs to reopen an existing post for editing.
 * Mirrors the web app's `EditInit`, which reuses its create wizard the same way.
 *
 * [existingImages] are the post's current photos as remote URLs. They go into the
 * same picker list as newly chosen ones, so removing one there drops it from
 * keepImageIds on save and the server deletes it.
 */
data class EditPostInit(
    val postId: Int,
    val category: CategoryItem,
    val map: MapScreenData,
    val title: String,
    val description: String,
    val params: Map<String, DynamicViewData>,
    /** The post's current photos, shown in the picker and kept unless removed. */
    val existingImages: List<ImageUrl>
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
    existingImages = images.map { image ->
        val url = Uri.parse("${BuildConfig.BASE_URL}post/image/${image.imagePath}")
        ImageUrl(isFromCamera = false, uri = url, fakeUri = url, existingId = image.id)
    }
)
