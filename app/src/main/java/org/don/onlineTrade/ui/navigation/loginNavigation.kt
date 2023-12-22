package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.don.onlineTrade.ui.auth.forgotPassword.ForgotPasswordRoute
import org.don.onlineTrade.ui.auth.forgotPassword.ForgotPasswordScreen
import org.don.onlineTrade.ui.auth.forgotPassword.ResetPasswordRoute
import org.don.onlineTrade.ui.auth.login.SignInRoute


const val loginScreen = "loginScreen"

fun NavController.navigationToHome(navOptions: NavOptions? = null) {
    this.navigate(loginScreen, navOptions)
}

fun NavGraphBuilder.loginScreen(
    navigationToVerification: (email: String) -> Unit,
    forgotPassword: () -> Unit
) {
    composable(route = loginScreen) {
        SignInRoute(
            navigateToVerification = {
                navigationToVerification(it)
            },
            forgotPassword
        )
    }
}


const val forgotPasswordRoute = "forgotPasswordRoute/{fromLoginPage}"
fun NavGraphBuilder.forgotPasswordRoute(
    navigationToResetPage: (email: String, fromLoginPage: Boolean) -> Unit,
) {
    composable(
        route = forgotPasswordRoute,
        arguments = listOf(
            navArgument("fromLoginPage") {
                type = NavType.BoolType
            },
        )
    ) {
        val fromLoginPage = it.arguments?.getBoolean("fromLoginPage") ?: true
        ForgotPasswordRoute(
            goToResetPage = {
                navigationToResetPage(it, fromLoginPage)
            },
        )
    }
}

const val resetPasswordRoute = "resetPasswordRoute/{email}/{fromLoginPage}"
fun NavGraphBuilder.resetPasswordRoute(
    navigateToLoginPage: () -> Unit,
    onBackPressed: () -> Unit

) {
    composable(
        route = resetPasswordRoute,
        arguments = listOf(
            navArgument("email") {
                type = NavType.StringType
            },
            navArgument("fromLoginPage") {
                type = NavType.BoolType
            },
        )
    ) {
        val email = it.arguments?.getString("email") ?: return@composable
        val fromLoginPage = it.arguments?.getBoolean("fromLoginPage") ?: true

        ResetPasswordRoute(
            goToLoginPage = {
                navigateToLoginPage()
            },
            mEmail = email,
            fromLoginPage = fromLoginPage,
            onBackPressed = onBackPressed
        )
    }
}

