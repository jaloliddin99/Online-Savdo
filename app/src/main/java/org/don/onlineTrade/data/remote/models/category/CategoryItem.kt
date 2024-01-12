package org.don.onlineTrade.data.remote.models.category

import java.io.Serializable

data class CategoryItem(
    val children: List<CategoryItem>,
    val id: Int,
    val image: String,
    val title: String
):Serializable