package org.don.bottomappbar.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import org.don.bottomappbar.BottomNavContentScreens


const val homeNavigationRoute = "home"
fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(homeNavigationRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(onTopicClick: (String) -> Unit) {
    composable(
        route = homeNavigationRoute,
    ) {
        BottomNavContentScreens.HomeScreen()
    }
}
