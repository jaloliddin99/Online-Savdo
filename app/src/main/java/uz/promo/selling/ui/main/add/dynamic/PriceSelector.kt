package uz.promo.selling.ui.main.add.dynamic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.ai.PriceSuggestionDTO
import uz.promo.selling.data.remote.models.leak.Parameter
import uz.promo.selling.ui.auth.TextFieldError
import uz.promo.selling.ui.auth.TextFieldState
import uz.promo.selling.ui.main.add.UnitDropdownSelector
import uz.promo.selling.ui.theme.spacing

@Composable
fun PriceSelector(
    textState: TextFieldState,
    modifier: Modifier,
    onImeAction: () -> Unit,
    parameter: Parameter,
    unit: uz.promo.selling.data.remote.models.leak.Unit,
    onUnitSelected: (uz.promo.selling.data.remote.models.leak.Unit) -> Unit,
    onSuggestPrice: ((String, (PriceSuggestionDTO?) -> Unit) -> Unit)? = null
) {

    val context = LocalContext.current
    var suggesting by remember { mutableStateOf(false) }

    ContentWrapper(parameter = parameter) {
        if (onSuggestPrice != null) {
            TextButton(
                enabled = !suggesting,
                onClick = {
                    suggesting = true
                    onSuggestPrice(unit.code) { suggestion ->
                        suggesting = false
                        val recommended = suggestion?.recommended
                        if (recommended != null) {
                            textState.text = recommended.toLong().toString()
                            val low = suggestion.low?.toLong()
                            val high = suggestion.high?.toLong()
                            Toast.makeText(
                                context,
                                context.getString(
                                    R.string.ai_price_suggested,
                                    recommended.toLong().toString(),
                                    low.toString(),
                                    high.toString(),
                                    suggestion.sampleSize.toString()
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.ai_price_not_enough_data),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            ) {
                if (suggesting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = "✨ " + stringResource(R.string.ai_suggest_price), style = MaterialTheme.typography.bodyMedium)
            }
        }
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