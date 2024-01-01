package org.don.onlineTrade.ui.region.district

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import org.don.onlineTrade.data.location.GpsCheckHelper
import org.don.onlineTrade.data.location.checkGpsEnabled
import org.don.onlineTrade.data.remote.models.region.Data
import org.don.onlineTrade.data.remote.models.region.DataDistrict
import org.don.onlineTrade.ui.add.AskLocationDialog
import org.don.onlineTrade.ui.add.ProductTitleStateSaver
import org.don.onlineTrade.ui.home.GPSEnableView
import org.don.onlineTrade.ui.home.NearPosts
import org.don.onlineTrade.ui.home.RegionsScreenState
import org.don.onlineTrade.ui.region.DistrictItem
import org.don.onlineTrade.ui.region.MyLocation
import org.don.onlineTrade.ui.region.RegionsViewModel
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.LocaleManager
import org.don.onlineTrade.utils.hasPermissionForLocation
import org.don.onlineTrade.utils.runTimePermission.RunTimePermission

@Composable
fun DistrictsRoute(
    modifier: Modifier = Modifier,
    onBackPressed: (DataDistrict, Data, lat: String, lon: String) -> Unit,
    region: Data
) {
    DistrictsScreen(
        modifier = modifier,
        onDistrictsRequested = onBackPressed::invoke,
        region
    )
}


@Composable
fun DistrictsScreen(
    modifier: Modifier = Modifier,
    onDistrictsRequested: (district: DataDistrict, region: Data, lat: String, lon: String) -> Unit,
    region: Data,
    regionsViewModel: RegionsViewModel = hiltViewModel()

) {
    val state = regionsViewModel.state.value

    LaunchedEffect(key1 = "key") {
        regionsViewModel.getAllDistricts(regionId = region.id)
    }

    var showAskPermissionDialog by remember {
        mutableStateOf(false)
    }



    if (state.showAlertDialog) {
        AskLocationDialog(
            allowed = { granted ->
                regionsViewModel.hideAlertDialog()
                if (granted) {
                    showAskPermissionDialog = true
                } else {
                    onDistrictsRequested(state.district!!, region, "0.0", "0.0")
                }
            }
        )
    }

    if (state.myLocation != null){
        showAskPermissionDialog = false
        onDistrictsRequested(
            state.district!!,
            region,
            state.myLocation.lat.toString(),
            state.myLocation.lon.toString()
        )
    }

    if (showAskPermissionDialog) {
        showAskPermissionDialog = false
        val activity = LocalContext.current as ComponentActivity
        val hasNotPermission = !hasPermissionForLocation(activity)
        val gpsNotEnabled = !checkGpsEnabled(activity)

        if (hasNotPermission) {
            RunTimePermission().locationPermission(
                onPermissionEnabled = {
                    GpsCheckHelper(activity).turnOnGpsDialogRequest()
                    regionsViewModel.locationObserve()
                    regionsViewModel.startLocationUpdates()
                },
                onPermissionNotEnabled = {},
                activity
            )
            return
        }
        if (gpsNotEnabled) {
            GpsCheckHelper(activity).turnOnGpsDialogRequest()
            regionsViewModel.locationObserve()
            regionsViewModel.startLocationUpdates()
        } else {
            regionsViewModel.locationObserve()
            regionsViewModel.startLocationUpdates()
        }

    }

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
                            regionsViewModel.updateDistrictAndShowAlertDialog(it)
                        }
                    )
                }
            }
        }
        FreeLoading(isFeedLoading = state.isLoading)

        if (state.error.isNotBlank()) {
            Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_SHORT).show()
        }
    }


}