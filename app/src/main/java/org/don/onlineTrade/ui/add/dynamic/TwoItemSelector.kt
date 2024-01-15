package org.don.onlineTrade.ui.add.dynamic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.don.onlineTrade.R
import org.don.onlineTrade.data.remote.models.leak.Parameter
import org.don.onlineTrade.data.remote.models.leak.Values
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing

@Composable
fun HorizontalRadioGroup(
    selectedItem: (Values) -> Unit,
    param: Parameter,
) {
    var option by remember { mutableIntStateOf(0) }

    Column {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        Text(
            text = "${param.label}${if (param.validation.is_required) "*" else ""}",
            fontSize = MaterialTheme.spacing.dimen16Sp,
            fontFamily = robotoFontFamily,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen8Dp))
        OutlinedCard {
            Row(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .weight(1f)
                        .height(56.dp)
                        .selectable(
                            selected = (option == 0),
                            onClick = {
                                option = 0
                                selectedItem.invoke(param.values[option])
                            }

                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == 0),
                        onClick = {
                            option = 0
                            selectedItem.invoke(param.values[option])
                        }
                    )
                    Text(
                        text = param.values[0].label,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    Modifier
                        .weight(1f)
                        .height(56.dp)
                        .selectable(
                            selected = (option == 1),
                            onClick = {
                                option = 1
                                selectedItem.invoke(param.values[option])
                            }

                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == 1),
                        onClick = {
                            option = 1
                            selectedItem.invoke(param.values[option])
                        }
                    )
                    Text(
                        text = param.values[1].label,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

        }
    }
}
