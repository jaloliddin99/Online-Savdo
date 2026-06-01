package uz.promo.selling.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import androidx.compose.ui.graphics.luminance
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.GoogleMapOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import uz.promo.selling.R


@Composable
fun MapShowLocationScreen(
    lat: Double,
    lon: Double
) {

    val destinationLatLng = LatLng(lat, lon)


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


    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val context = LocalContext.current
    val mapStyleOptions = remember(isDarkTheme) {
        if (isDarkTheme) {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
        } else null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            googleMapOptionsFactory = { GoogleMapOptions().liteMode(true) },
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isBuildingEnabled = true,
                mapStyleOptions = mapStyleOptions
            ),
            cameraPositionState = cameraPositionState
        ){
            Marker(
                state = MarkerState(position = destinationLatLng),
                title = "Location",
                snippet = "User location"
            )
        }

    }
}
