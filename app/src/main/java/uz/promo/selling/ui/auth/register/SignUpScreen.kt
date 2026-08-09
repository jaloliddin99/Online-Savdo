package uz.promo.selling.ui.auth.register

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.promo.selling.R
import uz.promo.selling.ui.auth.google.GoogleSignInButton
import uz.promo.selling.ui.auth.google.OrDivider
import uz.promo.selling.ui.auth.ConfirmPasswordState
import uz.promo.selling.ui.auth.Email
import uz.promo.selling.ui.auth.EmailState
import uz.promo.selling.ui.auth.EmailStateSaver
import uz.promo.selling.ui.auth.NameField
import uz.promo.selling.ui.auth.Password
import uz.promo.selling.ui.auth.PasswordState
import uz.promo.selling.ui.auth.PhoneNumber
import uz.promo.selling.ui.auth.PhoneNumberState
import uz.promo.selling.ui.auth.TextFieldState
import uz.promo.selling.ui.auth.removeFirstCharacterAndAllSpaces
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.FreeLoading
import uz.promo.selling.utils.localizedError

private val BrandGreen = Color(0xFF1A6B3C)

@Composable
fun SignUpScreen(
    onSignInSignUp: (firstName: String, email: String, password: String, phoneNumber: String) -> Unit,
    state: RegistrationState,
    registrationSuccess: (email: String) -> Unit,
    onLoginPage: () -> Unit,
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

            SignUpCreateAccount(
                onSignInSignUp = onSignInSignUp,
                onFocusChange = { hasFocus -> showBranding = !hasFocus },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                onLoginPage = onLoginPage,
                onGoogleSignIn = onGoogleSignIn
            )
        }

        if (state.error.isNotBlank()) {
            Toast.makeText(LocalContext.current, localizedError(LocalContext.current, state.error), Toast.LENGTH_SHORT).show()
        }

        FreeLoading(isFeedLoading = state.isLoading, paddingTop = 64.dp)
    }
}

@Composable
fun Branding(
    modifier: Modifier = Modifier,
    @StringRes text: Int? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo(
            modifier = Modifier
                .padding(top = 32.dp)
                .width(110.dp)
                .height(110.dp)
        )
        if (text != null) {
            Text(
                text = stringResource(id = text),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    Image(
        painter = painterResource(id = R.drawable.sotiq_icon),
        modifier = modifier,
        contentDescription = null,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpCreateAccount(
    onSignInSignUp: (name: String, email: String, password: String, phoneNumber: String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onLoginPage: () -> Unit,
    onGoogleSignIn: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val firstNameFocus = remember { FocusRequester() }
    val focusRequester = remember { FocusRequester() }
    val confirmationPasswordFocusRequest = remember { FocusRequester() }
    val phoneNumberRequester = remember { FocusRequester() }

    val nameState = remember { TextFieldState() }
    val emailState by rememberSaveable(stateSaver = EmailStateSaver) {
        mutableStateOf(EmailState())
    }
    val passwordState = remember { PasswordState() }
    val confirmPasswordState = remember {
        ConfirmPasswordState(passwordState = passwordState)
    }
    val phoneState = remember { PhoneNumberState() }

    var showEmailForm by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        // Heading
        Text(
            text = stringResource(id = R.string.sign_in_or_create_account),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        val onSubmit = {
            if (!emailState.isValid) {
                emailState.enableShowErrors()
            }
            if (emailState.isValid && passwordState.isValid) {
                onSignInSignUp(
                    nameState.text,
                    emailState.text,
                    passwordState.text,
                    removeFirstCharacterAndAllSpaces(phoneState.text)
                )
            }
        }
        onFocusChange(
            emailState.isFocused
                    || passwordState.isFocused
                    || confirmPasswordState.isFocused
                    || nameState.isFocused
                    || phoneState.isFocused
        )

        // Collapsed state: single "Sign Up with email" button that expands the form
        AnimatedVisibility(
            visible = !showEmailForm,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Button(
                onClick = { showEmailForm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.sign_up_with_email),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }

        AnimatedVisibility(
            visible = showEmailForm,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                NameField(
                    modifier = Modifier,
                    nameState = nameState,
                    imeAction = ImeAction.Next,
                    onImeAction = { firstNameFocus.requestFocus() },
                )
                Spacer(modifier = Modifier.height(12.dp))

                Email(
                    emailState = emailState,
                    imeAction = ImeAction.Next,
                    onImeAction = { focusRequester.requestFocus() },
                    modifier = Modifier.focusRequester(firstNameFocus)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Password(
                    label = stringResource(id = R.string.password),
                    passwordState = passwordState,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.focusRequester(focusRequester),
                    onImeAction = { confirmationPasswordFocusRequest.requestFocus() }
                )
                Spacer(modifier = Modifier.height(12.dp))

                Password(
                    label = stringResource(id = R.string.confirm_password),
                    passwordState = confirmPasswordState,
                    modifier = Modifier.focusRequester(confirmationPasswordFocusRequest),
                    onImeAction = { phoneNumberRequester.requestFocus() }
                )
                Spacer(modifier = Modifier.height(12.dp))

                PhoneNumber(
                    phoneState = phoneState,
                    modifier = Modifier.focusRequester(phoneNumberRequester),
                    onImeAction = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )

                // Sign Up button
                val isEnabled = emailState.isValid &&
                        passwordState.isValid &&
                        confirmPasswordState.isValid &&
                        nameState.isValid &&
                        phoneState.isValid

                Button(
                    onClick = onSubmit,
                    enabled = isEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp)
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
            }
        }

        OrDivider(modifier = Modifier.padding(top = 24.dp))

        GoogleSignInButton(
            onClick = onGoogleSignIn,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Login link
        DoYouHaveAccount(
            onTextClicked = { onLoginPage() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)
        )
    }
}

@Composable
fun DoYouHaveAccount(
    onTextClicked: () -> Unit,
    @StringRes text: Int = R.string.do_you_already_have_account,
    modifier: Modifier = Modifier
) {
    val label = stringResource(id = text)
    Text(
        text = label,
        color = BrandGreen,
        fontFamily = robotoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        modifier = modifier.clickable { onTextClicked() }
    )
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    SignUpScreen(
        onSignInSignUp = { _: String, _: String, _: String, _: String -> },
        state = RegistrationState(),
        registrationSuccess = {},
        onLoginPage = {}
    )
}
