package uz.don.onlineTrade.data.remote.models.category


class Category : ArrayList<CategoryItem>()
class ParentCategories : ArrayList<CategoryParent>()
data class CategoryParent(
    val id: Int = -1,
    val image: String? = null,
    val title: String = "",
    val position: Int = -1
)
