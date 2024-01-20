package org.don.onlineTrade.ui.auth.register

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.data.remote.models.RegistrationBody


@Composable
fun SignUpRoute(
    navigateToVerification: (email: String) -> Unit,
    onLoginPage: () -> Unit
) {
    val welcomeViewModel = hiltViewModel<WelcomeViewModel>()
    val state = welcomeViewModel.state
    SignUpScreen(
        onSignInSignUp = { firstName, email, password, phoneNumber ->

            val body = RegistrationBody(
                name = firstName,
                email = email,
                password = password,
                phoneNumber = phoneNumber
            )
            welcomeViewModel.registerUser(
                body
            )
        },
        state = state.value,
        registrationSuccess = navigateToVerification,
        onLoginPage = onLoginPage
    )
}