package uz.promo.selling.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import uz.promo.selling.BuildConfig
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.chat.ChatMessage
import uz.promo.selling.data.remote.models.chat.Conversation
import uz.promo.selling.data.remote.models.chat.ReportBody
import uz.promo.selling.data.remote.models.chat.SendMessageBody
import uz.promo.selling.utils.SharedPref
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val api: ApiInterface
) : ViewModel() {

    var state by mutableStateOf(ChatDetailState())
        private set

    var socketConnected by mutableStateOf(false)
        private set

    private var webSocket: WebSocket? = null
    private var socketConversationId: Long = -1
    private val socketClient = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    /** Live message events; REST stays the only send/refresh path. */
    fun connectSocket(conversationId: Long) {
        if (webSocket != null && socketConversationId == conversationId && socketConnected) return
        webSocket?.cancel()
        socketConversationId = conversationId
        val token = SharedPref.deviceToken
        if (token.isBlank()) return
        // BASE_URL is "https://selling.uz/api/v1/" — derive the ws endpoint from it.
        val wsUrl = BuildConfig.BASE_URL
            .replaceFirst("https://", "wss://")
            .replaceFirst("http://", "ws://") + "ws/chat"
        val request = Request.Builder()
            .url(wsUrl)
            .addHeader("Authorization", token)
            .build()
        webSocket = socketClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                socketConnected = true
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val event = JSONObject(text)
                    val type = event.optString("type")
                    if ((type == "message" || type == "message_edited" || type == "message_deleted") &&
                        event.optLong("conversationId") == socketConversationId
                    ) {
                        refreshMessages(socketConversationId)
                    }
                } catch (_: Exception) {
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                socketConnected = false
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                socketConnected = false
            }
        })
    }

    override fun onCleared() {
        webSocket?.cancel()
        socketClient.dispatcher.executorService.shutdown()
        super.onCleared()
    }

    /** Load the conversation header once when the screen opens. */
    fun loadHeader(conversationId: Long) {
        viewModelScope.launch {
            try {
                val res = api.getConversation(conversationId)
                if (res.success) state = state.copy(conversation = res.data)
            } catch (_: Exception) {
            }
        }
    }

    /** Fetch the latest messages (also marks incoming ones read server-side). */
    fun refreshMessages(conversationId: Long) {
        viewModelScope.launch {
            try {
                val res = api.getChatMessages(conversationId, page = 0, size = 50)
                if (res.success) {
                    // Server returns newest-first; the thread renders oldest-first.
                    state = state.copy(messages = res.data.content.reversed(), loaded = true)
                }
            } catch (_: Exception) {
                state = state.copy(loaded = true)
            }
        }
    }

    fun send(conversationId: Long, content: String) {
        val text = content.trim()
        if (text.isEmpty() || state.sending) return
        val editing = state.editing
        state = state.copy(sending = true)
        viewModelScope.launch {
            try {
                if (editing != null) {
                    val res = api.editChatMessage(conversationId, editing.id, SendMessageBody(text))
                    // The window may have expired between fetch and tap — show the
                    // server's localized message; either way the edit UI is done.
                    state = state.copy(
                        editing = null,
                        error = if (res.success) null else res.message
                    )
                    if (res.success) refreshMessages(conversationId)
                } else {
                    val res = api.sendChatMessage(
                        conversationId,
                        SendMessageBody(text, state.replyingTo?.id)
                    )
                    if (res.success) {
                        state = state.copy(messages = state.messages + res.data, replyingTo = null)
                    } else {
                        state = state.copy(error = res.message)
                    }
                }
            } catch (_: Exception) {
            }
            state = state.copy(sending = false)
        }
    }

    /** Starting a reply cancels an in-progress edit and vice versa. */
    fun startReply(message: ChatMessage) {
        state = state.copy(replyingTo = message, editing = null)
    }

    fun startEdit(message: ChatMessage) {
        state = state.copy(editing = message, replyingTo = null)
    }

    fun cancelReplyEdit() {
        state = state.copy(replyingTo = null, editing = null)
    }

    fun deleteMessage(conversationId: Long, messageId: Long) {
        viewModelScope.launch {
            try {
                val res = api.deleteChatMessage(conversationId, messageId)
                if (!res.success) state = state.copy(error = res.message)
            } catch (_: Exception) {
            }
            refreshMessages(conversationId)
        }
    }

    fun clearError() {
        state = state.copy(error = null)
    }

    fun report(conversationId: Long, reason: String, message: String?, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                api.reportConversation(conversationId, ReportBody(reason, message?.ifBlank { null }))
            } catch (_: Exception) {
            }
            onDone()
        }
    }

    fun block(conversationId: Long) {
        viewModelScope.launch {
            try {
                api.blockConversation(conversationId)
            } catch (_: Exception) {
            }
            loadHeader(conversationId)
        }
    }

    fun unblock(conversationId: Long) {
        viewModelScope.launch {
            try {
                api.unblockConversation(conversationId)
            } catch (_: Exception) {
            }
            loadHeader(conversationId)
        }
    }

    fun delete(conversationId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                api.deleteConversation(conversationId)
            } catch (_: Exception) {
            }
            onDone()
        }
    }

    data class ChatDetailState(
        val conversation: Conversation? = null,
        val messages: List<ChatMessage> = emptyList(),
        val loaded: Boolean = false,
        val sending: Boolean = false,
        val replyingTo: ChatMessage? = null,
        val editing: ChatMessage? = null,
        val error: String? = null
    )
}
