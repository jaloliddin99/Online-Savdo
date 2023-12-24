package org.don.onlineTrade.ui.auth

import android.util.Log
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.phone.PhoneNumberVisualTransformation


@Composable
fun PhoneNumber(
    phoneState: TextFieldState = remember { PhoneNumberState() },
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
    modifier: Modifier,
    @StringRes resId: Int = R.string.phone_number
){
    val cornerSize = with(LocalDensity.current) { MaterialTheme.spacing.dimen8Dp }

    OutlinedTextField(
        value = phoneState.text,
        onValueChange = {
            Log.d("TAG", "PhoneNumberdwadawdawdawdawdawd $it , ${it.length}")
            if (it.length <= 13){
                phoneState.text = it
            }
        },
        singleLine = true,
        label = {
            Text(
                text = stringResource(id = resId),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                phoneState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    phoneState.enableShowErrors()
                }
            },
        shape = MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(cornerSize),
            topEnd = CornerSize(cornerSize),
            bottomStart = CornerSize(cornerSize),
            bottomEnd = CornerSize(cornerSize)
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        isError = phoneState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Phone
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        visualTransformation = PhoneNumberVisualTransformation(),
        )

    phoneState.getError()?.let { error -> TextFieldError(textError = error) }
}
@Composable
fun NameField(
    nameState: TextFieldState = remember { TextFieldState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    modifier: Modifier,
    @StringRes resId: Int = R.string.firstName
){
    val cornerSize = with(LocalDensity.current) { MaterialTheme.spacing.dimen8Dp }

    OutlinedTextField(
        value = nameState.text,
        onValueChange = {
            nameState.text = it
        },
        label = {
            Text(
                text = stringResource(id = resId),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                nameState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    nameState.enableShowErrors()
                }
            },
        shape = MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(cornerSize),
            topEnd = CornerSize(cornerSize),
            bottomStart = CornerSize(cornerSize),
            bottomEnd = CornerSize(cornerSize)
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        isError = nameState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
    )

    nameState.getError()?.let { error -> TextFieldError(textError = error) }
}
@Composable
fun Email(
    emailState: TextFieldState = remember { EmailState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    modifier: Modifier
) {
    val cornerSize = with(LocalDensity.current) { MaterialTheme.spacing.dimen8Dp }

    OutlinedTextField(
        value = emailState.text,
        onValueChange = {
            emailState.text = it
        },
        label = {
            Text(
                text = stringResource(id = R.string.email),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                emailState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    emailState.enableShowErrors()
                }
            },
        shape = MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(cornerSize),
            topEnd = CornerSize(cornerSize),
            bottomStart = CornerSize(cornerSize),
            bottomEnd = CornerSize(cornerSize)
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        isError = emailState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
    )

    emailState.getError()?.let { error -> TextFieldError(textError = error) }
}

/**
 * To be removed when [TextField]s support error
 */
@Composable
fun TextFieldError(textError: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = textError,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.error
        )
    }
}


@Composable
fun OrSignInAsGuest(
    onSignInAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.or),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.paddingFromBaseline(top = 16.dp)
        )
        OutlinedButton(
            onClick = onSignInAsGuest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp),
        ) {
            Text(text = stringResource(id = R.string.sign_in_guest))
        }
    }
}

@Composable
fun Password(
    label: String,
    passwordState: TextFieldState,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    val showPassword = rememberSaveable { mutableStateOf(false) }
    val cornerSize = with(LocalDensity.current) { MaterialTheme.spacing.dimen8Dp }

    OutlinedTextField(
        value = passwordState.text,
        onValueChange = {
            passwordState.text = it
            passwordState.enableShowErrors()
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                passwordState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    passwordState.enableShowErrors()
                }
            },
        shape = MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(cornerSize),
            topEnd = CornerSize(cornerSize),
            bottomStart = CornerSize(cornerSize),
            bottomEnd = CornerSize(cornerSize)
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        trailingIcon = {
            if (showPassword.value) {
                IconButton(onClick = { showPassword.value = false }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = stringResource(id = R.string.hide_password)
                    )
                }
            } else {
                IconButton(onClick = { showPassword.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = stringResource(id = R.string.show_password)
                    )
                }
            }
        },
        visualTransformation = if (showPassword.value) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        isError = passwordState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
    )
    passwordState.getError()?.let { error -> TextFieldError(textError = error) }

}