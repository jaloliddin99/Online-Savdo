package org.don.onlineTrade.ui.add

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import org.don.onlineTrade.R
import org.don.onlineTrade.ui.auth.EmailState
import org.don.onlineTrade.ui.auth.TextFieldState
import org.don.onlineTrade.ui.theme.spacing

@Composable
fun TextFieldForProduct(
    modifier: Modifier = Modifier,
    onImeAction: () -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    productState: TextFieldState = remember { ProductTitleState() },
) {

    OutlinedTextField(
        value = productState.text,
        onValueChange = {
            productState.text = it
        },
        label = {
            Text(
                text = stringResource(id = R.string.for_instance_redme),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = modifier,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp)
    )
}
