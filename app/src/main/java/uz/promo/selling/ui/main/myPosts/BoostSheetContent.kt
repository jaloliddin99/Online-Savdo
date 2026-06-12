package uz.promo.selling.ui.main.myPosts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.payments.BoostTariff
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.utils.formatNumberWithSpaces

/**
 * Paid boost: pick a duration, then a payment provider. The checkout page
 * opens in the browser; the backend applies the boost once the provider
 * confirms payment.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BoostSheetContent(
    tariffs: List<BoostTariff>,
    busy: Boolean,
    onPay: (hours: Int, provider: String) -> Unit
) {
    var selectedHours by remember(tariffs) {
        mutableStateOf(tariffs.firstOrNull()?.hours)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.boost_title),
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = stringResource(R.string.boost_subtitle),
            fontFamily = robotoFontFamily,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        if (tariffs.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tariffs.forEach { tariff ->
                    val isSelected = selectedHours == tariff.hours
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable { selectedHours = tariff.hours }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${tariff.hours} ${stringResource(R.string.hours_short)}",
                            fontFamily = robotoFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${formatNumberWithSpaces(tariff.price.toString())} ${stringResource(R.string.uzs)}",
                            fontFamily = robotoFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { selectedHours?.let { onPay(it, "payme") } },
                enabled = !busy && selectedHours != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF33CCCC),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.pay_with_payme),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { selectedHours?.let { onPay(it, "click") } },
                enabled = !busy && selectedHours != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0073E6),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.pay_with_click),
                    fontFamily = robotoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
            if (busy) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
