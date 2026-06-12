package uz.promo.selling.utils

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.promo.selling.AppFirebaseMessagingService

/**
 * Sends the current FCM token to the backend so pushes can reach this user.
 * Must run right after EVERY successful login: the token is generated at
 * install time (while logged out), so the app-startup sync alone misses the
 * fresh-install -> login flow and the user never receives notifications.
 */
object FcmTokenSync {

    fun sync(context: Context) {
        if (SharedPref.deviceToken.isBlank()) return
        val api = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AppFirebaseMessagingService.ApiEntryPoint::class.java
        ).apiInterface()

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            SharedPref.fcmToken = token
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    api.sendFcmToken(body = mapOf("fcmToken" to token))
                } catch (_: Exception) {
                }
            }
        }
    }
}
