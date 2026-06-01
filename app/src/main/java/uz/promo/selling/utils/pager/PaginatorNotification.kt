package uz.promo.selling.utils.pager

interface PaginatorNotification<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}