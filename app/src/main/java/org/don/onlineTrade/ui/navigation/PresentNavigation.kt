package org.don.onlineTrade.ui.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.don.onlineTrade.ui.home.HomeRoute
import org.don.onlineTrade.ui.presentProduct.PresentRoute


const val presentProductNavigationRoute = "presentProduct"
fun NavController.navigateToPresent(navOptions: NavOptions? = null) {
    this.navigate(presentProductNavigationRoute, navOptions)
}

fun NavGraphBuilder.presentProductScreen() {
    composable(
        route = "$presentProductNavigationRoute/{param}",
        arguments = listOf(
            navArgument("param") {
                type = NavType.IntType
                defaultValue = 0
            }
        )
    ) { backStackEntry ->
        val param = backStackEntry.arguments?.getInt("param")
        Log.d("TAG", "presentProductScreendwdwadawd $param")
        PresentRoute(param?:0)
    }
}
