package org.don.bottomappbar.ui.dialogs.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.don.bottomappbar.domain.model.DarkThemeConfig
import org.don.bottomappbar.domain.model.ThemeBrand
import org.don.bottomappbar.domain.repository.UserDataRepository
import org.don.bottomappbar.utils.ModelPref
import java.io.Serializable
import javax.inject.Inject


@HiltViewModel
class SettingsDialogViewModel @Inject constructor() : ViewModel() {


    private val _sendUiChanges = Channel<UserEditableSettings>()
    val sendUiChanges = _sendUiChanges.receiveAsFlow()

    private fun sendUiEvents(settings: UserEditableSettings) {
        viewModelScope.launch {
            _sendUiChanges.send(settings)
        }
    }

    fun updateThemeBrand(themeBrand: ThemeBrand) {
        viewModelScope.launch {
            putThemeBrand(themeBrand)?.let { sendUiEvents(it) }
        }
    }

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            putDarkThemeConfig(darkThemeConfig)?.let { sendUiEvents(it) }
        }
    }

    fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        viewModelScope.launch {
            putUseDynamicColor(useDynamicColor)?.let { sendUiEvents(it) }
        }
    }


    private fun putUseDynamicColor(useDynamicColor: Boolean): UserEditableSettings? {
        val userSetting = ModelPref.get<UserEditableSettings>(SETTINGS_UI_STATE)
        userSetting?.let {
            ModelPref.put(it.copy(useDynamicColor = useDynamicColor), SETTINGS_UI_STATE)
        }
        return ModelPref.get<UserEditableSettings>(SETTINGS_UI_STATE)
    }

    private fun putThemeBrand(brand: ThemeBrand): UserEditableSettings? {
        val userSetting = ModelPref.get<UserEditableSettings>(SETTINGS_UI_STATE)
        userSetting?.let {
            ModelPref.put(it.copy(brand = brand), SETTINGS_UI_STATE)
        }
        return ModelPref.get<UserEditableSettings>(SETTINGS_UI_STATE)
    }

    private fun putDarkThemeConfig(darkThemeConfig: DarkThemeConfig): UserEditableSettings? {
        val userSetting = ModelPref.get<UserEditableSettings>(SETTINGS_UI_STATE)
        userSetting?.let {
            ModelPref.put(it.copy(darkThemeConfig = darkThemeConfig), SETTINGS_UI_STATE)
        }
        return ModelPref.get<UserEditableSettings>(SETTINGS_UI_STATE)
    }
}

private const val TAG = "SettingsDialogViewModel"

/**
 * Represents the settings which the user can edit within the app.
 */
data class UserEditableSettings(
    val brand: ThemeBrand,
    val useDynamicColor: Boolean,
    val darkThemeConfig: DarkThemeConfig,
):Serializable

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settings: UserEditableSettings) : SettingsUiState
}
