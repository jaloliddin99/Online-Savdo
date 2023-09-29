package org.don.onlineTrade.data.remote.models.showProducts

import java.io.Serializable

data class ShowProductModel(
    val category: Category,
    val date: String,
    val id: Int,
    val description: String,
    val images: List<String>,
    val price: String,
    val region: Region,
    val title: String,
    val user: User,
    val views: Int
):Serializable