package org.don.onlineTrade.data.remote.models.getPublicProducts

import androidx.annotation.Keep

@Keep
data class Data(
    val date: String,
    val description: String,
    val id: Int,
    val images: List<String>,
    val price: String,
    val region: Region,
    val title: String,
    val views: String
)