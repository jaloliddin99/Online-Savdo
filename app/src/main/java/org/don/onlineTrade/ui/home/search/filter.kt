package org.don.onlineTrade.ui.home.search

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.data.remote.models.region.District
import org.don.onlineTrade.data.remote.models.region.RegionDistrict
import org.don.onlineTrade.ui.map.MapViewModel
import org.don.onlineTrade.utils.FreeLoading

// Define showDatePicker inside the composable function


@Composable
fun ShowRegionsDialog(
    onDismissRequest: () -> Unit,
    onSelectRequest: (region: RegionDistrict, district:District) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {

    val state = viewModel.state.value


    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        title = {
            Text(text = "Select Region")
        },
        text = {
            if (state.regionDistrictData!= null){
                RegionsList(state.regionDistrictData, onSelectRequest)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text("Close")
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(horizontal = 10.dp)
    )
    FreeLoading(
        isFeedLoading = state.isLoading, paddingTop = 56.dp
    )
    if (state.error.isNotBlank()) {
        Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun RegionsList(
    regions: List<RegionDistrict>,
    onSelectRequest: (region: RegionDistrict, district:District) -> Unit
) {
    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) { // Limit height and allow scrolling
        items(regions) { region ->
            ExpandableRegionItem(region, onSelectRequest)
        }
    }
}


@Composable
fun ExpandableRegionItem(
    region: RegionDistrict,
    onSelectRequest: (region: RegionDistrict, district:District) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { expanded = !expanded } ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = region.name,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier.padding(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Expand/Collapse",
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (expanded) 90f else 0f)
                )
            }
        }
        Divider(modifier = Modifier.height(0.3.dp))
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                region.districts.forEach { district ->
                    Text(
                        text = district.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectRequest.invoke(region, district)
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
