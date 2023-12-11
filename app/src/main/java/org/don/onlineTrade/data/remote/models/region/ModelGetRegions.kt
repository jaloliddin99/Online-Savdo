package org.don.onlineTrade.data.remote.models.region

import androidx.annotation.Keep

@Keep
data class ModelGetRegions(
    val `data`: List<Data>,
    val message: String,
    val success: Boolean
)