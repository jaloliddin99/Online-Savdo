package uz.promo.selling.utils

import com.chibatching.kotpref.KotprefModel
import uz.promo.selling.ui.map.LATITUDE
import uz.promo.selling.ui.map.LONGITUDE

object SharedPref: KotprefModel() {

    var radius by intPref(10)
    var locationName by stringPref("")

    var deviceToken by stringPref()
    var refreshToken by stringPref()

    var loginTime by longPref()

    var language by stringPref("")
    var latitude by stringPref(LATITUDE.toString())
    var longitude by stringPref(LONGITUDE.toString())

    var userId by intPref(-1)

    var permissionCounter by intPref(0)

    var fcmToken by stringPref()

}