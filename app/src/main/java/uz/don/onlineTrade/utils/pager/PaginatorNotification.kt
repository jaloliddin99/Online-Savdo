package uz.don.onlineTrade.utils.pager

interface PaginatorNotification<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}