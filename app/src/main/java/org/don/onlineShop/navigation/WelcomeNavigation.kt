package org.don.onlineShop.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val welcomeScreen = "welcome_screen"

fun NavController.navigateToWelcome(navOptions: NavOptions? = null) {
    this.navigate(welcomeScreen, navOptions)
}

fun NavGraphBuilder.welcomeScreen(
    navigateToSignUp: (String) -> Unit,
    navigateToSignIn: (String) -> Unit,
    navigateToSurvey: () -> Unit,
) {
    composable(route = welcomeScreen) {
        WelcomeRoute(
            onNavigateToSignIn = {
                navigateToSignIn(it)
            },
            onNavigateToSignUp = {
                navigateToSignUp(it)
            },
            onSignInAsGuest = {
                navigateToSurvey()
            },
        )
    }
}