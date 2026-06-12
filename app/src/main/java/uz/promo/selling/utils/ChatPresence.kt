package uz.promo.selling.utils

/**
 * Tracks which conversation the user is currently looking at so the FCM
 * service can skip the notification (the message already appears live via
 * the chat WebSocket/refresh).
 */
object ChatPresence {
    @Volatile
    var activeConversationId: Long = -1L
}
