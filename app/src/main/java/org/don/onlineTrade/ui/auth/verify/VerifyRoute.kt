package org.don.onlineTrade.ui.auth.verify

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VerificationRoute(
    emailParam: String,
    navigateToMainScreen: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val verifyViewModel = hiltViewModel<VerifyViewModel>()
    val state = verifyViewModel.state

    VerificationScreen(
        emailParam = emailParam,
        state = state.value,
        onMainScreen = navigateToMainScreen,
        onBackPressed = onBackPressed,
        onSubmit = { email, code ->
            verifyViewModel.verify(code, email)
        }
    )

}