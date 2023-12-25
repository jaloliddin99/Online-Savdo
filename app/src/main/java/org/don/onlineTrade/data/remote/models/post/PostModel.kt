package org.don.onlineTrade.data.remote.models.post

data class PostModel(
    val `data`: Data,
    val message: String,
    val success: Boolean
)