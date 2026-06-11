package uz.promo.selling.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.chat.Conversation
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val api: ApiInterface
) : ViewModel() {

    var state by mutableStateOf(ChatListState())
        private set

    fun loadConversations() {
        viewModelScope.launch {
            state = state.copy(isLoading = state.items.isEmpty(), error = null)
            try {
                val res = api.getConversations(page = 0, size = 50)
                state = if (res.success) {
                    state.copy(isLoading = false, items = res.data.content, error = null)
                } else {
                    state.copy(isLoading = false, error = res.message)
                }
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    /** "Delete for me": optimistically drop the row, then tell the server. */
    fun deleteConversation(id: Long) {
        state = state.copy(items = state.items.filterNot { it.id == id })
        viewModelScope.launch {
            try {
                api.deleteConversation(id)
            } catch (_: Exception) {
            }
        }
    }

    data class ChatListState(
        val isLoading: Boolean = false,
        val items: List<Conversation> = emptyList(),
        val error: String? = null
    )
}
