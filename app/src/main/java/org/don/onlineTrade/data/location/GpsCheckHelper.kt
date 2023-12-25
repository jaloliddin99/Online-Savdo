package org.don.onlineTrade.data.location

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import androidx.activity.ComponentActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class GpsCheckHelper(val activity: Activity) {
    fun turnOnGpsDialogRequest() {
        val locationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGPS) turnOnGPS()
    }

    private fun turnOnGPS() {
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1200).apply {
                setMinUpdateDistanceMeters(0F)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                activity.let { it1 -> it.startResolutionForResult(it1, 12345) }
            }
        }
    }

}

fun checkGpsEnabled(activity: ComponentActivity): Boolean {
    val locationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}