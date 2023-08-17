package org.don.bottomappbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.don.bottomappbar.ui.MainScreenView
import org.don.bottomappbar.ui.dialogs.settings.SettingsDialogViewModel
import org.don.bottomappbar.ui.theme.BottomAppbarTheme
import org.don.bottomappbar.utils.SharedPref


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {

            CompositionLocalProvider {
                BottomAppbarTheme(
                    darkTheme = SharedPref.darkTheme,
                    androidTheme = SharedPref.androidTheme,
                    disableDynamicTheming = SharedPref.disableDynamicTheming
                ) {
                    MainScreenView()
                }
            }
        }
    }
}

