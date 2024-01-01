package org.don.onlineTrade.data.remote.models.category

import androidx.annotation.Keep
import java.io.Serializable


@Keep
data class CompactedCategoryItem(
    val id: Int = -1,
    val image: String? = null,
    val title: String = "",
    val parentId: Int = -1
): Serializable
