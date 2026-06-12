package uz.promo.selling.data.remote.models.inbox

data class UserNotificationItem(
    val id: Long,
    val type: String?,
    val title: String?,
    val body: String?,
    val refId: Long?,
    val read: Boolean,
    val createdDate: String?
)

data class UserNotificationPage(
    val content: List<UserNotificationItem>,
    val last: Boolean
)

data class UnreadCountRes(
    val count: Long
)

data class NotificationPrefs(
    val chat: Boolean = true,
    val search: Boolean = true,
    val myAds: Boolean = true
)
