package org.don.onlineTrade.data.remote.models.showProducts

import org.don.onlineTrade.data.remote.models.getPublicProducts.District
import org.don.onlineTrade.data.remote.models.getPublicProducts.Region

data class PostDetailsData(
    val category: Category,
    val createdDate: String,
    val description: String,
    val district: District,
    val addressName: String,
    val addressDescription: String,
    val id: Int,
    val images: List<Image>,
    val isLiked: Boolean,
    val latitude: Double,
    val likes: Int,
    val longitude: Double,
    val region: Region,
    val title: String,
    val user: User
)