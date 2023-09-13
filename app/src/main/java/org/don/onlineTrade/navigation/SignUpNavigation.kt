package org.don.onlineTrade.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val welcomeScreen = "welcome_screen"

fun NavController.navigateToWelcome(navOptions: NavOptions? = null) {
    this.navigate(welcomeScreen, navOptions)
}

fun NavGraphBuilder.welcomeScreen(
    navigateToSignUp: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToSurvey: () -> Unit,
) {
    composable(route = welcomeScreen) {
        SignUpRoute(
            onNavigateToSignIn = {
                navigateToSignIn()
            },
            onNavigateToSignUp = {
                navigateToSignUp()
            },
            onSignInAsGuest = {
                navigateToSurvey()
            },
        )
    }
}