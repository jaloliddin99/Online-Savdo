package org.don.onlineTrade.ui.profile.update

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.getProfile.UpdateProfileModel
import org.don.onlineTrade.ui.auth.NameField
import org.don.onlineTrade.ui.auth.PhoneNumber
import org.don.onlineTrade.ui.auth.PhoneNumberState
import org.don.onlineTrade.ui.auth.TextFieldState
import org.don.onlineTrade.ui.home.UpdateProfileState
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading

@Composable
fun UpdateProfileRoute(
    modifier: Modifier = Modifier,
    goBackAndRefresh: () -> Unit
) {
    val viewModel = hiltViewModel<UpdateProfileViewModel>()
    val state = viewModel.state.value
    UpdateProfileScreen(modifier, state, requestToUpdate = {
        viewModel.updateProfile(body = it)
    },
        goBackAndRefresh)
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UpdateProfileScreen(
    modifier: Modifier,
    state: UpdateProfileState,
    requestToUpdate: (UpdateProfileModel) -> Unit,
    goBackAndRefresh: () -> Unit,

) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val firstNameFocus = remember { FocusRequester() }
    val lastNameFocus = remember { FocusRequester() }
    val phoneNumberRequester = remember { FocusRequester() }

    val nameState = remember { TextFieldState() }
    val lastNameState = remember { TextFieldState() }
    val phoneState = remember { PhoneNumberState() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.dimen16Dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val onSubmit = {
            requestToUpdate(
                UpdateProfileModel(
                    firstName = nameState.text,
                    lastName = lastNameState.text,
                    phoneNumber = phoneState.text
                )
            )
        }
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
        PhoneNumber(
            phoneState = phoneState,
            modifier = Modifier.focusRequester(phoneNumberRequester),
            onImeAction = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))

        val isEnabled = nameState.isValid && lastNameState.isValid && phoneState.isValid

        Button(
            onClick = onSubmit,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 3.dp)
        ) {
            Text(
                text = stringResource(id = R.string.update_profile),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }


    val context = LocalContext.current
    val rememberedContext = remember { { context } }
    if (state.getProfile != null){
        goBackAndRefresh.invoke()
    }
    if (state.error.isNotEmpty()){
        Toast.makeText(rememberedContext(), state.error, Toast.LENGTH_SHORT).show()
    }
    FreeLoading(state.isLoading, paddingTop = 64.dp)
}