package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.auth.register.SignUpRoute

const val welcomeScreen = "welcome_screen"

fun NavController.navigateToWelcome(navOptions: NavOptions? = null) {
    this.navigate(welcomeScreen, navOptions)
}

fun NavGraphBuilder.registrationScreen(
    navigateToMainScreen: () -> Unit,
    onLoginPage: () -> Unit
) {
    composable(route = welcomeScreen) {
        SignUpRoute(
            navigateToMainScreen = {
                navigateToMainScreen()
            },
            onLoginPage = {
                onLoginPage()
            }
        )
    }
}