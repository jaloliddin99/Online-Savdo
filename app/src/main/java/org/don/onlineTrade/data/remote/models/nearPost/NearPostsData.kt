package org.don.onlineTrade.data.remote.models.nearPost

data class NearPostsData(
    val createdDate: String,
    val distance: Int,
    val id: Int,
    val image: Image,
    val price: Double,
    val title: String,
    val status: Int
)