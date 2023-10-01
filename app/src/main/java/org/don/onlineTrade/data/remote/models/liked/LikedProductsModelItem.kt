package org.don.onlineTrade.data.remote.models.liked

data class LikedProductsModelItem(
    val date: String,
    val id: Int,
    val images: List<String>,
    val price: String,
    val title: String,
    val views: Int
)