package uz.don.onlineTrade.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    suspend fun getCurrentLocation(): Flow<Location>
    fun startLocationUpdate()
    fun stopLocationUpdate()
}