package uz.don.onlineTrade.ui.auth.forgotPassword

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uz.don.onlineTrade.R
import uz.don.onlineTrade.ui.auth.Email
import uz.don.onlineTrade.ui.auth.EmailState
import uz.don.onlineTrade.ui.auth.EmailStateSaver
import uz.don.onlineTrade.ui.auth.register.Branding
import uz.don.onlineTrade.ui.main.home.ForgotPasswordState
import uz.don.onlineTrade.ui.theme.robotoFontFamily
import uz.don.onlineTrade.ui.theme.spacing
import uz.don.onlineTrade.utils.FreeLoading

@Composable
fun ForgotPasswordRoute(
    modifier: Modifier = Modifier,
    goToResetPage: (email: String) -> Unit,
) {
    val viewModel = hiltViewModel<FPasswordViewModel>()
    val state = viewModel.state.value
    ForgotPasswordScreen(
        modifier, state, requestToUpdate = {
            viewModel.forgotPassword(it)
        },
        goToResetPage,
    )
}


@Composable
fun ForgotPasswordScreen(
    modifier: Modifier,
    state: ForgotPasswordState,
    requestToUpdate: (email: String) -> Unit,
    goToResetPage: (email: String) -> Unit,
) {
    val emailState by rememberSaveable(stateSaver = EmailStateSaver) {
        mutableStateOf(EmailState())
    }
    val focusManager = LocalFocusManager.current


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
                emailState.text
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
            Branding(text = R.string.forgot_password)
        }

        Spacer(
            modifier = Modifier
                .weight(1f, fill = showBranding)
                .animateContentSize()
        )

        Text(
            text = stringResource(id = R.string.forgot_password),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = MaterialTheme.spacing.dimen16Sp
        )

        Email(
            emailState = emailState,
            imeAction = ImeAction.Done,
            onImeAction = {
                focusManager.clearFocus()
            },
            modifier = Modifier
        )

        showBranding = !emailState.isFocused
        val isEnabled = emailState.isValid

        Spacer(
            modifier = Modifier
                .weight(9f, fill = showBranding)
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
                text = stringResource(id = R.string.send),
                style = MaterialTheme.typography.titleSmall
            )
        }

    }


    val context = LocalContext.current
    val rememberedContext = remember { { context } }
    if (state.main != null) {
        goToResetPage.invoke(emailState.text)
    }
    if (state.error.isNotEmpty()) {
        Toast.makeText(rememberedContext(), state.error, Toast.LENGTH_SHORT).show()
    }
    FreeLoading(state.isLoading, paddingTop = 64.dp)
}