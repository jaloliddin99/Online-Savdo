package org.don.onlineTrade.ui.auth.register

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.data.remote.models.RegistrationBody


@Composable
fun SignUpRoute(
    navigateToMainScreen: () -> Unit,
    onLoginPage: () -> Unit
) {
    val welcomeViewModel = hiltViewModel<WelcomeViewModel>()
    val state = welcomeViewModel.state
    SignUpScreen(
        onSignInSignUp = { firstName, lastName, email, password, phoneNumber ->

            val body = RegistrationBody(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                phoneNumber = phoneNumber
            )
            welcomeViewModel.registerUser(
                body
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