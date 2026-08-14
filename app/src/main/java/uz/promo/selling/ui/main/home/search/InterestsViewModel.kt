package uz.promo.selling.ui.main.home.search

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

/**
 * Holds the user's explicitly-chosen interest categories. Drives the
 * "personalize with AI" card on the SearchScreen and the interest-based
 * recommendations search.
 */
@HiltViewModel
class InterestsViewModel @Inject constructor(
    private val apiInterface: ApiInterface
) : ViewModel() {

    var interests by mutableStateOf<List<Long>>(emptyList())
        private set

    private var loaded by mutableStateOf(false)

    var isSaving by mutableStateOf(false)
        private set

    /** Set when a save failed, so the screen can tell the user instead of silently doing nothing. */
    var saveFailed by mutableStateOf(false)
        private set

    fun clearSaveFailed() {
        saveFailed = false
    }

    private var dismissed by mutableStateOf(SharedPref.interestsCardDismissed)

    private val isLoggedIn: Boolean get() = SharedPref.userId >= 0

    /** Show the prompt card only to a logged-in user with no interests who hasn't dismissed it. */
    val showCard: Boolean
        get() = isLoggedIn && loaded && interests.isEmpty() && !dismissed

    /** True once the user has interests we can recommend from. */
    val hasInterests: Boolean
        get() = loaded && interests.isNotEmpty()

    init {
        loadInterests()
    }

    fun loadInterests() {
        if (!isLoggedIn) {
            loaded = true
            return
        }
        viewModelScope.launch {
            try {
                // The server sends data: null on some error shapes; Gson happily puts that
                // into the non-null field, so guard before it reaches composition.
                @Suppress("USELESS_ELVIS")
                interests = apiInterface.getInterests().data ?: emptyList()
            } catch (_: Exception) {
                // Treat any failure as "no interests" — the card simply won't show.
            } finally {
                loaded = true
            }
        }
    }

    fun saveInterests(categoryIds: List<Long>, onSaved: (List<Long>) -> Unit = {}) {
        if (!isLoggedIn) {
            saveFailed = true
            return
        }
        isSaving = true
        saveFailed = false
        viewModelScope.launch {
            var saved: List<Long>? = null
            try {
                @Suppress("USELESS_ELVIS")
                saved = apiInterface.updateInterests(
                    body = mapOf("categoryIds" to categoryIds)
                ).data ?: emptyList()
                interests = saved
            } catch (_: Exception) {
                // Keep the previous state; the user can retry from the card.
                saveFailed = true
            } finally {
                isSaving = false
            }
            // Outside the try: an exception thrown by the UI callback must not be
            // reported as a failed save.
            saved?.let(onSaved)
        }
    }

    fun dismissCard() {
        dismissed = true
        SharedPref.interestsCardDismissed = true
    }
}
