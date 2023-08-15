package org.don.bottomappbar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import org.don.bottomappbar.navigation.navigateToChat
import org.don.bottomappbar.navigation.navigateToHome
import org.don.bottomappbar.navigation.navigateToSearch
import org.don.bottomappbar.navigation.navigateToSettingsGraph


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
            "home" -> NavItems.Home
            "chat" -> NavItems.Chat
            "settings" -> NavItems.Settings
            else -> null
        }



    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: NavItems) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            NavItems.Home -> navController.navigateToHome(topLevelNavOptions)
            NavItems.Chat -> navController.navigateToChat(topLevelNavOptions)
            NavItems.Settings -> navController.navigateToSettingsGraph(topLevelNavOptions)
        }
    }

    fun navigateToSearch() {
        navController.navigateToSearch()
    }


}

val currentDestination: (String) -> NavItems? = { destination: String ->
    when (destination) {
        "home" -> NavItems.Home
        "chat" -> NavItems.Chat
        "settings" -> NavItems.Settings
        else -> null
    }
}
