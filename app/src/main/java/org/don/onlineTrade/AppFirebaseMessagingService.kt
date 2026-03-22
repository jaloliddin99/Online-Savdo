package org.don.onlineTrade

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.don.onlineTrade.utils.SharedPref

class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        SharedPref.fcmToken = token
        // Send to backend if logged in
        if (SharedPref.deviceToken.isNotBlank()) {
            sendTokenToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Selling"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "selling_notifications"
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Selling Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "New posts matching your searches"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun sendTokenToServer(token: String) {
        // Use OkHttp directly since we can't use Hilt in a Service easily
        val client = okhttp3.OkHttpClient()
        val json = """{"fcmToken":"$token"}"""
        val body = okhttp3.RequestBody.create(
            okhttp3.MediaType.parse("application/json"),
            json
        )
        val request = okhttp3.Request.Builder()
            .url(BuildConfig.BASE_URL + "user/fcm-token")
            .post(body)
            .addHeader("Authorization", SharedPref.deviceToken)
            .build()

        Thread {
            try {
                client.newCall(request).execute().close()
            } catch (_: Exception) {}
        }.start()
    }
}
