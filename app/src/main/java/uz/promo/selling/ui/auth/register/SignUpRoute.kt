package uz.promo.selling.ui.auth.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import uz.promo.selling.data.remote.models.RegistrationBody


@Composable
fun SignUpRoute(
    navigateToVerification: (email: String) -> Unit,
    onLoginPage: () -> Unit,
    onGoogleSignIn: () -> Unit = {},
    brandingModifier: Modifier = Modifier
) {
    val welcomeViewModel = hiltViewModel<WelcomeViewModel>()
    val state = welcomeViewModel.state

    // Navigate to the OTP screen once; clear the result so Back from the
    // verification screen doesn't bounce the user forward again.
    LaunchedEffect(state.value.registerMain) {
        if (state.value.registerMain != null) {
            navigateToVerification(state.value.email)
            welcomeViewModel.consumeRegisterResult()
        }
    }

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
        registrationSuccess = {},
        onLoginPage = onLoginPage,
        onGoogleSignIn = onGoogleSignIn,
        brandingModifier = brandingModifier
    )
}
