package org.don.onlineTrade.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.auth.login.SignInRoute
import org.don.onlineTrade.ui.auth.login.SignInScreen


const val loginScreen = "loginScreen"

fun NavController.navigationToHome(navOptions: NavOptions? = null) {
    this.navigate(loginScreen, navOptions)
}

fun NavGraphBuilder.loginScreen(
    navigateToMainScreen: () -> Unit,
) {
    composable(route = loginScreen) {
        SignInRoute(
            navigateToMainScreen = {
                navigateToMainScreen()
            },
        )
    }
}