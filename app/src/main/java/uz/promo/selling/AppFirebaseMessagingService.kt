package uz.promo.selling

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.promo.selling.data.remote.ApiInterface
import uz.promo.selling.utils.ChatPresence
import uz.promo.selling.utils.SharedPref
import androidx.core.net.toUri

class AppFirebaseMessagingService : FirebaseMessagingService() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ApiEntryPoint {
        fun apiInterface(): ApiInterface
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        SharedPref.fcmToken = token
        if (SharedPref.deviceToken.isNotBlank()) {
            sendTokenToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val data = remoteMessage.data
        val type = data["type"] ?: "news"
        val title = remoteMessage.notification?.title ?: data["title"] ?: "Selling"
        val body = remoteMessage.notification?.body ?: data["body"] ?: ""

        when (type) {
            "chat" -> {
                val conversationId = data["conversationId"]?.toLongOrNull() ?: -1L
                // Already looking at this conversation — the message appears live.
                if (conversationId > 0 && conversationId == ChatPresence.activeConversationId) return
                showNotification(
                    channelId = "chat",
                    title = title,
                    body = body,
                    deepLink = "selling://open/chat/$conversationId",
                    // One notification per conversation: new messages replace it.
                    notificationId = (10000 + conversationId).toInt()
                )
            }

            "search_match" -> showNotification(
                channelId = "search",
                title = title,
                body = body,
                deepLink = data["refId"]?.let { "selling://open/post/$it" },
                notificationId = System.currentTimeMillis().toInt()
            )

            "post_approved", "post_rejected", "boost_paid" -> showNotification(
                channelId = "my_ads",
                title = title,
                body = body,
                deepLink = data["refId"]?.let { "selling://open/post/$it" },
                notificationId = System.currentTimeMillis().toInt()
            )

            // News broadcasts and anything this app version doesn't know yet.
            else -> showNotification(
                channelId = "news",
                title = title,
                body = body,
                deepLink = "selling://open/notifications",
                notificationId = System.currentTimeMillis().toInt()
            )
        }
    }

    private fun showNotification(
        channelId: String,
        title: String,
        body: String,
        deepLink: String?,
        notificationId: Int
    ) {
        val intent = if (deepLink != null) {
            Intent(Intent.ACTION_VIEW, deepLink.toUri()).apply {
                setPackage(packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        } else {
            packageManager.getLaunchIntentForPackage(packageName)?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            this, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_sotiq)
            // Status-bar icons are rendered as monochrome silhouettes; the
            // recognizable colored logo goes in the large icon instead.
            .setLargeIcon(
                android.graphics.BitmapFactory.decodeResource(resources, R.drawable.sotiq_icon)
            )
            .setColor(0xFF1A6B3C.toInt()) // brand green tint for the small icon
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(
                if (channelId == "chat") NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    private fun sendTokenToServer(token: String) {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            ApiEntryPoint::class.java
        )
        val apiInterface = entryPoint.apiInterface()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                apiInterface.sendFcmToken(
                    body = mapOf("fcmToken" to token)
                )
            } catch (_: Exception) {}
        }
    }
}
