package org.don.onlineTrade.utils

import com.chibatching.kotpref.KotprefModel

object SharedPref: KotprefModel() {


    var deviceToken by stringPref()

    var loginTime by longPref()

    var language by stringPref("uz")

    var userId by intPref(-1)


















}