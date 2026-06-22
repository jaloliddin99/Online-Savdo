package uz.promo.selling.ui.map

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.android.gms.maps.GoogleMapOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.graphics.luminance
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uz.promo.selling.R
import uz.promo.selling.data.location.checkGpsEnabled
import uz.promo.selling.ui.main.home.search.filter.SearchToolbar
import uz.promo.selling.ui.theme.robotoFontFamily
import uz.promo.selling.ui.theme.spacing
import uz.promo.selling.utils.FreeLoading
import uz.promo.selling.utils.SharedPref
import uz.promo.selling.utils.hasPermissionForLocation
import kotlin.math.ln
import androidx.core.graphics.toColorInt

@Composable
fun SelectRadiusMapScreen(
    onBackClick: (MapScreenData?) -> Unit,
    viewModel: MapViewModel = hiltViewModel<MapViewModel>()
) {
    val state = viewModel.state.value
    val activity = LocalActivity.current as ComponentActivity
    val hasNotPermission = !hasPermissionForLocation(activity)
    val gpsNotEnabled = !checkGpsEnabled(activity)

    var searchTextListener by remember { mutableStateOf("") }
    var sliderPosition by remember { mutableFloatStateOf(sliderFromKm(SharedPref.radius.toFloat())) }
    val radiusKm = kmFromSlider(sliderPosition).toInt()
    var markerPosition by remember {
        mutableStateOf(
            LatLng(
                SharedPref.latitude.toDouble(),
                SharedPref.longitude.toDouble()
            )
        )
    }


    // Update marker when user location arrives
    LaunchedEffect(state.latLng) {
        state.latLng?.let { markerPosition = it }
    }

    // Update marker only when user picks a search result (not from reverse geocode)
    var lastSearchSelection by remember { mutableStateOf<LatLng?>(null) }
    val destinationLatLng =
        if (state.singleFutureMember?.isNotEmpty() == true) {
            val obj = state.singleFutureMember[0].GeoObject.Point.pos
            val lng = obj.split(" ")[0].toDouble()
            val lat = obj.split(" ")[1].toDouble()
            LatLng(lat, lng)
        } else null

    LaunchedEffect(destinationLatLng) {
        // Only move marker for search selections, not for reverse geocode responses
        if (destinationLatLng != null && destinationLatLng != lastSearchSelection) {
            lastSearchSelection = destinationLatLng
            markerPosition = destinationLatLng
        }
    }

    LaunchedEffect(searchTextListener) {
        viewModel.listenRevereTyping(searchTextListener)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, ZOOM_LEVEL)
    }

    // Animate camera to marker position
    LaunchedEffect(markerPosition) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(markerPosition, cameraPositionState.position.zoom, TILT, BEARING)
            ),
            durationMs = ANIM_DURATION
        )
    }

    // Adjust camera zoom so the circle always fits on screen
    LaunchedEffect(radiusKm) {
        val zoom = (13.5 - ln(radiusKm.toDouble()) / ln(2.0)).toFloat().coerceIn(2f, 20f)
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(cameraPositionState.position.target, zoom, TILT, BEARING)
            ),
            durationMs = 300
        )
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val strokeWidthPx = with(LocalDensity.current) { 3.dp.toPx() }
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val context = LocalContext.current
    val mapStyleOptions = remember(isDarkTheme) {
        if (isDarkTheme) {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
        } else null
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            googleMapOptionsFactory = {
                if (isDarkTheme) {
                    GoogleMapOptions().backgroundColor("#242f3e".toColorInt())
                } else {
                    GoogleMapOptions()
                }
            },
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isBuildingEnabled = true,
                mapStyleOptions = mapStyleOptions,
                latLngBoundsForCameraTarget = UZBEKISTAN_BOUNDS
            ),
            uiSettings = MapUiSettings(zoomControlsEnabled = false),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                markerPosition = latLng
                viewModel.reverseGeocodeFromCamera(latLng)
            }
        ) {
            Marker(
                state = MarkerState(position = markerPosition),
                title = stringResource(id = R.string.select_this_location)
            )
            Circle(
                center = markerPosition,
                radius = radiusKm.toDouble() * 1000,
                strokeWidth = strokeWidthPx,
                strokeColor = primaryColor,
                fillColor = primaryColor.copy(alpha = 0.1f)
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            SearchToolbar(
                onBackClick = { onBackClick(null) },
                onSearchQueryChanged = { searchTextListener = it },
                onSearchTriggered = { searchTextListener = it },
                searchQuery = searchTextListener,
                autoFocus = false
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.dimen16Dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(MaterialTheme.spacing.dimen12Dp)
                    )
                    .padding(horizontal = MaterialTheme.spacing.dimen12Dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 0f..1f,
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.dimen4Dp))
                Text(
                    text = "${radiusKm.toInt()} km",
                    fontWeight = FontWeight.Medium,
                    fontFamily = robotoFontFamily,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))
            DisplayLocations(
                featureMember = state.featureMember,
                onItemClick = { featureMember ->
                    viewModel.selectLocation(featureMember)
                    searchTextListener = ""
                }
            )
            Spacer(modifier = Modifier.weight(1f))

            FloatingActionButton(
                onClick = {
                    turnOnGps(viewModel, hasNotPermission, activity, gpsNotEnabled)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = MaterialTheme.spacing.dimen16Dp)
            ) {
                Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.dimen12Dp))

            // Extract location name from reverse geocode Components
            val locationName = remember(state.singleFutureMember) {
                val components = state.singleFutureMember?.firstOrNull()
                    ?.GeoObject?.metaDataProperty?.GeocoderMetaData?.Address?.Components
                val raw = components?.firstOrNull { it.kind == "area" }?.name
                    ?: components?.firstOrNull { it.kind == "province" }?.name
                    ?: ""
                raw.removeSuffix(" tumani").removeSuffix(" район").removeSuffix(" District")
            }

            Button(
                onClick = {
                    SharedPref.radius = radiusKm
                    SharedPref.latitude = markerPosition.latitude.toString()
                    SharedPref.longitude = markerPosition.longitude.toString()
                    SharedPref.hasPickedLocation = true
                    if (locationName.isNotBlank()) {
                        SharedPref.locationName = locationName
                    }
                    onBackClick(
                        MapScreenData(
                            lat = markerPosition.latitude,
                            lon = markerPosition.longitude,
                            radiusKm = radiusKm
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
                Text(
                    text = stringResource(id = R.string.select_this_location)
                )
            }
        }
        FreeLoading(isFeedLoading = state.isLoading, paddingTop = 56.dp)
        if (state.error.isNotBlank()) {
            Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_SHORT).show()
        }
    }
}

// Piecewise linear mapping: 50% of slider = 1–20km, 25% = 20–50km, 25% = 50–500km
private fun kmFromSlider(position: Float): Float = when {
    position <= 0.5f -> 1f + (position / 0.5f) * 19f        // 1..20
    position <= 0.75f -> 20f + ((position - 0.5f) / 0.25f) * 30f  // 20..50
    else -> 50f + ((position - 0.75f) / 0.25f) * 450f       // 50..500
}

private fun sliderFromKm(km: Float): Float = when {
    km <= 20f -> ((km - 1f) / 19f) * 0.5f
    km <= 50f -> 0.5f + ((km - 20f) / 30f) * 0.25f
    else -> 0.75f + ((km - 50f) / 450f) * 0.25f
}
