package org.don.onlineTrade.utils.pager

class DefaultPaginator<Key, Item>(
    private val initialKey: Key,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val onRequest: suspend (
        nextKey: Key,
        query: String?,
        categoryId: Int?,
        minPrice: Int?,
        maxPrice: Int?,
        startDate: String?,
        endDate: String?,
        regionId: Int,
        districtId: Int,
        isMyPosts: Boolean
    ) -> Result<List<Item>>,
    private inline val getNextKey: suspend (List<Item>) -> Key,
    private inline val onError: suspend (Throwable?) -> Unit,
    private inline val onSuccess: suspend (items: List<Item>, newKey: Key) -> Unit
) : Paginator<Key, Item> {

    private var currentKey = initialKey
    private var isMakingRequest = false

    override suspend fun loadNextItems(
        query: String?,
        categoryId: Int?,
        minPrice: Int?,
        maxPrice: Int?,
        startDate: String?,
        endDate: String?,
        regionId: Int,
        districtId: Int,
        isMyPosts: Boolean
    ) {
        if (isMakingRequest) {
            return
        }
        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(
            currentKey,
            query,
            categoryId,
            minPrice,
            maxPrice,
            startDate, endDate, regionId, districtId,
            isMyPosts
        )
        isMakingRequest = false
        val items = result.getOrElse {
            onError(it)
            onLoadUpdated(false)
            return
        }
        currentKey = getNextKey(items)
        onSuccess(items, currentKey)
        onLoadUpdated(false)
    }

    override fun reset() {
        currentKey = initialKey
    }
}