package org.don.onlineTrade.utils.pager

interface Paginator<Key, Item> {
    suspend fun loadNextItems(
        query: String?,
        categoryId: Int?,
        minPrice: Int?,
        maxPrice: Int?,
        startDate: String?,
        endDate: String?,
        isMyPosts: Boolean,
        lat: Double? = null,
        lon: Double? = null,
        radius: Int? = null
    )
    fun reset()
}
