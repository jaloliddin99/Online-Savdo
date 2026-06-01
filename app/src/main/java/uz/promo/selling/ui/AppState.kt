package uz.promo.selling.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import uz.promo.selling.ui.navigation.Screen


@Composable
fun rememberNiaAppState(
    navController: NavHostController = rememberNavController(),
): ApplicationState {
    return remember(
        navController,
    ) {
        ApplicationState(
            navController,
        )
    }
}


@Stable
class ApplicationState(
    val navController: NavHostController,
){
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination
    val currentTopLevelDestination: NavItems?
        @Composable get() = when (currentDestination?.route) {
            Screen.Home.route -> NavItems.Home
            Screen.AddProduct.route -> NavItems.AddProduct
            Screen.Saved.route -> NavItems.Saved
            Screen.Profile.route -> NavItems.Profile
            Screen.Categories.route -> NavItems.Categories
            Screen.MyProducts.route -> NavItems.MyPosts
            Screen.ProfileUpdate.route -> NavItems.ProfileUpdate
            Screen.PasswordUpdate.route -> NavItems.PasswordUpdate
            Screen.Notifications.route -> NavItems.Notifications
            Screen.FilterCategory.ROUTE -> NavItems.FilterCategories
            else -> null
        }

    fun navigateToTopLevelDestination(topLevelDestination: NavItems) {
        // Auth-required tabs: redirect to Welcome if not logged in
        val requiresAuth = topLevelDestination in setOf(
            NavItems.MyPosts, NavItems.AddProduct, NavItems.Saved, NavItems.Profile
        )
        if (requiresAuth && !isUserLoggedIn()) {
            navController.navigate(Screen.Welcome.route)
            return
        }

        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            NavItems.Home -> navController.navigate(Screen.Home.route, topLevelNavOptions)
            NavItems.MyPosts -> navController.navigate(Screen.MyProducts.route, topLevelNavOptions)
            NavItems.AddProduct -> navController.navigate(Screen.AddProduct.route, topLevelNavOptions)
            NavItems.Saved -> navController.navigate(Screen.Saved.route, topLevelNavOptions)
            NavItems.Profile -> navController.navigate(Screen.Profile.route, topLevelNavOptions)
            else -> {}
        }
    }

    fun navigateToSearch() {
        navController.navigate(Screen.Search.route)
    }


}
