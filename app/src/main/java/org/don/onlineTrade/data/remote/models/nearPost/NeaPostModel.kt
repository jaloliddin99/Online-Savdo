package org.don.onlineTrade.data.remote.models.nearPost

data class NeaPostModel(
    val `data`: List<Data>,
    val message: String,
    val success: Boolean
)