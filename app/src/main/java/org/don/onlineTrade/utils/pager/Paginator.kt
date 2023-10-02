package org.don.onlineTrade.utils.pager

interface Paginator<Key, Item> {
    suspend fun loadNextItems(
        query: String?,
        categoryId: Int?,
        minPrice: Int?,
        maxPrice: Int?
    )
    fun reset()
}