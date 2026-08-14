package uz.promo.selling.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.promo.selling.R
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.formatNumberWithSpaces
import kotlin.math.roundToLong

/** Server-identical discount math: whole so'm, half-up rounding. */
fun promoDiscountedPrice(price: Long, percent: Int): Long =
    (price * (100 - percent) / 100.0).roundToLong()

/**
 * Promo-code input shared by the boost and premium paywalls: text field +
 * apply button; once a code is validated it collapses into a success line
 * ("PROMO10 — -10%") with an X to remove it.
 */
@Composable
fun PromoCodeRow(
    appliedCode: String?,
    appliedPercent: Int,
    checking: Boolean,
    accent: Color,
    onApply: (String) -> Unit,
    onClear: () -> Unit,
) {
    var input by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = if (appliedCode != null) appliedCode else input,
                onValueChange = { input = it.uppercase() },
                modifier = Modifier.weight(1f),
                enabled = appliedCode == null,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = robotoFontFamily,
                    fontSize = 14.sp
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.promo_code_hint),
                        fontFamily = robotoFontFamily,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            val canApply = appliedCode == null && !checking && input.isNotBlank()
            Box(
                modifier = Modifier
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (canApply) accent else accent.copy(alpha = 0.4f))
                    .clickable(enabled = canApply) { onApply(input.trim()) }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                if (checking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = stringResource(R.string.promo_apply),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }

        if (appliedCode != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$appliedCode — -$appliedPercent% " +
                            stringResource(R.string.promo_discount),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = accent,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { input = ""; onClear() }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/** Original price struck through next to the discounted one. */
@Composable
fun PromoPriceText(
    original: Long,
    percent: Int,
    accent: Color,
    fontSize: androidx.compose.ui.unit.TextUnit = 13.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
) {
    val discounted = promoDiscountedPrice(original, percent)
    Column(horizontalAlignment = horizontalAlignment) {
        Text(
            text = "${formatNumberWithSpaces(original.toString())} ${stringResource(R.string.uzs)}",
            fontFamily = robotoFontFamily,
            fontSize = 11.sp,
            textDecoration = TextDecoration.LineThrough,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
        )
        Text(
            text = "${formatNumberWithSpaces(discounted.toString())} ${stringResource(R.string.uzs)}",
            fontFamily = robotoFontFamily,
            fontWeight = fontWeight,
            fontSize = fontSize,
            color = accent
        )
    }
}
