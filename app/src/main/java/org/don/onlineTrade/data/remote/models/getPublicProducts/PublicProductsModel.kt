package org.don.onlineTrade.data.remote.models.getPublicProducts

import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.data.remote.models.getPublicProducts.Links
import org.don.onlineTrade.data.remote.models.getPublicProducts.Meta

data class PublicProductsModel(
    val `data`: List<Data>,
    val links: Links,
    val meta: Meta
)