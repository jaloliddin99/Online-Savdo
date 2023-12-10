package org.don.onlineTrade.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.don.onlineTrade.ui.auth.register.SignUpRoute
import org.don.onlineTrade.ui.auth.verify.VerificationRoute


const val verification = "verification_screen/{email}"

fun NavController.navigateToVerification(navOptions: NavOptions? = null) {
    this.navigate(verification, navOptions)
}

fun NavGraphBuilder.verificationScreen(
    navigateToMainScreen: () -> Unit,
    onBackPressed: () -> Unit
) {
    composable(route = verification,
        arguments = listOf(
            navArgument("email") {
                type = NavType.StringType
            }
        )) {backStackEntry ->
        val emailParam = backStackEntry.arguments?.getString("email")
        emailParam?.let {
            VerificationRoute(
                emailParam = it,
                navigateToMainScreen = navigateToMainScreen,
                onBackPressed = onBackPressed
            )
        }
    }
}