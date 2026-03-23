package uz.don.onlineTrade.ui.auth.register

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import uz.don.onlineTrade.data.remote.models.RegistrationBody


@Composable
fun SignUpRoute(
    navigateToVerification: (email: String) -> Unit,
    onLoginPage: () -> Unit,
    onGoogleSignIn: () -> Unit = {}
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
            welcomeViewModel.registerUser(body)
        },
        state = state.value,
        registrationSuccess = navigateToVerification,
        onLoginPage = onLoginPage,
        onGoogleSignIn = onGoogleSignIn
    )
}
