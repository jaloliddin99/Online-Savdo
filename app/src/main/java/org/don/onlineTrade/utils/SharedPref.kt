package org.don.onlineTrade.utils

import com.chibatching.kotpref.KotprefModel

object SharedPref: KotprefModel() {


    var deviceLoggedIn by booleanPref()
    var deviceToken by stringPref()
    var expirationTime by intPref()



    var loginTime by longPref()

    var language by stringPref("uz")

    var userId by intPref()


}