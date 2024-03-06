package org.don.onlineTrade.data.remote.models.category

import java.io.Serializable

class Category : ArrayList<CategoryItem>()
class ParentCategories : ArrayList<CategoryParent>()
data class CategoryParent(
    val id: Int = -1,
    val image: String? = null,
    val title: String = "",
    val position: Int = -1
)
