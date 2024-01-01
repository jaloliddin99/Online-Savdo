package org.don.onlineTrade.data.mappers

import org.don.onlineTrade.data.remote.models.category.CategoryModelItem
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem


fun CategoryModelItem.toCompactedCategoryItem(): CompactedCategoryItem {
    return CompactedCategoryItem(
        id = this.id,
        title = this.title,
        image = this.image,
        parentId = this.parentId
    )
}
