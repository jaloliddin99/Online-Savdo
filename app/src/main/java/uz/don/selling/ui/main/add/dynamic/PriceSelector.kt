package uz.don.selling.ui.main.add.dynamic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import uz.don.selling.data.remote.models.leak.Parameter
import uz.don.selling.ui.auth.TextFieldError
import uz.don.selling.ui.auth.TextFieldState
import uz.don.selling.ui.main.add.UnitDropdownSelector
import uz.don.selling.ui.theme.spacing

@Composable
fun PriceSelector(
    textState: TextFieldState,
    modifier: Modifier,
    onImeAction: () -> Unit,
    parameter: Parameter,
    unit: uz.don.selling.data.remote.models.leak.Unit,
    onUnitSelected: (uz.don.selling.data.remote.models.leak.Unit) -> Unit
) {


    ContentWrapper(parameter = parameter) {
        Row(
            modifier = Modifier.wrapContentHeight(), verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                UnitDropdownSelector(
                    list = parameter.units,
                    preselected = parameter.units[0],
                    onSelectionChanged = onUnitSelected,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))
            OutlinedTextField(
                value = textState.text,
                onValueChange = {
                    textState.text = it
                },
                label = {
                    Text(
                        text = unit.label,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                isError = textState.showErrors(),
                modifier = modifier
                    .fillMaxWidth()
                    .weight(2f)
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

}