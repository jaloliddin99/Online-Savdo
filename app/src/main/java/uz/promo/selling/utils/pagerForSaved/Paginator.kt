package uz.promo.selling.utils.pagerForSaved

interface Paginator<Key, Item> {
    suspend fun loadNextItems()
    fun reset()
}