package org.don.onlineTrade.utils.pagerForSaved

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}