package org.don.onlineTrade

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class AppFirebaseMessagingService :FirebaseMessagingService() {

    private lateinit var intent: Intent
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val message = remoteMessage.data["text"]
//        intent.putExtra(Constants.FSM_MESSAGE_KEY, message)
//        intent.setPackage(this.packageName)
//        sendBroadcast(intent)
    }

}