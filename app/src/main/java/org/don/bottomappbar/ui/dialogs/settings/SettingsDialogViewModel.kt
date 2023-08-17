package org.don.bottomappbar.ui.dialogs.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.don.bottomappbar.domain.model.DarkThemeConfig
import org.don.bottomappbar.domain.model.ThemeBrand
import org.don.bottomappbar.domain.repository.UserDataRepository
import javax.inject.Inject


@HiltViewModel
class SettingsDialogViewModel : ViewModel() {


    fun updateThemeBrand(themeBrand: ThemeBrand) {
        viewModelScope.launch {

        }
    }

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {

        }
    }

    fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        viewModelScope.launch {

        }
    }


}

/**
 * Represents the settings which the user can edit within the app.
 */
data class UserEditableSettings(
    val brand: ThemeBrand,
    val useDynamicColor: Boolean,
    val darkThemeConfig: DarkThemeConfig,
)

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settings: UserEditableSettings) : SettingsUiState
}
