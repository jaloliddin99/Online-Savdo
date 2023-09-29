package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.don.onlineTrade.ui.presentProduct.ProductDetailsRoute


const val pDetailsNavigationRoute = "productDetails"
fun NavController.navigateToPresent(navOptions: NavOptions? = null) {
    this.navigate(pDetailsNavigationRoute, navOptions)
}

fun NavGraphBuilder.productDetailsScreen() {
    composable(
        route = "$pDetailsNavigationRoute/{param}",
        arguments = listOf(
            navArgument("param") {
                type = NavType.IntType
                defaultValue = 0
            }
        )
    ) { backStackEntry ->
        val param = backStackEntry.arguments?.getInt("param")
        ProductDetailsRoute(param?:0)
    }
}
