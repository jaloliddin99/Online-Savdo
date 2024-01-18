package org.don.onlineTrade.ui

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.don.onlineTrade.data.location.GpsCheckHelper
import org.don.onlineTrade.data.location.checkGpsEnabled
import org.don.onlineTrade.ui.add.AskLocationDialog
import org.don.onlineTrade.ui.home.search.MapViewModel
import org.don.onlineTrade.ui.home.search.SearchToolbar
import org.don.onlineTrade.utils.hasPermissionForLocation
import org.don.onlineTrade.utils.runTimePermission.RunTimePermission


@Composable
fun MapsScreen(
    onBackClick: () -> Unit,
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

    val singapore = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 10f)
    }

    Box {
        Column {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            SearchToolbar(
                onBackClick = onBackClick,
                onSearchQueryChanged = {
                    searchTextListener = it
                },
                onSearchTriggered = {
                    searchTextListener = it
                },
                searchQuery = searchTextListener,
            )

        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = true,
                mapType = MapType.HYBRID,
                isTrafficEnabled = true
            )
        ) {
            Marker(
                state = MarkerState(position = LatLng(41.33261529612184, 69.25163862724608)),
                title = "MyPosition",
                snippet = "This is a description of this Marker",
                draggable = true
            )
        }

    }
}