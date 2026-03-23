package uz.don.selling.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.chibatching.kotpref.Kotpref
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.don.selling.data.remote.ApiInterface
import uz.don.selling.data.worker.TokenRefreshScheduler
import uz.don.selling.utils.ModelPref
import uz.don.selling.utils.SharedPref
import javax.inject.Inject

@HiltAndroidApp
class App: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var apiInterface: ApiInterface

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        ModelPref.with(this)
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST) {}

        if (SharedPref.refreshToken.isNotBlank()) {
            TokenRefreshScheduler.scheduleDailyRefresh(this)
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            SharedPref.fcmToken = token
            if (SharedPref.deviceToken.isNotBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        apiInterface.sendFcmToken(
                            SharedPref.deviceToken,
                            mapOf("fcmToken" to token)
                        )
                    } catch (_: Exception) {}
                }
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("all")
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
