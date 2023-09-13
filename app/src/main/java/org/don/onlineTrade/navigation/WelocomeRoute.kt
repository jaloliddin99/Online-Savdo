package org.don.onlineTrade.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.ui.auth.SignUpScreen
import org.don.onlineTrade.ui.auth.WelcomeViewModel


@Composable
fun SignUpRoute(
    onNavigateToSignIn: (email: String) -> Unit,
    onNavigateToSignUp: (email: String) -> Unit,
    onSignInAsGuest: () -> Unit,
    welcomeViewModel: WelcomeViewModel = hiltViewModel()
) {
    SignUpScreen(
        onSignInSignUp = { email ->
            welcomeViewModel.handleContinue(
                email = email,
                onNavigateToSignIn = onNavigateToSignIn,
                onNavigateToSignUp = onNavigateToSignUp,
            )
        },
        onSignInAsGuest = {
            welcomeViewModel.signInAsGuest(onSignInAsGuest)
        },
    )
}