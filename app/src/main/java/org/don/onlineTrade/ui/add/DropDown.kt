package org.don.onlineTrade.ui.add
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.don.onlineTrade.data.remote.models.currencies.ModelCurrencyListsItem
import org.don.onlineTrade.ui.theme.spacing


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdownSelector(
    list: List<org.don.onlineTrade.data.remote.models.leak.Unit>,
    preselected: org.don.onlineTrade.data.remote.models.leak.Unit,
    onSelectionChanged: (myData: org.don.onlineTrade.data.remote.models.leak.Unit) -> Unit,
    modifier: Modifier = Modifier
) {

    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) } // initial value

    OutlinedCard(
        modifier = modifier
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
                text = selected.label,
                modifier = Modifier.weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Icon(Icons.Outlined.ArrowDropDown, null, modifier = Modifier.padding(8.dp))

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.wrapContentWidth() // delete this modifier and use .wrapContentWidth() if you would like to wrap the dropdown menu around the content
            ) {
                list.forEach { listEntry ->

                    DropdownMenuItem(
                        onClick = {
                            selected = listEntry
                            expanded = false
                            onSelectionChanged(selected)
                        },
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.dimen16Dp),
                        text = {
                            Text(
                                text = listEntry.label,
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


@Preview(showBackground = true)
@Composable
fun SpinnerSample_Preview() {
    MaterialTheme {
        val myData = listOf(ModelCurrencyListsItem(0, "Apples"), ModelCurrencyListsItem(1, "Bananas"), ModelCurrencyListsItem(2, "Kiwis"))
//
//        SpinnerSample(
//            myData,
//            preselected = myData.first(),
//            onSelectionChanged = { },
//            modifier = Modifier.fillMaxWidth()
//        )
    }
}
