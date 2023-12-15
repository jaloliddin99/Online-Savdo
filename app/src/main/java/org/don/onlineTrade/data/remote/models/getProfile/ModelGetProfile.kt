package org.don.onlineTrade.data.remote.models.getProfile

import org.don.onlineTrade.data.remote.models.getPublicProducts.Data

data class ModelGetProfile(
    val success: Boolean,
    val message: String,
    val `data`: User
)
