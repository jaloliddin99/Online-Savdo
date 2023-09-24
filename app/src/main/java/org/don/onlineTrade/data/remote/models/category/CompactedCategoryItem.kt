package org.don.onlineTrade.data.remote.models.category

import java.io.Serializable

data class CompactedCategoryItem(
    val id: Int,
    val image: String?=null,
    val title: String
):Serializable
