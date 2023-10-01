package org.don.onlineTrade.data.mappers

import org.don.onlineTrade.data.remote.models.category.CategoryModelItem
import org.don.onlineTrade.data.remote.models.category.CompactedCategoryItem
import org.don.onlineTrade.data.remote.models.getPublicProducts.Data
import org.don.onlineTrade.data.remote.models.getPublicProducts.Region
import org.don.onlineTrade.data.remote.models.liked.LikedProductsModelItem


fun CategoryModelItem.toCompactedCategoryItem(): CompactedCategoryItem {
    return CompactedCategoryItem(
        id, image, title
    )
}
