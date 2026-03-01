package org.don.onlineTrade.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


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
        ){
            Marker(
                state = MarkerState(position = destinationLatLng),
                title = "Location",
                snippet = "User location"
            )
        }

    }
}
