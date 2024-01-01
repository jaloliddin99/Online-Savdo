package org.don.onlineTrade.data.remote.models.category


data class CategoryModelItem(
    val id: Int,
    val image: String?,
    val title: String,
    val parentId: Int
)