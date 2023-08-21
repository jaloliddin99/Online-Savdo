package org.don.iaExaminer.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.iaExaminer.ui.auth.WelcomeScreen
import org.don.iaExaminer.ui.auth.WelcomeViewModel


@Composable
fun WelcomeRoute(
    onNavigateToSignIn: (email: String) -> Unit,
    onNavigateToSignUp: (email: String) -> Unit,
    onSignInAsGuest: () -> Unit,
    welcomeViewModel: WelcomeViewModel = hiltViewModel()
) {
    WelcomeScreen(
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