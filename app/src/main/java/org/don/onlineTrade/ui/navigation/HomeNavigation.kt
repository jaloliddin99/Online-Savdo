package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.home.HomeRoute


const val homeNavigationRoute = "home"
fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(homeNavigationRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    navigateToProduct: (Int) -> Unit,
    navigateToCategory: (Int) -> Unit
) {
    composable(
        route = homeNavigationRoute,

    ) {
        HomeRoute(
            navigateToProduct = navigateToProduct,
            navigateToCategory = navigateToCategory
        )
    }
}
