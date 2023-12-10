package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.ui.auth.login.SignInRoute


const val loginScreen = "loginScreen"

fun NavController.navigationToHome(navOptions: NavOptions? = null) {
    this.navigate(loginScreen, navOptions)
}

fun NavGraphBuilder.loginScreen(
    navigationToVerification: (email: String) -> Unit,
) {
    composable(route = loginScreen) {
        SignInRoute(
            navigateToVerification = {
                navigationToVerification(it)
            },
        )
    }
}