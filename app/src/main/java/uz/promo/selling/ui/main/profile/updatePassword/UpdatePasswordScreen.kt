package uz.promo.selling.ui.main.profile.updatePassword

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uz.promo.selling.R
import uz.promo.selling.ui.TopAppBar
import uz.promo.selling.data.remote.models.getProfile.UpdatePasswordModel
import uz.promo.selling.ui.auth.ConfirmPasswordState
import uz.promo.selling.ui.auth.Password
import uz.promo.selling.ui.auth.PasswordState
import uz.promo.selling.ui.main.home.UpdateProfileState
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.FreeLoading

@Composable
fun UpdatePasswordRoute(
    modifier: Modifier = Modifier,
    goBackAndRefresh: () -> Unit
) {
    val viewModel = hiltViewModel<UpdatePasswordViewModel>()
    val state = viewModel.state.value
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = stringResource(R.string.password_update),
            navigationIcon = Icons.Filled.ArrowBack,
            onNavigationClick = goBackAndRefresh,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        UpdatePasswordScreen(
            modifier.weight(1f), state, requestToUpdate = {
                viewModel.updatePassword(body = it)
            },
            goBackAndRefresh
        )
    }
}


@Composable
fun UpdatePasswordScreen(
    modifier: Modifier,
    state: UpdateProfileState,
    requestToUpdate: (UpdatePasswordModel) -> Unit,
    goBackAndRefresh: () -> Unit

) {


    val currentPasswordState = remember { PasswordState() }

    val focusRequester = remember { FocusRequester() }
    val confirmationPasswordFocusRequest = remember { FocusRequester() }

    val passwordState = remember { PasswordState() }
    val confirmPasswordState = remember {
        ConfirmPasswordState(passwordState = passwordState)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.dimen16Dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )  {

        val onSubmit = {
            requestToUpdate.invoke(
                UpdatePasswordModel(
                    currentPasswordState.text,
                    passwordState.text
                )
            )
        }


        Password(
            label = stringResource(id = R.string.current_password),
            passwordState = currentPasswordState,
            imeAction = ImeAction.Next,
            modifier = Modifier.focusRequester(focusRequester),
            onImeAction = {
                focusRequester.requestFocus()
            }
        )


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
            onImeAction = {}
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))


        val isEnabled =
           currentPasswordState.isValid && passwordState.isValid &&
                    confirmPasswordState.isValid

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

    }


    val context = LocalContext.current
    val rememberedContext = remember { { context } }
    if (state.getProfile != null) {
        goBackAndRefresh.invoke()
    }
    if (state.error.isNotEmpty()) {
        Toast.makeText(rememberedContext(), state.error, Toast.LENGTH_SHORT).show()
    }
    FreeLoading(state.isLoading, paddingTop = 64.dp)
}