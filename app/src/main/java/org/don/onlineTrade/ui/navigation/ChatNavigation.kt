package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.chat.ChatRoute

const val chatNavigationRoute = "chat"
fun NavController.navigateToChat(navOptions: NavOptions? = null) {
    this.navigate(chatNavigationRoute, navOptions)
}

fun NavGraphBuilder.chatScreen() {
    composable(
        route = chatNavigationRoute,
    ) {
        ChatRoute()
    }
}