package org.don.onlineTrade.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import org.don.onlineTrade.ui.navigation.addNavigationRoute
import org.don.onlineTrade.ui.navigation.categoriesNavigationRoute
import org.don.onlineTrade.ui.navigation.filterCategoryNavigationRoute
import org.don.onlineTrade.ui.navigation.homeNavigationRoute
import org.don.onlineTrade.ui.navigation.myProductsNavigationRoute
import org.don.onlineTrade.ui.navigation.navigateToHome
import org.don.onlineTrade.ui.navigation.navigateToPosts
import org.don.onlineTrade.ui.navigation.navigateToProfile
import org.don.onlineTrade.ui.navigation.navigateToSearch
import org.don.onlineTrade.ui.navigation.navigateToSettingsGraph
import org.don.onlineTrade.ui.navigation.notificationsNavigationRoute
import org.don.onlineTrade.ui.navigation.passwordUpdateNavigationRoute
import org.don.onlineTrade.ui.navigation.profileNavigationRoute
import org.don.onlineTrade.ui.navigation.profileUpdateNavigationRoute
import org.don.onlineTrade.ui.navigation.regionsNavigationRoute
import org.don.onlineTrade.ui.navigation.savedNavigationRoute


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
            homeNavigationRoute -> NavItems.Home
            addNavigationRoute -> NavItems.AddProduct
            savedNavigationRoute -> NavItems.Saved
            profileNavigationRoute -> NavItems.Profile
            categoriesNavigationRoute -> NavItems.Categories
            myProductsNavigationRoute -> NavItems.MyPosts
            regionsNavigationRoute -> NavItems.Regions
            profileUpdateNavigationRoute -> NavItems.ProfileUpdate
            passwordUpdateNavigationRoute -> NavItems.PasswordUpdate
            notificationsNavigationRoute -> NavItems.Notifications
            filterCategoryNavigationRoute -> NavItems.FilterCategories
            else -> null
        }

    fun navigateToTopLevelDestination(topLevelDestination: NavItems) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            NavItems.Home -> navController.navigateToHome(topLevelNavOptions)
            NavItems.AddProduct -> navController.navigateToPosts(topLevelNavOptions)
            NavItems.Saved -> navController.navigateToSettingsGraph(topLevelNavOptions)
            NavItems.Profile -> navController.navigateToProfile(topLevelNavOptions)
            else -> null
        }
    }

    fun navigateToSearch() {
        navController.navigateToSearch()
    }


}
