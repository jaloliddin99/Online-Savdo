package org.don.bottomappbar.utils

import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.KotprefModel

object SharedPref: KotprefModel() {


    val darkTheme by booleanPref( )
    val androidTheme by booleanPref(true)
    val disableDynamicTheming by booleanPref(true)

}