package org.don.onlineTrade.data.remote.models.category

import java.io.Serializable

data class CompactedCategoryItem(
    val id: Int = -1,
    val image: String?=null,
    val title: String
):Serializable
