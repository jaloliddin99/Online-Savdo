package org.don.onlineTrade.ui.map

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState
import org.don.onlineTrade.R
import org.don.onlineTrade.data.location.GpsCheckHelper
import org.don.onlineTrade.data.location.checkGpsEnabled
import org.don.onlineTrade.ui.add.AskLocationDialog
import org.don.onlineTrade.ui.home.search.SearchToolbar
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
import org.don.onlineTrade.utils.hasPermissionForLocation
import org.don.onlineTrade.utils.runTimePermission.RunTimePermission
import java.io.Serializable


const val mapNavigationRoute = "mapNavigationRoute"

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

const val LATITUDE = 41.33261529612184
const val LONGITUDE = 69.25163862724608
const val ZOOM_LEVEL = 15f
const val ANIM_DURATION = 1000
const val TILT = 0f
const val BEARING = 0f

@Composable
fun MapsScreen(
    onBackClick: (MapScreenData?) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val activity = LocalContext.current as ComponentActivity
    val hasNotPermission = !hasPermissionForLocation(activity)
    val gpsNotEnabled = !checkGpsEnabled(activity)

    var searchTextListener by remember {
        mutableStateOf("")
    }
    var moveByUser by remember {
        mutableStateOf(false)
    }
    var showAskPermissionDialog by remember {
        mutableStateOf(false)
    }

    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    var liftUpListener by remember {
        mutableStateOf(false)
    }

    val singleLocation = state.singleFutureMember?.get(0)?.GeoObject
    LaunchedEffect(key1 = searchTextListener) {
        viewModel.listenRevereTyping(searchTextListener)
    }

    if (showAlertDialog) {
        AskLocationDialog(
            allowed = { granted ->
                showAlertDialog = false
                if (granted) {
                    showAskPermissionDialog = true
                }
            }
        )
    }
    if (state.latLng != null) {
        showAskPermissionDialog = false
    }

    if (showAskPermissionDialog) {
        showAskPermissionDialog = false
        turnOnGps(viewModel, hasNotPermission, activity, gpsNotEnabled)
    }

    val destinationLatLng =
        if (state.singleFutureMember?.isNotEmpty() == true) {
            val obj = state.singleFutureMember[0].GeoObject.Point.pos
            val lng = obj.split(" ")[0].toDouble()
            val lat = obj.split(" ")[1].toDouble()
            LatLng(lat, lng)
        } else
            LatLng(LATITUDE, LONGITUDE)

    val initialZoom = ZOOM_LEVEL
    val finalZoom = ZOOM_LEVEL

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(destinationLatLng, initialZoom)
    }

    LaunchedEffect(key1 = destinationLatLng) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(destinationLatLng, finalZoom, TILT, BEARING)
            ),
            durationMs = ANIM_DURATION
        )
    }


    var initialCameraPosition by remember { mutableStateOf(cameraPositionState.position) }

    val onMapCameraMoveStart: (cameraPosition: CameraPosition) -> Unit = {
        liftUpListener = true
        initialCameraPosition = it
    }
    val onMapCameraIdle: (cameraPosition: CameraPosition) -> Unit = { it ->
        liftUpListener = false
        initialCameraPosition = it
    }

    LaunchedEffect(key1 = cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving)
            onMapCameraMoveStart(cameraPositionState.position)
        else
            onMapCameraIdle(cameraPositionState.position)

    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isBuildingEnabled = true
            ),
            cameraPositionState = cameraPositionState
        )

        ConstraintLayoutContent(singleLocation, liftUpListener)

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
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
                onFilterClicked = {}
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            DisplayLocations(featureMember = state.featureMember, onBackClick = {
                getLocation(singleLocation, state.regionDistrictData)
            })
            Spacer(modifier = Modifier.weight(1f))

            FloatingActionButton(
                onClick = {
                    moveByUser = true
                    turnOnGps(viewModel, hasNotPermission, activity, gpsNotEnabled)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = MaterialTheme.spacing.dimen16Dp)
            ) {
                Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            Button(
                enabled = singleLocation != null,
                onClick = {
                    onBackClick(
                        getLocation(singleLocation, state.regionDistrictData)
                    )
                },
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.spacing.dimen16Dp,
                        end = MaterialTheme.spacing.dimen16Dp,
                        bottom = 60.dp,
                    )
                    .height(48.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(
                            id = R.string.select_this_location
                        )
                    )
                }
            }
        }
        FreeLoading(
            isFeedLoading = state.isLoading, paddingTop = 56.dp
        )
        if (state.error.isNotBlank()) {
            Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_SHORT).show()
        }
    }
}


private fun turnOnGps(
    viewModel: MapViewModel,
    hasNotPermission: Boolean,
    activity: Activity,
    gpsNotEnabled: Boolean
) {
    if (hasNotPermission) {
        askPermission(activity) {
            GpsCheckHelper(activity).turnOnGpsDialogRequest()
            viewModel.locationObserve()
            viewModel.startLocationUpdates()
        }
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



fun askPermission(activity: Activity, enabled: () -> Unit) {
    RunTimePermission().locationPermission(
        onPermissionEnabled = enabled,
        onPermissionNotEnabled = {},
        activity
    )
}

data class MapScreenData(
    val lat: Double? = null,
    val lon: Double? = null,
    val addressName: String = "",
    val addressDescription: String = "",
    val regionId: Int = -1,
    val districtId: Int = -1
) : Serializable

