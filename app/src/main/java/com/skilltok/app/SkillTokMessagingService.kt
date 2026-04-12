package com.skilltok.app

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SkillTokMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("SkillTokFCM", "New token: $token")
        sendTokenToFirestore(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "SkillTok"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        
        NotificationHelper(applicationContext).showNotification(title, body)
    }

    private fun sendTokenToFirestore(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .update("fcmToken", token)
            .addOnFailureListener { e ->
                Log.e("SkillTokFCM", "Failed to update token", e)
            }
    }
}
