package org.don.onlineTrade.utils.pager

interface Paginator<Key, Item> {
    suspend fun loadNextItems(
        query: String?,
        categoryId: Int?,
        minPrice: Int?,
        maxPrice: Int?,
        startDate: String?,
        endDate: String?,
        regionId: Int,
        districtId: Int,
        isMyPosts: Boolean
    )
    fun reset()
}