package org.don.onlineTrade.data.remote.models.getPublicProducts

data class Content(
    val createdDate: String,
    val currency_id: Int,
    val id: Int,
    val image: Image,
    val likes: Int,
    val price: Double,
    val region: Region,
    val title: String,
    val status: Int?=1
)