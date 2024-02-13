package org.don.onlineTrade.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState

const val mapUserScreenNavigationRoute = "mapUserScreen/{latitude}/{longitude}"

fun NavGraphBuilder.mapUserScreen() {
    composable(
        route = mapUserScreenNavigationRoute,
        arguments = listOf(
            navArgument("latitude") {
                type = NavType.StringType
            },
            navArgument("longitude") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val lat = (backStackEntry.arguments?.getString("latitude") ?: return@composable).toDouble()
        val long = (backStackEntry.arguments?.getString("longitude") ?: return@composable).toDouble()
        MapShowLocationScreen(
            lat = lat, lon = long
        )
    }
}


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
        )

    }
}

