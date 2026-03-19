package org.don.onlineTrade.data.remote.models.showProducts

data class PostDetailsData(
    val category: Category,
    val createdDate: String,
    val description: String,
    val addressName: String,
    val addressDescription: String,
    val id: Int,
    val images: List<Image>,
    val isLiked: Boolean,
    val latitude: Double,
    val likes: Int,
    val longitude: Double,
    val title: String,
    val user: User
)
