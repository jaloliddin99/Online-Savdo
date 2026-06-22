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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import uz.promo.selling.utils.formatNumberWithSpaces

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
    // Holds the fetched suggestion so the explanation sheet can show it; the
    // price is only written to the field when the user taps "use this price".
    var suggestion by remember { mutableStateOf<PriceSuggestionDTO?>(null) }
    var showSheet by remember { mutableStateOf(false) }

    ContentWrapper(parameter = parameter) {
        if (onSuggestPrice != null) {
            // AI-styled pill (gradient + sparkle) so it clearly reads as AI-powered.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF7340D9), MaterialTheme.colorScheme.primary)
                        )
                    )
                    .clickable(enabled = !suggesting) {
                        suggesting = true
                        onSuggestPrice(unit.code) { result ->
                            suggesting = false
                            if (result?.recommended != null) {
                                suggestion = result
                                showSheet = true
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.ai_price_not_enough_data),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                if (suggesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.ai_suggest_price),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
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

        if (showSheet && suggestion != null) {
            PriceSuggestionBottomSheet(
                suggestion = suggestion!!,
                currencyLabel = unit.label,
                onUse = {
                    suggestion?.recommended?.let { textState.text = it.toLong().toString() }
                    showSheet = false
                },
                onDismiss = { showSheet = false }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriceSuggestionBottomSheet(
    suggestion: PriceSuggestionDTO,
    currencyLabel: String,
    onUse: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val recommended = suggestion.recommended ?: 0.0
    val low = suggestion.low ?: recommended
    val high = suggestion.high ?: recommended

    fun price(value: Double): String {
        val number = formatNumberWithSpaces(value.toLong().toString())
        return if (currencyLabel.isBlank()) number else "$number $currencyLabel"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF7340D9)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.ai_price_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.ai_price_recommended_label),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = price(recommended),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(
                    R.string.ai_price_explanation,
                    suggestion.sampleSize.toString(),
                    price(low),
                    price(high)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onUse,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.ai_price_use),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}