package org.don.onlineTrade.data.remote.models.showProducts

data class Data(
    val category: Category,
    val createdDate: String,
    val currency_id: Int,
    val description: String,
    val district: District,
    val id: Int,
    val images: List<Image>,
    val latitude: Double,
    val likes: Int,
    val longitude: Double,
    val price: Double,
    val region: Region,
    val title: String,
    val user: User,
    val isLiked: Boolean
)