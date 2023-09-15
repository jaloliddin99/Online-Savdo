package org.don.onlineTrade.data.remote.models.getPublicProducts

data class Data(
    val date: String,
    val id: Int,
    val images: List<String>,
    val price: String,
    val region: Region,
    val title: String,
    val views: Int
)