package org.don.onlineTrade.ui.auth.register

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.auth.ConfirmPasswordState
import org.don.onlineTrade.ui.auth.Email
import org.don.onlineTrade.ui.auth.EmailState
import org.don.onlineTrade.ui.auth.EmailStateSaver
import org.don.onlineTrade.ui.auth.OrSignInAsGuest
import org.don.onlineTrade.ui.auth.Password
import org.don.onlineTrade.ui.auth.PasswordState


@Composable
fun SignUpScreen(
    onSignInSignUp: (email: String, password: String, phoneNumber: String) -> Unit,
    onSignInAsGuest: () -> Unit,
    state: RegistrationState,
    registrationSuccess: () -> Unit
) {

    var showBranding by remember {
        mutableStateOf(true)
    }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize()){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(
                    modifier = Modifier
                        .weight(1f, fill = showBranding)
                        .animateContentSize()
                )

                AnimatedVisibility(
                    visible = showBranding,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Branding()
                }

                Spacer(
                    modifier = Modifier
                        .weight(1f, fill = showBranding)
                        .animateContentSize()
                )
                SignInCreateAccount(
                    onSignInSignUp = onSignInSignUp,
                    onSignInAsGuest = onSignInAsGuest,
                    onFocusChange = { hasFocus -> showBranding = !hasFocus },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 100.dp)
                )

            }

            if (state.registerMain!= null){
                registrationSuccess.invoke()
            }
            if (state.error.isNotBlank()){
                Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_SHORT).show()
            }

            if (state.isLoading){
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .wrapContentWidth(align = Alignment.CenterHorizontally)
    ) {


        Logo(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 76.dp)
        )

        Text(
            text = stringResource(id = R.string.please_register_or_login),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
        )


    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    val assetId = if (lightTheme) {
        R.drawable.ic_launcher_foreground
    } else {
        R.drawable.ic_launcher_foreground
    }
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier,
        contentDescription = null
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignInCreateAccount(
    onSignInSignUp: (email: String, password: String, phoneNumber: String) -> Unit,
    onSignInAsGuest: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val focusRequester = remember { FocusRequester() }
    val confirmationPasswordFocusRequest = remember { FocusRequester() }

    val emailState by rememberSaveable(stateSaver = EmailStateSaver) {
        mutableStateOf(EmailState())
    }
    val passwordState = remember {
        PasswordState()
    }
    val confirmPasswordState = remember {
        ConfirmPasswordState(passwordState = passwordState)
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.sign_in_or_create_account),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 64.dp, bottom = 12.dp)
        )

        val onSubmit = {
            if (!emailState.isValid) {
                emailState.enableShowErrors()
            }
            if (emailState.isValid && passwordState.isValid) {
                onSignInSignUp(emailState.text, passwordState.text, "998996666666")
            }
        }
        onFocusChange(emailState.isFocused || passwordState.isFocused || confirmPasswordState.isFocused)
        Email(
            emailState = emailState,
            imeAction = ImeAction.Next,
            onImeAction = {
                focusRequester.requestFocus()
            }
        )
        Spacer(modifier = Modifier.height(24.dp))

        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            imeAction = ImeAction.Next,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = {
                confirmationPasswordFocusRequest.requestFocus()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Password(
            label = stringResource(id = R.string.confirm_password),
            passwordState = confirmPasswordState,
            modifier = Modifier.focusRequester(confirmationPasswordFocusRequest),
            onImeAction = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )

        val isEnabled = emailState.isValid &&
                passwordState.isValid &&
                confirmPasswordState.isValid



        Button(
            onClick = onSubmit,
            enabled = isEnabled,

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 3.dp)
        ) {
            Text(
                text = stringResource(id = R.string.continuee),
                style = MaterialTheme.typography.titleSmall
            )
        }
        OrSignInAsGuest(
            onSignInAsGuest = onSignInAsGuest,
            modifier = Modifier.fillMaxWidth()
        )

    }


}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    SignUpScreen(onSignInSignUp = { s: String, s1: String, s2: String -> },
        onSignInAsGuest = {},
        state = RegistrationState(),
        registrationSuccess = {}
    )
}
