package org.don.onlineTrade.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import org.don.onlineTrade.R
import org.don.onlineTrade.data.location.GpsCheckHelper
import org.don.onlineTrade.data.location.checkGpsEnabled
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


    val tashkent = if (state.singleFutureMember?.isNotEmpty() == true) {
        val obj = state.singleFutureMember[0].GeoObject.Point.pos
        val lng = obj.split(" ")[0].toDouble()
        val lat = obj.split(" ")[1].toDouble()
        LatLng(lat, lng)
    } else
        LatLng(41.33261529612184, 69.25163862724608)

    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(
                mapType = MapType.NORMAL,
            ),
            cameraPositionState = CameraPositionState(CameraPosition.fromLatLngZoom(tashkent, 15f))
        ) {
            if (state.singleFutureMember?.isNotEmpty() == true) {
                Marker(
                    state = MarkerState(position = tashkent),
                    title = state.singleFutureMember[0].GeoObject.name,
                    snippet = state.singleFutureMember[0].GeoObject.description,
                )
            }

        }

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
                    state.featureMember?.let {
                        itemsIndexed(state.featureMember) { index, item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val lat = item.GeoObject.Point.pos.split(" ")[0].toDouble()
                                        val lon = item.GeoObject.Point.pos.split(" ")[1].toDouble()
                                        onBackClick(
                                            MapScreenData(
                                                lat = lat,
                                                lon = lon,
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


            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    turnOnGps(viewModel, hasNotPermission, activity, gpsNotEnabled)
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
                    Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null)
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen12Dp))
                    Text(text = stringResource(id = R.string.use_gps))
                }
            }


        }
        FreeLoading(isFeedLoading = state.isLoading)

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
    val addressDescription: String = ""
) : Serializable