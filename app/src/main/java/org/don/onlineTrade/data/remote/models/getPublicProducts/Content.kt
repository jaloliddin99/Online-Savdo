package org.don.onlineTrade.data.remote.models.getPublicProducts

data class Content(
    val createdDate: String,
    val id: Int,
    val image: Image,
    val likes: Int,
    val price: String,
    val priceUnit: String,
    val region: Region?,
    val district: District?,
    val addressName: String,
    val addressDescription: String,
    val title: String,
    val status: Int,
    val condition: String?=null,
    val isPrioritized: Boolean
)

data class District(
    val id: Int,
    val name: String,
    val regionId: Int
)