package org.don.bottomappbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import org.don.bottomappbar.ui.theme.BottomAppbarTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            BottomAppbarTheme {
                MainScreenView()
            }
        }
    }
}

