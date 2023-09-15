package org.don.onlineTrade.ui.auth.register

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun SignUpRoute(
    navigateToMainScreen: () -> Unit,
    onLoginPage: () -> Unit
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
            welcomeViewModel.signInAsGuest(navigateToMainScreen)
        },
        registrationSuccess = navigateToMainScreen,
        onLoginPage = onLoginPage
    )
}