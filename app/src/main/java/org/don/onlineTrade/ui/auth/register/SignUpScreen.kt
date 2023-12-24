package org.don.onlineTrade.ui.auth.register

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.auth.ConfirmPasswordState
import org.don.onlineTrade.ui.auth.Email
import org.don.onlineTrade.ui.auth.EmailState
import org.don.onlineTrade.ui.auth.EmailStateSaver
import org.don.onlineTrade.ui.auth.NameField
import org.don.onlineTrade.ui.auth.OrSignInAsGuest
import org.don.onlineTrade.ui.auth.Password
import org.don.onlineTrade.ui.auth.PasswordState
import org.don.onlineTrade.ui.auth.PhoneNumber
import org.don.onlineTrade.ui.auth.PhoneNumberState
import org.don.onlineTrade.ui.auth.TextFieldState
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing


@Composable
fun SignUpScreen(
    onSignInSignUp: (firstName: String, lastName: String, email: String, password: String, phoneNumber: String) -> Unit,
    state: RegistrationState,
    registrationSuccess: (email: String) -> Unit,
    onLoginPage: () -> Unit
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
        Box(modifier = Modifier.fillMaxSize()) {
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
                SignUpCreateAccount(
                    onSignInSignUp = onSignInSignUp,
                    onSignInAsGuest = {},
                    onFocusChange = { hasFocus -> showBranding = !hasFocus },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 100.dp),
                    onLoginPage = onLoginPage
                )

            }

            if (state.registerMain != null) {
                registrationSuccess.invoke(state.email)
            }
            if (state.error.isNotBlank()) {
                Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_SHORT).show()
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun Branding(
    modifier: Modifier = Modifier,
    @StringRes text: Int = R.string.please_login
) {
    Column(
        modifier = modifier
            .wrapContentWidth(align = Alignment.CenterHorizontally)
    ) {


        Logo(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 48.dp)
                .width(100.dp)
                .height(100.dp)
        )

        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
        )


    }
}

@Composable
fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    val assetId = if (lightTheme) {
        R.drawable.logo
    } else {
        R.drawable.logo
    }
    Image(
        painter = painterResource(id = assetId),
        modifier = modifier
            .padding(top = MaterialTheme.spacing.dimen12Dp),
        contentDescription = null,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpCreateAccount(
    onSignInSignUp: (firstName: String, lastName: String, email: String, password: String, phoneNumber: String) -> Unit,
    onSignInAsGuest: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onLoginPage: () -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val firstNameFocus = remember { FocusRequester() }
    val lastNameFocus = remember { FocusRequester() }
    val focusRequester = remember { FocusRequester() }
    val confirmationPasswordFocusRequest = remember { FocusRequester() }
    val phoneNumberRequester = remember { FocusRequester() }

    val nameState = remember { TextFieldState() }
    val lastNameState = remember { TextFieldState() }
    val emailState by rememberSaveable(stateSaver = EmailStateSaver) {
        mutableStateOf(EmailState())
    }
    val passwordState = remember { PasswordState() }
    val confirmPasswordState = remember {
        ConfirmPasswordState(passwordState = passwordState)
    }
    val phoneState = remember { PhoneNumberState() }

    Column(
        modifier = modifier
            .fillMaxWidth(),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.sign_in_or_create_account),
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
                onSignInSignUp(
                    nameState.text,
                    lastNameState.text,
                    emailState.text,
                    passwordState.text,
                    phoneState.text
                )
            }
        }
        onFocusChange(
            emailState.isFocused
                    || passwordState.isFocused
                    || confirmPasswordState.isFocused
                    || nameState.isFocused
                    || lastNameState.isFocused
                    || phoneState.isFocused
        )
        NameField(
            modifier = Modifier,
            nameState = nameState,
            imeAction = ImeAction.Next,
            onImeAction = {
                firstNameFocus.requestFocus()
            },
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        NameField(
            modifier = Modifier.focusRequester(firstNameFocus),
            nameState = lastNameState,
            imeAction = ImeAction.Next,
            onImeAction = {
                lastNameFocus.requestFocus()
            },
            resId = R.string.lastName
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        Email(
            emailState = emailState,
            imeAction = ImeAction.Next,
            onImeAction = {
                focusRequester.requestFocus()
            },
            modifier = Modifier.focusRequester(lastNameFocus)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            imeAction = ImeAction.Next,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = {
                confirmationPasswordFocusRequest.requestFocus()
            }
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        Password(
            label = stringResource(id = R.string.confirm_password),
            passwordState = confirmPasswordState,
            modifier = Modifier.focusRequester(confirmationPasswordFocusRequest),
            onImeAction = {
                phoneNumberRequester.requestFocus()
            }
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        PhoneNumber(
            phoneState = phoneState,
            modifier = Modifier.focusRequester(phoneNumberRequester),
            onImeAction = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        DoYouHaveAccount(onTextClicked = {
            onLoginPage()
        })

        val isEnabled = emailState.isValid &&
                passwordState.isValid &&
                confirmPasswordState.isValid &&
                nameState.isValid &&
                lastNameState.isValid &&
                phoneState.isValid

        Button(
            onClick = onSubmit,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 3.dp)
        ) {
            Text(
                text = stringResource(id = R.string.continuee),
                style = MaterialTheme.typography.titleSmall
            )
        }
//        OrSignInAsGuest(
//            onSignInAsGuest = onSignInAsGuest,
//            modifier = Modifier.fillMaxWidth()
//        )

    }
}

@Composable
fun DoYouHaveAccount(
    onTextClicked: () -> Unit,
    @StringRes text: Int = R.string.do_you_already_have_account
) {
    val getString = stringResource(id = text)
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Normal,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(getString)
        }
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = annotatedString,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            modifier = Modifier.clickable {
                onTextClicked()
            }
        )
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    SignUpScreen(onSignInSignUp = { firstName: String, lastName: String, s: String, s1: String, s2: String -> },
        state = RegistrationState(),
        registrationSuccess = {},
        onLoginPage = {}
    )
}
