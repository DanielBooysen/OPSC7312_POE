package com.example.opsc7312_poe

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.preference.PreferenceManager

class FirebaseMsgService : FirebaseMessagingService() {
    private val CHANNEL_ID = "notification_channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Retrieve user preference for push notifications
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val notificationsEnabled = sharedPreferences.getBoolean("push_notifications", true)

        // Check if notifications are enabled
        if (notificationsEnabled) {
            val title = remoteMessage.notification?.title ?: "Hey!"
            val message = remoteMessage.notification?.body ?: "Someone has posted a new catch!"

            // Create a notification channel for Android O and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID, "Notifications", NotificationManager.IMPORTANCE_HIGH
                )
                val manager = getSystemService(NotificationManager::class.java)
                manager?.createNotificationChannel(channel)
            }

            // Build and display the notification
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(1, notification)
        }
    }
}
