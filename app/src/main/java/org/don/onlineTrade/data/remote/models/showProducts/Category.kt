package org.don.onlineTrade.data.remote.models.showProducts

data class Category(
    val id: Int,
    val image: String?,
    val title: String,
    val parentId: Int
)
