package uz.don.selling.data.remote.models.category

import java.io.Serializable

data class CategoryItem(
    val children: List<CategoryItem> = emptyList(),
    val id: Int = -1,
    val image: String? = null,
    val title: String = ""
):Serializable


