package org.don.iaExaminer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.iaExaminer.ui.settings.SettingsRoute


private const val SETTINGS_GRAPH_ROUTE_PATTERN = "settings"
const val settingsRoute = "settings"
fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    this.navigate(SETTINGS_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavGraphBuilder.settingsScreen() {
    composable(route = settingsRoute) {
        SettingsRoute()
    }
}
