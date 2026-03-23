package uz.don.selling.data.location

import android.app.Application
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import uz.don.selling.domain.repository.LocationTracker
import uz.don.selling.utils.hasPermissionForLocation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
): LocationTracker {


    private var locationCallback: LocationCallback? = null

    override suspend fun getCurrentLocation(): Flow<Location> {
        return callbackFlow {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(location: LocationResult) {
                    super.onLocationResult(location)
                    location.lastLocation?.let {
                        launch { send(it) }
                    }
                }
            }
            startLocationUpdates()
            awaitClose {
                channel.close()
            }
        }.distinctUntilChanged()
    }

    override fun startLocationUpdate() {
        startLocationUpdates()
    }

    override fun stopLocationUpdate() {
        stopLocationUpdates()
    }

    @Throws(SecurityException::class)
    @MainThread
    fun startLocationUpdates(){
        val handler = Handler(Looper.getMainLooper())

        if (!application.hasPermissionForLocation()) return

        try {
            val locationRequest = LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1200).apply {
                    setMinUpdateDistanceMeters(0F)
                    setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                    setWaitForAccurateLocation(true)
                }.build()
            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                handler.looper
            )
        }catch (permissionRevoked: SecurityException){
            throw permissionRevoked
        }

    }


    @MainThread
    fun stopLocationUpdates(){
        locationCallback?.let {
            locationClient.removeLocationUpdates(it)
        }

    }

}