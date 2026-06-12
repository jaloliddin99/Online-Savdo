package uz.promo.selling

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
import uz.promo.selling.domain.model.DarkThemeConfig
import uz.promo.selling.domain.model.ThemeBrand
import uz.promo.selling.ui.MainScreenView
import uz.promo.selling.ui.splash.SplashScreen
import uz.promo.selling.ui.dialogs.settings.SETTINGS_UI_STATE
import uz.promo.selling.ui.dialogs.settings.SettingsDialogViewModel
import uz.promo.selling.ui.dialogs.settings.UserEditableSettings
import uz.promo.selling.ui.theme.OnlineMarketTheme
import uz.promo.selling.utils.LocaleManager
import uz.promo.selling.utils.ModelPref
import uz.promo.selling.utils.SharedPref
import uz.promo.selling.data.worker.TokenRefreshScheduler
import uz.promo.selling.utils.runTimePermission.OnRunTimePermissionListener
import uz.promo.selling.utils.runTimePermission.RunTimePermission
import java.util.Locale


@AndroidEntryPoint
class MainActivity : ComponentActivity(), OnRunTimePermissionListener {

    val language: String = Locale.getDefault().language


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            LocaleManager.setLocale(newBase, SharedPref.language.ifEmpty {
                val lan = when(language){
                    "en" -> "en"
                    "ru" -> "ru"
                    else -> "uz"
                }
                SharedPref.language = lan
                language
            })
        )
    }

    private val viewModel: SettingsDialogViewModel by viewModels()

    // Android 13+ requires runtime consent before ANY notification is shown.
    private val notificationPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { /* denial is fine — the user can enable it later in settings */ }

    private fun requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        requestNotificationPermission()

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
            // In-place language switching: the whole tree reads strings from a
            // locale-wrapped context, so bumping appLanguage re-resolves every
            // stringResource instantly, and key() below rebuilds the nav graph
            // (fresh ViewModels -> backend data reloads in the new language).
            // No activity restart, no splash.
            var appLanguage by androidx.compose.runtime.remember {
                mutableStateOf(SharedPref.language)
            }
            val localizedContext = androidx.compose.runtime.remember(appLanguage) {
                // Keep the ACTIVITY as the base context (Hilt's viewModel factory
                // walks LocalContext looking for it) and only override resources
                // with the locale-adjusted ones.
                val configContext = LocaleManager.setLocale(this, appLanguage) ?: this
                object : android.content.ContextWrapper(this) {
                    override fun getResources(): android.content.res.Resources =
                        configContext.resources
                }
            }

            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.ui.platform.LocalContext provides localizedContext,
                androidx.compose.ui.platform.LocalConfiguration provides
                        localizedContext.resources.configuration
            ) {
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
                        androidx.compose.runtime.key(appLanguage) {
                            MainScreenView(
                                state,
                                restartApp = {
                                    appLanguage = SharedPref.language
                                })
                        }
                    }
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

