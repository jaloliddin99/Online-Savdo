package uz.promo.selling.ui.main.add.dynamic

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import uz.promo.selling.data.remote.models.leak.Parameter
import uz.promo.selling.ui.auth.TextFieldError
import uz.promo.selling.ui.auth.TextFieldState
import uz.promo.selling.ui.theme.spacing


@Composable
fun DynamicTextView(
    textState: TextFieldState,
    modifier: Modifier,
    onImeAction: () -> Unit,
    parameter: Parameter
) {
    val hintText = if (parameter.units.isNotEmpty()) parameter.units[0].label else ""
    ContentWrapper(parameter = parameter) {
        OutlinedTextField(
            value = textState.text,
            onValueChange = {
                textState.text = it
            },
            label = {
                Text(
                    text = hintText,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            isError = textState.showErrors(),
            modifier = modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    textState.onFocusChange(focusState.isFocused)
                    if (!focusState.isFocused) {
                        textState.enableShowErrors()
                    }
                },
            textStyle = MaterialTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onImeAction()
                }
            ),
            shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp),
            supportingText = {
                textState.getError()?.let { error -> TextFieldError(textError = error) }
            },
        )
    }
}