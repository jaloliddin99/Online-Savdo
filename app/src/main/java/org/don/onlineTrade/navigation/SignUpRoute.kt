package org.don.onlineTrade.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.ui.auth.register.SignUpScreen
import org.don.onlineTrade.ui.auth.register.WelcomeViewModel


@Composable
fun SignUpRoute(
    onNavigateToSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onSignInAsGuest: () -> Unit,
) {
    val welcomeViewModel = hiltViewModel<WelcomeViewModel>()
    val state = welcomeViewModel.state
    SignUpScreen(
        onSignInSignUp = { email, password, phoneNumber ->
            welcomeViewModel.registerUser(
                "Jalol",
                email,
                password,
                password,
                phoneNumber
            )
        },
        state = state.value,
        onSignInAsGuest = {
            welcomeViewModel.signInAsGuest(onSignInAsGuest)
        },
        registrationSuccess = {
            welcomeViewModel.handleContinue(
                email = email,
                onNavigateToSignIn = onNavigateToSignIn,
                onNavigateToSignUp = onNavigateToSignUp,
            )
        }
    )
}