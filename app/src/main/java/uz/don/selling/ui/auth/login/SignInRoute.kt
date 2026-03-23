package uz.don.selling.ui.auth.login

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import uz.don.selling.data.remote.models.LoginBody


@Composable
fun SignInRoute(
    navigateToVerification: (email: String) -> Unit,
    forgotPassword: () -> Unit,
    onGoogleSignIn: () -> Unit = {}
) {
    val welcomeViewModel = hiltViewModel<LoginViewModel>()
    val state = welcomeViewModel.state

    SignInScreen(
        onSignInSignUp = { email, password ->
            val loginBody = LoginBody(email, password)
            welcomeViewModel.registerUser(
                loginBody,
            )
        },
        state = state.value,
        loginSuccess = navigateToVerification,
        forgotPassword = forgotPassword,
        onGoogleSignIn = onGoogleSignIn
    )
}
