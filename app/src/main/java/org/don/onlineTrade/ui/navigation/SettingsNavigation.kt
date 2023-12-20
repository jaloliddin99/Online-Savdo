package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.saved.SavedRoute


const val savedNavigationRoute = "saved"
fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) {
    this.navigate(savedNavigationRoute, navOptions)
}

fun NavGraphBuilder.savedScreen(
    navigateToProduct: (Int) -> Unit
) {
    composable(route = savedNavigationRoute) {
        SavedRoute(
            navigateToProduct = navigateToProduct
        )
    }
}
