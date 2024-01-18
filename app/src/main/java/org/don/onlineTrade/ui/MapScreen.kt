package org.don.onlineTrade.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
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
import org.don.onlineTrade.data.remote.models.reverse.FeatureMember
import org.don.onlineTrade.data.remote.models.reverse.GeoObject
import org.don.onlineTrade.ui.add.AskLocationDialog
import org.don.onlineTrade.ui.home.search.MapViewModel
import org.don.onlineTrade.ui.home.search.SearchToolbar
import org.don.onlineTrade.ui.theme.robotoFontFamily
import org.don.onlineTrade.ui.theme.spacing
import org.don.onlineTrade.utils.FreeLoading
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

    val singleLocation = state.singleFutureMember?.get(0)?.GeoObject
    LaunchedEffect(key1 = searchTextListener) {
        viewModel.listenRevereTyping(searchTextListener)
    }


    var showAskPermissionDialog by remember {
        mutableStateOf(false)
    }

    var showAlertDialog by remember {
        mutableStateOf(false)
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

    val destinationLatLng = if (state.singleFutureMember?.isNotEmpty() == true) {
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
        if (destinationLatLng.latitude != LATITUDE) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(destinationLatLng, finalZoom, TILT, BEARING)
                ),
                durationMs = ANIM_DURATION
            )
        } else {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(
                    destinationLatLng.latitude,
                    destinationLatLng.longitude
                ),
                ZOOM_LEVEL
            )
        }
    }


    var liftUpListener by remember {
        mutableStateOf(false)
    }


    var initialCameraPosition by remember { mutableStateOf(cameraPositionState.position) }

    val onMapCameraMoveStart: (cameraPosition: CameraPosition) -> Unit = {
        liftUpListener = true
        initialCameraPosition = it
    }
    val onMapCameraIdle: (cameraPosition: CameraPosition) -> Unit = { it ->
        liftUpListener = false
        viewModel.getLocationReverse(location = it.target, isMapMoved = true)
        initialCameraPosition = it
    }

    LaunchedEffect(key1 = cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving)
            onMapCameraMoveStart(cameraPositionState.position)
        else
            onMapCameraIdle(cameraPositionState.position)
    }


    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(
                mapType = MapType.NORMAL,
            ),
            cameraPositionState = cameraPositionState
        )

        ConstraintLayoutContent(singleLocation, liftUpListener, state.isMapMoved)

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
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            DisplayLocations(featureMember = state.featureMember, onBackClick = onBackClick)
            Spacer(modifier = Modifier.weight(1f))

            FloatingActionButton(
                onClick = {
                    turnOnGps(viewModel, hasNotPermission, activity, gpsNotEnabled)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))

            Button(
                enabled = singleLocation != null,
                onClick = {
                    val lat = singleLocation!!.Point.pos.split(" ")[0].toDouble()
                    val lon = singleLocation.Point.pos.split(" ")[1].toDouble()
                    onBackClick(
                        MapScreenData(
                            lat = lon,
                            lon = lat,
                            addressName = singleLocation.name,
                            addressDescription = singleLocation.description
                        )
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


@Composable
fun ConstraintLayoutContent(
    singleLocation: GeoObject?,
    liftUpListener: Boolean,
    isMapMoved: Boolean
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        val (markerImage) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(markerImage) {
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            MarkerContent(singleLocation, liftUpListener)
        }
    }
}


@Composable
fun MarkerContent(singleLocation: GeoObject?, liftUpListener: Boolean) {
    val yOffset by animateDpAsState(if (liftUpListener) (-20).dp else 0.dp, label = "")
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .offset(y = yOffset),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MarkerMainBox(singleLocation)
        Box(
            modifier = Modifier
                .height(24.dp)
                .width(2.dp)
                .background(Color.Black)
        )
    }
}


@Composable
fun MarkerMainBox(singleLocation: GeoObject?) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(MaterialTheme.spacing.dimen8Dp)
            )
            .padding(MaterialTheme.spacing.dimen4Dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Color.White,
                    shape = RoundedCornerShape(MaterialTheme.spacing.dimen8Dp)
                )
                .padding(4.dp)
        ) {
            val composition by
            rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.marker_location))
            LottieAnimation(
                modifier = Modifier.fillMaxSize(),
                composition = composition,
                iterations = LottieConstants.IterateForever,
            )
        }

        singleLocation?.let {
            AnimatedVisibility(visible = true) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 48.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Text(
                        text = it.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                    Text(
                        text = it.description,
                        fontSize = 11.sp
                    )
                }
            }
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


@Composable
fun DisplayLocations(
    featureMember: List<FeatureMember>?,
    onBackClick: (MapScreenData?) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.dimen16Dp)

    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(MaterialTheme.spacing.dimen16Dp)
                )
                .padding(horizontal = MaterialTheme.spacing.dimen16Dp)
        ) {
            featureMember?.let {
                itemsIndexed(featureMember) { index, item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val lat = item.GeoObject.Point.pos.split(" ")[0].toDouble()
                                val lon = item.GeoObject.Point.pos.split(" ")[1].toDouble()
                                onBackClick(
                                    MapScreenData(
                                        lat = lon,
                                        lon = lat,
                                        addressName = item.GeoObject.name,
                                        addressDescription = item.GeoObject.description
                                    )
                                )
                            },
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.GeoObject.name,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = robotoFontFamily
                        )
                        Text(
                            text = item.GeoObject.description,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = robotoFontFamily
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (index != it.lastIndex)
                            Divider(modifier = Modifier.height(0.4.dp))
                    }
                }

            }

        }
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
    val addressDescription: String = ""
) : Serializable

