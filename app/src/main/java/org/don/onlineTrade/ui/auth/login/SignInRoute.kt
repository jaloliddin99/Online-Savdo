package org.don.onlineTrade.ui.auth.login

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.ui.auth.register.SignUpScreen
import org.don.onlineTrade.ui.auth.register.WelcomeViewModel


@Composable
fun SignInRoute(
    navigateToMainScreen: () -> Unit,
) {
    val welcomeViewModel = hiltViewModel<LoginViewModel>()
    val state = welcomeViewModel.state
    SignInScreen(
        onSignInSignUp = { email, password ->
            welcomeViewModel.registerUser(
                email,
                password,
            )
        },
        state = state.value,
        onSignInAsGuest = {
            welcomeViewModel.signInAsGuest(navigateToMainScreen)
        },
        loginSuccess = navigateToMainScreen
    )
}