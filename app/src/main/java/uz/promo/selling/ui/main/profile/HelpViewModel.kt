package uz.promo.selling.ui.main.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.utils.SharedPref
import javax.inject.Inject

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val api: ApiInterface
) : ViewModel() {

    var sending by mutableStateOf(false)
        private set

    fun send(message: String, onDone: (Boolean) -> Unit) {
        val text = message.trim()
        if (text.isEmpty() || sending) return
        sending = true
        viewModelScope.launch {
            val ok = try {
                api.sendSupportMessage(SharedPref.deviceToken, mapOf("message" to text)).success
            } catch (e: Exception) {
                false
            }
            sending = false
            onDone(ok)
        }
    }
}
