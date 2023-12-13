package org.don.onlineTrade.ui.region.district

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.data.remote.models.region.Data
import org.don.onlineTrade.data.remote.models.region.DataDistrict
import org.don.onlineTrade.ui.home.RegionsScreenState
import org.don.onlineTrade.ui.region.DistrictItem
import org.don.onlineTrade.ui.region.RegionsViewModel
import org.don.onlineTrade.utils.FreeLoading

@Composable
fun DistrictsRoute(modifier: Modifier = Modifier,
                 onBackPressed: (DataDistrict, Data) -> Unit,
                   region: Data
) {
    val regionsViewModel = hiltViewModel<RegionsViewModel>()
    val state = regionsViewModel.state.value

    LaunchedEffect(key1 ="key"){
        regionsViewModel.getAllDistricts(regionId = region.id)
    }
    DistrictsScreen(
        modifier = modifier,
        state = state,
        onDistrictsRequested = onBackPressed::invoke,
        region
    )
}


@Composable
fun DistrictsScreen(
    modifier: Modifier = Modifier,
    state: RegionsScreenState,
    onDistrictsRequested: (district: DataDistrict, region:Data) -> Unit,
    region: Data
) {

    val isFeedLoading = state.isLoading
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyColumn {
            if (state.districts != null) {
                itemsIndexed(state.districts) { index, item ->
                    DistrictItem(
                        item = item,
                        onDistrictClicked = {
                            onDistrictsRequested(it, region)
                        }
                    )
                }
            }
        }
        FreeLoading(isFeedLoading = isFeedLoading)

        if (state.error.isNotBlank()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
        }



    }


}