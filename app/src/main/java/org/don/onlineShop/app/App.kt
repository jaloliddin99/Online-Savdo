package org.don.onlineShop.app

import android.app.Application
import com.chibatching.kotpref.Kotpref
import dagger.hilt.android.HiltAndroidApp
import org.don.onlineShop.utils.ModelPref

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        ModelPref.with(this)
    }

}