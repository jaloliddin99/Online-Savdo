package org.don.onlineTrade.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import org.don.onlineTrade.app.App
import java.util.*

object LocaleManager {

    var FLAG_HAS_DATA = false
    fun setLocale(base: Context, language: String): Context? {
        SharedPref.language = language
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)

        val wrappedContext: Context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wrappedContext = base.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            wrappedContext = ContextWrapper(base).apply {
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }
        return wrappedContext
    }





}
