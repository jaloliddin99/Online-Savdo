package uz.don.selling.ui.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import uz.don.selling.data.remote.models.LoginBody


@Composable
fun SignInRoute(
    navigateToVerification: (email: String) -> Unit,
    forgotPassword: () -> Unit,
    onGoogleSignIn: () -> Unit = {},
    brandingModifier: Modifier = Modifier
) {
    val welcomeViewModel = hiltViewModel<LoginViewModel>()
    val state = welcomeViewModel.state

    // Navigate to the OTP screen exactly once when the password step succeeds.
    // Clearing the result afterwards prevents bouncing back to OTP when the user
    // presses Back from the verification screen.
    LaunchedEffect(state.value.registerMain) {
        if (state.value.registerMain != null) {
            navigateToVerification(state.value.email)
            welcomeViewModel.consumeLoginResult()
        }
    }

    SignInScreen(
        onSignInSignUp = { email, password ->
            val loginBody = LoginBody(email, password)
            welcomeViewModel.registerUser(
                loginBody,
            )
        },
        state = state.value,
        loginSuccess = {},
        forgotPassword = forgotPassword,
        onGoogleSignIn = onGoogleSignIn,
        brandingModifier = brandingModifier
    )
}
