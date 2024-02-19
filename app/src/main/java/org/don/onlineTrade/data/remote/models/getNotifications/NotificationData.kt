package org.don.onlineTrade.data.remote.models.getNotifications

import org.don.onlineTrade.data.remote.models.getPublicProducts.Pageable
import org.don.onlineTrade.data.remote.models.getPublicProducts.SortX

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