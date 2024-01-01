package org.don.onlineTrade.ui.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.don.onlineTrade.R
import org.don.onlineTrade.utils.reverseAppLanguageName

@Composable
fun HorizontalRadioGroup(
    selectedItem: (Int) -> Unit
) {
    var option by remember { mutableIntStateOf(1) }

    OutlinedCard {
        Row(modifier = Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .weight(1f)
                    .height(56.dp)
                    .selectable(
                        selected = (option == 1),
                        onClick = {
                            option = 1
                            selectedItem.invoke(option)
                        }

                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == 1),
                    onClick = {}
                )
                Text(
                    text = stringResource(id = R.string.txt_new),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                Modifier
                    .weight(1f)
                    .height(56.dp)
                    .selectable(
                        selected = (option == 2),
                        onClick = {
                            option = 2
                            selectedItem.invoke(option)
                        }

                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == 2),
                    onClick = {}
                )
                Text(
                    text = stringResource(id = R.string.txt_old),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

    }
}
