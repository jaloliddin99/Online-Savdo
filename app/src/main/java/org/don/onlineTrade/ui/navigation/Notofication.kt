package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.notification.NotificationsRoute

const val notificationsNavigationRoute = "notifications"
fun NavController.navigateToNotifications(navOptions: NavOptions? = null) {
    this.navigate(notificationsNavigationRoute, navOptions)
}

fun NavGraphBuilder.notificationsScreen() {
    composable(
        route = notificationsNavigationRoute,
    ) {
        NotificationsRoute()
    }
}