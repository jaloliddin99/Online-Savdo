package org.don.onlineTrade

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.don.onlineTrade.domain.model.DarkThemeConfig
import org.don.onlineTrade.ui.MainScreenView
import org.don.onlineTrade.ui.dialogs.settings.SettingsDialogViewModel
import org.don.onlineTrade.ui.dialogs.settings.UserEditableSettings
import org.don.onlineTrade.ui.theme.IELTSAIExaminerTheme
import androidx.compose.runtime.setValue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.insets.ProvideWindowInsets
import org.don.onlineTrade.app.App
import org.don.onlineTrade.domain.model.ThemeBrand
import org.don.onlineTrade.ui.add.AddProductScreenViewModel
import org.don.onlineTrade.ui.dialogs.settings.SETTINGS_UI_STATE
import org.don.onlineTrade.ui.navigation.myProductsNavigationRoute
import org.don.onlineTrade.ui.navigation.pDetailsNavigationRoute
import org.don.onlineTrade.utils.LocaleManager
import org.don.onlineTrade.utils.ModelPref
import org.don.onlineTrade.utils.SharedPref
import org.don.onlineTrade.utils.runTimePermission.OnRunTimePermissionListener
import org.don.onlineTrade.utils.runTimePermission.RunTimePermission


@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnRunTimePermissionListener {
    val addProductViewModel: AddProductScreenViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            LocaleManager.setLocale(newBase, SharedPref.language)
        )
    }
    private val viewModel: SettingsDialogViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        //LocaleManager.setLocale(this, SharedPref.language)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        RunTimePermission().permissionList(this, this)
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


        setContent {
            val darkTheme = shouldUseDarkTheme(state.darkThemeConfig)
            IELTSAIExaminerTheme(
                darkTheme = darkTheme,
                androidTheme = shouldUseAndroidTheme(state.brand),
                disableDynamicTheming = shouldDisableDynamicTheming(state.useDynamicColor)
            ) {
                MainScreenView(state,addProductViewModel = addProductViewModel,
                    restartApp = {
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
                    })
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

