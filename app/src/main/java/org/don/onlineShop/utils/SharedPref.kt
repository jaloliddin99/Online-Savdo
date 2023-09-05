package org.don.onlineShop.utils

import com.chibatching.kotpref.KotprefModel

object SharedPref: KotprefModel() {


    val darkTheme by booleanPref( )
    val androidTheme by booleanPref(true)
    val disableDynamicTheming by booleanPref(true)

}