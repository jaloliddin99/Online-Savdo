package org.don.onlineTrade.utils

import com.chibatching.kotpref.KotprefModel
import org.don.onlineTrade.ui.map.LATITUDE
import org.don.onlineTrade.ui.map.LONGITUDE

object SharedPref: KotprefModel() {

    var radius by intPref(10)

    var deviceToken by stringPref()
    var refreshToken by stringPref()

    var loginTime by longPref()

    var language by stringPref("uz")
    var latitude by stringPref(LATITUDE.toString())
    var longitude by stringPref(LONGITUDE.toString())

    var userId by intPref(-1)

    var permissionCounter by intPref(0)


}