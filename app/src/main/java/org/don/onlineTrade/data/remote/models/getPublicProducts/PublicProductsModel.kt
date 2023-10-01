package org.don.onlineTrade.data.remote.models.getPublicProducts

import androidx.annotation.Keep

@Keep
data class PublicProductsModel(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)