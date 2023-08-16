package org.don.bottomappbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import org.don.bottomappbar.ui.MainScreenView
import org.don.bottomappbar.ui.theme.BottomAppbarTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            CompositionLocalProvider {
                BottomAppbarTheme {
                    MainScreenView()
                }
            }
        }
    }
}

