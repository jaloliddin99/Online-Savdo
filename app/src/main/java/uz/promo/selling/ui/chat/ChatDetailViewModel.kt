package uz.promo.selling.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.chat.ChatMessage
import uz.promo.selling.data.remote.models.chat.Conversation
import uz.promo.selling.data.remote.models.chat.ReportBody
import uz.promo.selling.data.remote.models.chat.SendMessageBody
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val api: ApiInterface
) : ViewModel() {

    var state by mutableStateOf(ChatDetailState())
        private set

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
        state = state.copy(sending = true)
        viewModelScope.launch {
            try {
                val res = api.sendChatMessage(conversationId, SendMessageBody(text))
                if (res.success) {
                    state = state.copy(messages = state.messages + res.data)
                }
            } catch (_: Exception) {
            }
            state = state.copy(sending = false)
        }
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
        val sending: Boolean = false
    )
}
