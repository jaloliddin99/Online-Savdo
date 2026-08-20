package uz.promo.selling.ui.main.add.dynamic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uz.promo.selling.R
import uz.promo.selling.data.remote.models.leak.Parameter
import uz.promo.selling.data.remote.models.leak.Values
import uz.promo.selling.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownSample(
    param: Parameter,
    onSelectionChanged: (myData: Values) -> Unit,
    initialKey: String? = null,
) {
    // Pre-select the AI-chosen value (matched by key); null when none.
    var selected by remember {
        mutableStateOf<Values?>(param.values.firstOrNull { it.key == initialKey })
    }
    var expanded by remember { mutableStateOf(false) }
    ContentWrapper(parameter = param) {
        OutlinedCard(
            modifier = Modifier
                .height(56.dp)
                .wrapContentWidth(),
            onClick = {
                expanded = !expanded
            },

            ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selected != null) {
                        translator(selected!!.label_uz, selected!!.label_ru, selected!!.label_en)
                    } else
                        stringResource(id = R.string.select_options),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Icon(Icons.Outlined.ArrowDropDown, null, modifier = Modifier.padding(8.dp))

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    param.values.forEach { listEntry ->

                        DropdownMenuItem(
                            onClick = {
                                selected = listEntry
                                expanded = false
                                onSelectionChanged(selected!!)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.spacing.dimen16Dp),
                            text = {
                                Text(
                                    text = translator(listEntry.label_uz, listEntry.label_ru, listEntry.label_en),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Start)
                                )
                            },
                        )
                    }
                }

            }
        }
    }
}

