package org.don.onlineTrade.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Now in Android navigation default values.
 */
object NavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}
