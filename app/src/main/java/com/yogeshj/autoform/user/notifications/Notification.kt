package com.yogeshj.autoform.user.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class Notification(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "user_notifications"
    }

    // Method to create a notification channel
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "User Notifications"
        val descriptionText = "Notifications for user activities"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Method to send a notification with a dynamic title and message
    fun sendNotification(title: String, message: String) {
        // Check for POST_NOTIFICATIONS permission on Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission here if not granted
                return
            }
        }

        // Set up the notification
//        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(android.R.drawable.ic_dialog_info) // Change icon as needed
//            .setContentTitle(title) // Use passed title
//            .setContentText(message) // Use passed message
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Send the notification
//        with(NotificationManagerCompat.from(context)) {
////            notify(System.currentTimeMillis().toInt(), builder.build())
//        }
    }

    // Public method to handle sending notifications
    fun showNotification(title: String, message: String) {
        // Create the notification channel if needed (only on devices with API level >= 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        // Send the notification with dynamic title and message
        sendNotification(title, message)
    }
}