package org.don.onlineTrade.ui.auth.verify

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozcanalasalvar.otp_view.compose.OtpView
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.auth.register.Branding
import org.don.onlineTrade.ui.auth.register.Logo
import org.don.onlineTrade.ui.auth.register.RegistrationState
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.SharedPref


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VerificationScreen(
    emailParam: String,
    state: VerificationState,
    onMainScreen: () -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    onSubmit: (email: String, code: Int) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val otpCodeState = remember {
        mutableStateOf("")
    }
    val enabledState = remember {
        mutableStateOf(false)
    }
    var showBranding by remember {
        mutableStateOf(true)
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
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
                Logo(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 48.dp)
                        .width(100.dp)
                        .height(100.dp)
                )
            }

            Spacer(
                modifier = Modifier
                    .weight(1f, fill = showBranding)
                    .animateContentSize()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen20Dp))

            Text(
                text = String.format(
                    stringResource(id = R.string.enter_code_that_we_send),
                    emailParam
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
                    .wrapContentWidth(),
                textColor = MaterialTheme.colorScheme.onPrimary,
                activeColor = MaterialTheme.colorScheme.primary,
                passiveColor = MaterialTheme.colorScheme.background,
                fontWeight = FontWeight.Bold,
                fontFamily = robotoFontFamily,
                onTextChange = { value, _ ->
                    otpCodeState.value = value
                    if (otpCodeState.value.length == 6) {
                        enabledState.value = true
                        keyboardController?.hide()
                        showBranding = true
                    } else {
                        enabledState.value = false
                    }
                },
                autoFocusEnabled = false,
                onFocusChanged = { hasFocus ->
                    showBranding = !hasFocus
                }
            )

            val onSubmitClick = {
                onSubmit(emailParam, otpCodeState.value.toInt())
            }
            Spacer(
                modifier = Modifier
                    .weight(1f, fill = showBranding)
                    .animateContentSize()
            )
            Button(
                onClick = onSubmitClick,
                enabled = enabledState.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.send),
                    fontSize = 16.sp,
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.Normal
                )
            }
            Spacer(
                modifier = Modifier
                    .weight(1f, fill = showBranding)
                    .animateContentSize()
            )
        }

        FreeLoading(state.isLoading, paddingTop = 64.dp)

    }

    val context = LocalContext.current

    if (state.registerMain != null) {
        if (state.registerMain.status){
            SharedPref.loginTime = System.currentTimeMillis()
            SharedPref.deviceToken = "Bearer ${state.registerMain.token}"
            SharedPref.refreshToken = state.registerMain.refreshToken ?: ""
            org.don.onlineTrade.data.worker.TokenRefreshScheduler.scheduleDailyRefresh(context)
            onMainScreen.invoke()
        }else{
            Toast.makeText(context, stringResource(id = R.string.invalid_password), Toast.LENGTH_SHORT).show()
        }
    }
    val rememberedContext = remember { { context } }
    SideEffect {
        if (state.error.isNotBlank()) {
            Toast.makeText(rememberedContext(), state.error, Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview
@Composable
fun VerificationScreenPreview() {
    VerificationScreen(
        emailParam = "jaloliddinabdullaev7@gmail.com",
        state = VerificationState(),
        onMainScreen = {},
        onBackPressed = {},
        onSubmit = { email: String, code: Int ->

        }
    )
}