package uz.promo.selling.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.remote.models.chat.StartConversationBody
import javax.inject.Inject

/** Opens (or reuses) the conversation for a listing, then hands back its id. */
@HiltViewModel
class ChatStartViewModel @Inject constructor(
    private val api: ApiInterface
) : ViewModel() {

    var isStarting by mutableStateOf(false)
        private set

    fun start(postId: Int, onSuccess: (Long) -> Unit, onError: () -> Unit) {
        if (isStarting) return
        isStarting = true
        viewModelScope.launch {
            try {
                val res = api.startConversation(StartConversationBody(postId))
                if (res.success) onSuccess(res.data.id) else onError()
            } catch (e: Exception) {
                onError()
            }
            isStarting = false
        }
    }
}
