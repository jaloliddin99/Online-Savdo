package org.don.onlineTrade.ui.add

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.auth.TextFieldState
import org.don.onlineTrade.ui.theme.spacing

@Composable
fun TextFieldForProduct(
    modifier: Modifier = Modifier,
    onImeAction: () -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    productState: TextFieldState = remember { ProductTitleState() },
    @StringRes title: Int = R.string.for_instance_redme,
) {

    OutlinedTextField(
        value = productState.text,
        onValueChange = {
            productState.text = it
        },
        label = {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = modifier,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Text,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp)
    )
}


@Composable
fun TextFieldUnEditable(
    modifier: Modifier = Modifier,
    onImeAction: () -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    productState: TextFieldState = remember { ProductTitleState() },
    @StringRes title: Int,
    isFocusedOrClicked: () -> Unit = {}
) {
    OutlinedTextField(
        value = productState.text,
        onValueChange = {
            productState.text = it
        },
        label = {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = modifier
            .onFocusChanged {
                if (it.isFocused){
                    isFocusedOrClicked()
                }
            },
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Text,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
        trailingIcon = {
            Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
        },
        readOnly = true,
    )
}
