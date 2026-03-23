package uz.don.selling.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import uz.don.selling.data.remote.ApiInterface
import uz.don.selling.utils.SharedPref

@HiltWorker
class TokenRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val apiInterface: ApiInterface
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val refreshToken = SharedPref.refreshToken
        if (refreshToken.isBlank()) {
            Log.d("TokenRefresh", "No refresh token stored, skipping")
            return Result.success()
        }

        return try {
            val response = apiInterface.refreshToken(mapOf("refreshToken" to refreshToken))
            if (response.success && response.data != null) {
                val newAccessToken = response.data["token"]
                if (newAccessToken != null) {
                    SharedPref.deviceToken = "Bearer $newAccessToken"
                    SharedPref.loginTime = System.currentTimeMillis()
                    Log.d("TokenRefresh", "Access token refreshed successfully")
                    Result.success()
                } else {
                    Log.e("TokenRefresh", "No token in response")
                    Result.retry()
                }
            } else {
                Log.e("TokenRefresh", "Refresh failed: ${response.message}")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("TokenRefresh", "Error refreshing token", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "token_refresh_work"
    }
}
