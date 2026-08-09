package uz.promo.selling.ui.auth.forgotPassword

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ozcanalasalvar.otp_view.compose.OtpView
import uz.promo.selling.R
import uz.promo.selling.ui.auth.ConfirmPasswordState
import uz.promo.selling.ui.auth.Password
import uz.promo.selling.ui.auth.PasswordState
import uz.promo.selling.ui.auth.register.Branding
import uz.promo.selling.ui.main.home.ResetNewPasswordState
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.FreeLoading
import uz.promo.selling.utils.localizedError

@Composable
fun ResetPasswordRoute(
    modifier: Modifier = Modifier,
    goToLoginPage: () -> Unit,
    mEmail: String,
    fromLoginPage: Boolean,
    onBackPressed: () -> Unit
) {
    val viewModel = hiltViewModel<FPasswordViewModel>()
    val state = viewModel.stateR.value
    ResetPasswordScreen(
        modifier,
        state,
        mEmail,
        requestToUpdate = { email: String, code: Int, password: String ->
            viewModel.resetNewPassword(email, code, password)
        },
        goToLoginPage = goToLoginPage,
        fromLoginPage,
        onBackPressed
    )
}


@Composable
fun ResetPasswordScreen(
    modifier: Modifier,
    state: ResetNewPasswordState,
    email: String,
    requestToUpdate: (email: String, code: Int, password: String) -> Unit,
    goToLoginPage: () -> Unit,
    fromLoginPage: Boolean,
    onBackPressed: () -> Unit
) {

    val otpCodeState = remember {
        mutableStateOf("")
    }
    val enabledState = remember {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current

    val focusRequester = remember { FocusRequester() }
    val confirmationPasswordFocusRequest = remember { FocusRequester() }

    val passwordState = remember { PasswordState() }
    val confirmPasswordState = remember {
        ConfirmPasswordState(passwordState = passwordState)
    }


    val codeFieldFocusState = remember {
        mutableStateOf(false)
    }


    var showBranding by remember {
        mutableStateOf(true)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = MaterialTheme.spacing.dimen16Dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val onSubmit = {
            requestToUpdate(
                email, otpCodeState.value.toInt(), passwordState.text
            )
        }

        Spacer(
            modifier = Modifier
                .weight(1f, fill = showBranding)
                .animateContentSize()
        )

        AnimatedVisibility(
            visible = showBranding,
            modifier = Modifier.fillMaxWidth()
        ) {
            Branding(text = R.string.reset_password)
        }

        Spacer(
            modifier = Modifier
                .weight(1f, fill = showBranding)
                .animateContentSize()
        )
        Text(
            text = stringResource(id = R.string.reset_password),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = MaterialTheme.spacing.dimen16Sp
        )

        Text(
            text = String.format(
                stringResource(id = R.string.enter_code_that_we_send),
                email
            ),
            fontWeight = FontWeight.Normal,
            fontFamily = robotoFontFamily,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.dimen16Dp),
            fontSize = MaterialTheme.spacing.dimen16Sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen20Dp))
        OtpView(
            value = otpCodeState.value,
            digits = 6,
            modifier = Modifier
                .wrapContentWidth()
                .onFocusChanged { focusState ->
                    codeFieldFocusState.value = focusState.isFocused
                },
            textColor = MaterialTheme.colorScheme.onPrimary,
            activeColor = MaterialTheme.colorScheme.primary,
            passiveColor = MaterialTheme.colorScheme.background,
            fontWeight = FontWeight.Bold,
            fontFamily = robotoFontFamily,
            onTextChange = { value, _ ->
                otpCodeState.value = value
                if (otpCodeState.value.length == 6) {
                    enabledState.value = true
                    focusRequester.requestFocus()
                } else {
                    enabledState.value = false
                }
            },
            autoFocusEnabled = false,
            onFocusChanged = { }
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
                focusManager.clearFocus()
            }
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))


        val isEnabled =
            passwordState.isValid &&
                    confirmPasswordState.isValid && enabledState.value


        showBranding = !(passwordState.isFocused || confirmPasswordState.isFocused || codeFieldFocusState.value)

        Spacer(
            modifier = Modifier
                .weight(7f, fill = showBranding)
                .animateContentSize()
        )

        Button(
            onClick = onSubmit,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 70.dp)
        ) {
            Text(
                text = stringResource(id = R.string.reset_password),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }


    val context = LocalContext.current
    val rememberedContext = remember { { context } }
    if (state.main != null) {
        if (fromLoginPage)
            goToLoginPage.invoke()
        else
            onBackPressed.invoke()
    }
    if (state.error.isNotEmpty()) {
        Toast.makeText(rememberedContext(), localizedError(rememberedContext(), state.error), Toast.LENGTH_SHORT).show()
    }
    FreeLoading(state.isLoading, paddingTop = 64.dp)
}