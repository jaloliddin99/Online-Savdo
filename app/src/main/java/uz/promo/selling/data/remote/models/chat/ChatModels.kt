package uz.promo.selling.data.remote.models.chat

import androidx.annotation.Keep

/** A conversation row; the "other" user is relative to the current viewer. */
@Keep
data class Conversation(
    val id: Long,
    val postId: Long? = null,
    val postTitle: String? = null,
    val postImage: String? = null,
    val postPrice: String? = null,
    val postPriceUnit: String? = null,
    val otherUserId: Int? = null,
    val otherUserName: String? = null,
    val otherUserProfileUrl: String? = null,
    val lastMessage: String? = null,
    val lastMessageAt: String? = null,
    val unreadCount: Long = 0,
    val blocked: Boolean = false
)

/** A single chat message; [mine] and [read] are relative to the current viewer. */
@Keep
data class ChatMessage(
    val id: Long,
    val conversationId: Long = 0,
    val senderId: Int = 0,
    val content: String = "",
    val createdDate: String? = null,
    val readAt: String? = null,
    val mine: Boolean = false,
    val read: Boolean = false,
    val editedAt: String? = null,
    val deleted: Boolean = false,
    /** Server-computed: mine && not deleted && within the edit/delete window. */
    val editable: Boolean = false,
    val replyToId: Long? = null,
    /** Quote snapshot; null with [replyToId] set means the quoted message was deleted. */
    val replyToContent: String? = null,
    val replyToSenderName: String? = null
)

/** Spring Data page wrappers (only the fields the UI needs). */
@Keep
data class ConversationPage(
    val content: List<Conversation> = emptyList(),
    val last: Boolean = true,
    val number: Int = 0
)

@Keep
data class MessagePage(
    val content: List<ChatMessage> = emptyList(),
    val last: Boolean = true,
    val number: Int = 0
)

@Keep
data class StartConversationBody(
    val postId: Int
)

@Keep
data class SendMessageBody(
    val content: String,
    val replyToId: Long? = null
)

@Keep
data class ReportBody(
    val reason: String,
    val message: String? = null
)

@Keep
data class UnreadCount(
    val count: Long = 0
)
