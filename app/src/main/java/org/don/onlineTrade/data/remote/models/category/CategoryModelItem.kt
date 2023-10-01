package org.don.onlineTrade.data.remote.models.category

data class CategoryModelItem(
    val children: List<Children>,
    val id: Int,
    val image: String?=null,
    val title: String
)