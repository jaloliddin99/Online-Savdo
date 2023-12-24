package org.don.onlineTrade.app

import android.app.Application
import android.content.Context
import com.chibatching.kotpref.Kotpref
import dagger.hilt.android.HiltAndroidApp
import org.don.onlineTrade.utils.LocaleManager
import org.don.onlineTrade.utils.ModelPref
import org.don.onlineTrade.utils.SharedPref

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        ModelPref.with(this)
    }




}