package org.don.onlineTrade.ui.auth.login

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.auth.Email
import org.don.onlineTrade.ui.auth.EmailState
import org.don.onlineTrade.ui.auth.EmailStateSaver
import org.don.onlineTrade.ui.auth.OrSignInAsGuest
import org.don.onlineTrade.ui.auth.Password
import org.don.onlineTrade.ui.auth.PasswordState
import org.don.onlineTrade.ui.auth.register.Branding
import org.don.onlineTrade.ui.auth.register.RegistrationState
import org.don.onlineTrade.ui.auth.register.SignUpScreen
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.SharedPref
import java.util.Date

@Composable
fun SignInScreen(
    onSignInSignUp: (email: String, password: String) -> Unit,
    state: LoginState,
    loginSuccess: (email: String) -> Unit
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
                SignInToLoginAccount(
                    onSignInSignUp = onSignInSignUp,
                    onSignInAsGuest = {},
                    onFocusChange = { hasFocus -> showBranding = !hasFocus },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 100.dp)
                )
            }
            if (state.registerMain!= null){
                loginSuccess.invoke(state.email)
            }

            val context = LocalContext.current
            val rememberedContext = remember { { context } }
            SideEffect {
                if (state.error.isNotBlank()){
                    Toast.makeText(rememberedContext(), state.error, Toast.LENGTH_SHORT).show()
                }
            }
            FreeLoading(state.isLoading, paddingTop = 64.dp)
        }
    }

}


@Composable
fun SignInToLoginAccount(
    onSignInSignUp: (email: String, password: String) -> Unit,
    onSignInAsGuest: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    val focusRequester = remember { FocusRequester() }

    val emailState by rememberSaveable(stateSaver = EmailStateSaver) {
        mutableStateOf(EmailState())
    }
    val passwordState = remember {
        PasswordState()
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.login_account),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = MaterialTheme.spacing.dimen16Sp
        )

        val onSubmit = {
            if (!emailState.isValid) {
                emailState.enableShowErrors()
            }
            if (emailState.isValid && passwordState.isValid) {
                onSignInSignUp(emailState.text, passwordState.text)
            }
        }
        onFocusChange(emailState.isFocused || passwordState.isFocused)
        Email(
            emailState = emailState,
            imeAction = ImeAction.Next,
            onImeAction = {
                focusRequester.requestFocus()
            },
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(24.dp))

        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            imeAction = ImeAction.Done,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = {
                focusManager.clearFocus()
            }
        )

        val isEnabled = emailState.isValid &&
                passwordState.isValid


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
    SignInScreen(onSignInSignUp = { s: String, s1: String -> },
        state = LoginState(),
        loginSuccess = {}
    )
}
