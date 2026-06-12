package uz.promo.selling.app

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
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.data.worker.TokenRefreshScheduler
import uz.promo.selling.utils.ModelPref
import uz.promo.selling.utils.SharedPref
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
        createNotificationChannels()

        if (SharedPref.refreshToken.isNotBlank()) {
            TokenRefreshScheduler.scheduleDailyRefresh(this)
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            SharedPref.fcmToken = token
            if (SharedPref.deviceToken.isNotBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        apiInterface.sendFcmToken(
                            body = mapOf("fcmToken" to token)
                        )
                    } catch (_: Exception) {}
                }
            }
        }
        if (SharedPref.newsNotifications) {
            FirebaseMessaging.getInstance().subscribeToTopic("all")
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("all")
        }
    }

    /**
     * One channel per notification category so users can mute exactly what
     * they want in system settings. Created up-front (channel importance
     * can't be raised later once a channel exists on the device).
     */
    private fun createNotificationChannels() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) return
        val manager = getSystemService(android.app.NotificationManager::class.java)
        val channels = listOf(
            android.app.NotificationChannel(
                "chat", getString(uz.promo.selling.R.string.channel_chat),
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply { description = getString(uz.promo.selling.R.string.channel_chat_desc) },
            android.app.NotificationChannel(
                "search", getString(uz.promo.selling.R.string.channel_search),
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = getString(uz.promo.selling.R.string.channel_search_desc) },
            android.app.NotificationChannel(
                "news", getString(uz.promo.selling.R.string.channel_news),
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = getString(uz.promo.selling.R.string.channel_news_desc) },
            android.app.NotificationChannel(
                "my_ads", getString(uz.promo.selling.R.string.channel_my_ads),
                android.app.NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = getString(uz.promo.selling.R.string.channel_my_ads_desc) },
        )
        channels.forEach { manager.createNotificationChannel(it) }
        // Migrate away from the old catch-all channel.
        manager.deleteNotificationChannel("selling_notifications")
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
