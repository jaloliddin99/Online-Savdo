package uz.don.selling.ui.main.add.dynamic

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.don.selling.data.remote.models.leak.Parameter
import uz.don.selling.data.remote.models.leak.Values

@Composable
fun HorizontalRadioGroup(
    selectedItem: (Values) -> Unit,
    param: Parameter,
) {
    var option by remember { mutableStateOf<Int?>(null) }

    ContentWrapper(parameter = param) {
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
                                selectedItem.invoke(param.values[option!!])
                            }

                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == 0),
                        onClick = {
                            option = 0
                            selectedItem.invoke(param.values[option!!])
                        }
                    )
                    Text(
                        text = translator(param.values[0].label_uz, param.values[0].label_ru),
                        modifier = Modifier.padding(start = 8.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
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
                                selectedItem.invoke(param.values[option!!])
                            }

                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == 1),
                        onClick = {
                            option = 1
                            selectedItem.invoke(param.values[option!!])
                        }
                    )
                    Text(
                        text = translator(param.values[1].label_uz, param.values[1].label_ru),
                        modifier = Modifier.padding(start = 8.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

        }
    }
}
