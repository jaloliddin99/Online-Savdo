package org.don.onlineTrade.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val welcomeScreen = "welcome_screen"

fun NavController.navigateToWelcome(navOptions: NavOptions? = null) {
    this.navigate(welcomeScreen, navOptions)
}

fun NavGraphBuilder.registrationScreen(
    navigateToMainScreen: () -> Unit,
) {
    composable(route = welcomeScreen) {
        SignUpRoute(
            navigateToMainScreen = {
                navigateToMainScreen()
            },
        )
    }
}