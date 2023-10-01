package org.don.onlineTrade.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.saved.SavedRoute
import org.don.onlineTrade.ui.saved.SavedViewModel


const val savedNavigationRoute = "saved"
fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    this.navigate(savedNavigationRoute, navOptions)
}

fun NavGraphBuilder.settingsScreen() {
    composable(route = savedNavigationRoute) {
        val savedViewModel = hiltViewModel<SavedViewModel>()
        val state = savedViewModel.state.value
        SavedRoute(
            state = state
        )
    }
}
