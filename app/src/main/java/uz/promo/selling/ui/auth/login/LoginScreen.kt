package uz.promo.selling.ui.auth.login

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.promo.selling.R
import uz.promo.selling.ui.auth.Email
import uz.promo.selling.ui.auth.EmailState
import uz.promo.selling.ui.auth.EmailStateSaver
import uz.promo.selling.ui.auth.Password
import uz.promo.selling.ui.auth.PasswordState
import uz.promo.selling.ui.auth.google.GoogleSignInButton
import uz.promo.selling.ui.auth.google.OrDivider
import uz.promo.selling.ui.auth.register.Branding
import uz.promo.selling.ui.auth.register.DoYouHaveAccount
import uz.promo.selling.ui.theme.OnlineMarketTheme
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.FreeLoading

private val BrandGreen = Color(0xFF1A6B3C)

@Composable
fun SignInScreen(
    onSignInSignUp: (email: String, password: String) -> Unit,
    state: LoginState,
    loginSuccess: (email: String) -> Unit,
    forgotPassword: () -> Unit,
    onGoogleSignIn: () -> Unit = {},
    brandingModifier: Modifier = Modifier
) {
    var showBranding by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
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
                Branding(modifier = brandingModifier)
            }

            Spacer(
                modifier = Modifier
                    .weight(1f, fill = showBranding)
                    .animateContentSize()
            )

            SignInToLoginAccount(
                onSignInSignUp = onSignInSignUp,
                onFocusChange = { hasFocus -> showBranding = !hasFocus },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                forgotPassword = forgotPassword,
                onGoogleSignIn = onGoogleSignIn
            )
        }

        val context = LocalContext.current
        val rememberedContext = remember { { context } }
        SideEffect {
            if (state.error.isNotBlank()) {
                Toast.makeText(rememberedContext(), state.error, Toast.LENGTH_SHORT).show()
            }
        }
        FreeLoading(state.isLoading, paddingTop = 64.dp)
    }
}

@Composable
fun SignInToLoginAccount(
    onSignInSignUp: (email: String, password: String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    forgotPassword: () -> Unit,
    onGoogleSignIn: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val emailState by rememberSaveable(stateSaver = EmailStateSaver) {
        mutableStateOf(EmailState())
    }
    val passwordState = remember { PasswordState() }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        // Heading
        Text(
            text = stringResource(id = R.string.login_account),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(id = R.string.please_login),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 6.dp, bottom = 28.dp)
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
            onImeAction = { focusRequester.requestFocus() },
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            imeAction = ImeAction.Done,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = { focusManager.clearFocus() }
        )

        // Forgot password link
        DoYouHaveAccount(
            onTextClicked = { forgotPassword() },
            text = R.string.forgot_password,
            modifier = Modifier.padding(top = 12.dp)
        )

        // Sign In button
        val isEnabled = emailState.isValid && passwordState.isValid

        Button(
            onClick = onSubmit,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGreen,
                contentColor = Color.White,
                disabledContainerColor = BrandGreen.copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = stringResource(id = R.string.continuee),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        OrDivider(modifier = Modifier.padding(top = 24.dp))

        GoogleSignInButton(
            onClick = onGoogleSignIn,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Sign Up link
        DoYouHaveAccount(
            onTextClicked = { forgotPassword() },
            text = R.string.do_you_already_have_account,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        )
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    OnlineMarketTheme {
        SignInScreen(
            onSignInSignUp = { _: String, _: String -> },
            state = LoginState(),
            loginSuccess = {},
            forgotPassword = {}
        )
    }
}
