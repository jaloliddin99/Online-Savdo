package uz.don.selling.data.remote.models.getNotifications

import uz.don.selling.data.remote.models.getPublicProducts.Pageable
import uz.don.selling.data.remote.models.getPublicProducts.SortX

data class NotificationData(
    val content: List<Content>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: SortX,
    val totalElements: Int,
    val totalPages: Int
)
class Content(
    val id: Long,
    val imagePath: String,
    val title: String,
    val desc: String,
    val createdDate: String
)