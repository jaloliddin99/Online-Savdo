package uz.promo.selling.ui.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import uz.promo.selling.data.remote.models.LoginBody
import uz.promo.selling.data.worker.TokenRefreshScheduler
import uz.promo.selling.utils.FcmTokenSync
import uz.promo.selling.utils.SharedPref


@Composable
fun SignInRoute(
    navigateToVerification: (email: String) -> Unit,
    navigateToMainScreen: () -> Unit,
    forgotPassword: () -> Unit,
    onSignUpPage: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {},
    brandingModifier: Modifier = Modifier
) {
    val welcomeViewModel = hiltViewModel<LoginViewModel>()
    val state = welcomeViewModel.state
    val context = LocalContext.current

    // Login now returns the tokens directly — store the session and go straight
    // to the main screen. Only unverified accounts are sent to the OTP screen
    // (the backend has just emailed them a code). Clearing the result prevents
    // re-navigation when returning to this screen.
    LaunchedEffect(state.value.registerMain) {
        val result = state.value.registerMain
        if (result != null) {
            if (result.status) {
                SharedPref.loginTime = System.currentTimeMillis()
                SharedPref.deviceToken = "Bearer ${result.token}"
                SharedPref.refreshToken = result.refreshToken ?: ""
                TokenRefreshScheduler.scheduleDailyRefresh(context)
                FcmTokenSync.sync(context)
                navigateToMainScreen()
            } else if (result.requiresVerification == true) {
                navigateToVerification(state.value.email)
            }
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
        onSignUpPage = onSignUpPage,
        onGoogleSignIn = onGoogleSignIn,
        brandingModifier = brandingModifier
    )
}
