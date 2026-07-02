package uz.promo.selling.utils

import com.chibatching.kotpref.KotprefModel
import uz.promo.selling.ui.map.LATITUDE
import uz.promo.selling.ui.map.LONGITUDE

object SharedPref: KotprefModel() {

    var radius by intPref(10)
    var locationName by stringPref("")

    // True once the user has explicitly picked a location on the map. Until then
    // "near you" uses the live device location; afterwards it uses latitude/longitude.
    var hasPickedLocation by booleanPref(false)

    var deviceToken by stringPref()
    var refreshToken by stringPref()

    var loginTime by longPref()

    var language by stringPref("")
    var latitude by stringPref(LATITUDE.toString())
    var longitude by stringPref(LONGITUDE.toString())

    var userId by intPref(-1)

    var permissionCounter by intPref(0)

    var fcmToken by stringPref()

    // Payment orders awaiting provider confirmation. Persisted because Android
    // may kill our process while the user pays in the Click/Payme app — on
    // return the screen restarts polling from these.
    var pendingBoostOrderId by longPref(0L)
    var pendingPremiumOrderId by longPref(0L)

    // Device-side news opt-out (FCM topic "all" subscription).
    var newsNotifications by booleanPref(true)

    // True once the user dismisses the "personalize with AI" interests card on the
    // SearchScreen, so it isn't shown again even while they have no interests set.
    var interestsCardDismissed by booleanPref(false)

}