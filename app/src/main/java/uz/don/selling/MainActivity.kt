package uz.don.selling

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.don.selling.domain.model.DarkThemeConfig
import uz.don.selling.domain.model.ThemeBrand
import uz.don.selling.ui.MainScreenView
import uz.don.selling.ui.splash.SplashScreen
import uz.don.selling.ui.dialogs.settings.SETTINGS_UI_STATE
import uz.don.selling.ui.dialogs.settings.SettingsDialogViewModel
import uz.don.selling.ui.dialogs.settings.UserEditableSettings
import uz.don.selling.ui.theme.OnlineMarketTheme
import uz.don.selling.utils.LocaleManager
import uz.don.selling.utils.ModelPref
import uz.don.selling.utils.SharedPref
import uz.don.selling.data.worker.TokenRefreshScheduler
import uz.don.selling.utils.runTimePermission.OnRunTimePermissionListener
import uz.don.selling.utils.runTimePermission.RunTimePermission


@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnRunTimePermissionListener {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            LocaleManager.setLocale(newBase, SharedPref.language)
        )
    }
    private val viewModel: SettingsDialogViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        RunTimePermission().permissionList(this, this)

        if (SharedPref.refreshToken.isNotBlank()) {
            TokenRefreshScheduler.refreshNow(this)
        }
        val userSetting = ModelPref.get<UserEditableSettings>(SETTINGS_UI_STATE)
        val settingsUiState = if (userSetting != null) userSetting
        else {
            val data = UserEditableSettings(
                ThemeBrand.ANDROID,
                false,
                DarkThemeConfig.LIGHT
            )
            ModelPref.put(data, SETTINGS_UI_STATE)
            data
        }

        var state: UserEditableSettings by mutableStateOf(settingsUiState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sendUiChanges.collectLatest {
                    state = it
                }
            }
        }
        var showSplash by mutableStateOf(true)

        setContent {
            val darkTheme = shouldUseDarkTheme(state.darkThemeConfig)
            OnlineMarketTheme(
                darkTheme = darkTheme,
                androidTheme = shouldUseAndroidTheme(state.brand),
                disableDynamicTheming = shouldDisableDynamicTheming(state.useDynamicColor)
            ) {
                if (showSplash) {
                    SplashScreen(
                        onAnimationEnd = { showSplash = false }
                    )
                } else {
                    MainScreenView(state,
                        restartApp = {
                            finish()
                            startActivity(Intent(this, MainActivity::class.java))
                        })
                }
            }
        }
    }

    override fun onPermissionGranted() {

    }


    override fun onPermissionDenied() {

    }
}

@Composable
private fun shouldUseDarkTheme(
    darkThemeConfig: DarkThemeConfig,
): Boolean = when (darkThemeConfig) {
    DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    DarkThemeConfig.LIGHT -> false
    DarkThemeConfig.DARK -> true
}


/**
 * Returns `true` if the Android theme should be used, as a function of the [uiState].
 */
@Composable
private fun shouldUseAndroidTheme(
    uiState: ThemeBrand,
): Boolean = when (uiState) {
    ThemeBrand.DEFAULT -> false
    ThemeBrand.ANDROID -> true
}

/**
 * Returns `true` if the dynamic color is disabled, as a function of the [uiState].
 */
@Composable
private fun shouldDisableDynamicTheming(
    uiState: Boolean,
): Boolean = !uiState

