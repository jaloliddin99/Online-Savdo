package org.don.onlineTrade.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.chibatching.kotpref.Kotpref
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.HiltAndroidApp
import org.don.onlineTrade.data.worker.TokenRefreshScheduler
import org.don.onlineTrade.utils.ModelPref
import org.don.onlineTrade.utils.SharedPref
import javax.inject.Inject

@HiltAndroidApp
class App: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        ModelPref.with(this)
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST) {}

        if (SharedPref.refreshToken.isNotBlank()) {
            TokenRefreshScheduler.scheduleDailyRefresh(this)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
