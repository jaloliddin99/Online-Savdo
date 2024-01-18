package org.don.onlineTrade.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.don.onlineTrade.data.location.GpsCheckHelper
import org.don.onlineTrade.data.location.checkGpsEnabled
import org.don.onlineTrade.ui.add.AskLocationDialog
import org.don.onlineTrade.ui.home.search.MapViewModel
import org.don.onlineTrade.ui.home.search.SearchToolbar
import org.don.onlineTrade.utils.hasPermissionForLocation
import org.don.onlineTrade.utils.runTimePermission.RunTimePermission
import java.io.Serializable


const val mapNavigationRoute = "mapNavigationRoute"
fun NavController.navigateToNotifications(navOptions: NavOptions? = null) {
    this.navigate(mapNavigationRoute, navOptions)
}

fun NavGraphBuilder.mapScreen(
    onBackClick: (MapScreenData?) -> Unit,
) {
    composable(
        route = mapNavigationRoute,
    ) {
        MapsScreen(
            onBackClick = onBackClick
        )
    }
}

@Composable
fun MapsScreen(
    onBackClick: (MapScreenData?) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    var searchTextListener by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = searchTextListener) {
        viewModel.listenRevereTyping(searchTextListener)
    }



    var showAskPermissionDialog by remember {
        mutableStateOf(false)
    }



    if (state.showAlertDialog) {
        AskLocationDialog(
            allowed = { granted ->
                viewModel.hideAlertDialog()
                if (granted) {
                    showAskPermissionDialog = true
                }
            }
        )
    }

    if (state.latLng != null){
        showAskPermissionDialog = false
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
                    viewModel.locationObserve()
                    viewModel.startLocationUpdates()
                },
                onPermissionNotEnabled = {},
                activity
            )
            return
        }
        if (gpsNotEnabled) {
            GpsCheckHelper(activity).turnOnGpsDialogRequest()
            viewModel.locationObserve()
            viewModel.startLocationUpdates()
        } else {
            viewModel.locationObserve()
            viewModel.startLocationUpdates()
        }

    }


    Box {
        Column {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            SearchToolbar(
                onBackClick = {
                    onBackClick(null)
                },
                onSearchQueryChanged = {
                    searchTextListener = it
                },
                onSearchTriggered = {
                    searchTextListener = it
                },
                searchQuery = searchTextListener,
            )

        }

//        GoogleMap(
//            modifier = Modifier.fillMaxSize(),
//            properties = MapProperties(
//                isMyLocationEnabled = true,
//                mapType = MapType.NORMAL,
//                isTrafficEnabled = true
//            )
//        ) {
//            Marker(
//                state = MarkerState(position = LatLng(41.33261529612184, 69.25163862724608)),
//                title = "MyPosition",
//                snippet = "This is a description of this Marker",
//                draggable = true
//            )
//        }

    }
}

data class MapScreenData(
    val lat: Double? = null,
    val lon: Double? = null,
    val addressName: String = "",
    val addressDescription: String = ""
):Serializable