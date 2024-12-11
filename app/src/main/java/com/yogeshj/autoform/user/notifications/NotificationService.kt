package com.yogeshj.autoform.user.notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Build
import com.google.firebase.database.*

class NotificationService : Service() {

    companion object {
        const val CHANNEL_ID = "NotificationServiceChannel"
        lateinit var database: DatabaseReference
    }

    override fun onCreate() {
        super.onCreate()
        // Optional: Create the notification channel (not required for background service)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Listen for real-time database changes
        listenForDatabaseChanges()

        // Return sticky so service keeps running
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // This service doesn't support binding.
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cleanup or shutdown code if necessary
    }

    private fun listenForDatabaseChanges() {
        // Listen to changes in "UploadForm" node
        val uploadFormRef = database.child("UploadForm")

        uploadFormRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle new form added to the database
                val formData = snapshot.getValue(Form::class.java)
                formData?.let {
                    // Show notification for new form with both title and message
                    showCustomNotification("New Form Added", "Form Title: ${it.formTitle}")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle form update
                val updatedForm = snapshot.getValue(Form::class.java)
                updatedForm?.let {
                    // Show notification for form update with both title and message
                    showCustomNotification("Form Updated", "Form Title: ${it.formTitle}")
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle form removal
                val removedForm = snapshot.getValue(Form::class.java)
                removedForm?.let {
                    // Show notification for removed form with both title and message
                    showCustomNotification("Form Removed", "Form Title: ${it.formTitle}")
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // You can skip handling onChildMoved if you don't need it
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation (e.g., permission issues)
            }
        })
    }

    private fun showCustomNotification(title: String, message: String) {
        // Assuming you have a custom Notification class with a method to show notifications
        val notification = Notification(this) // Replace with your custom class
        notification.showNotification(title, message)
    }

    private fun createNotificationChannel() {
        // Create the channel only if necessary (for devices with API level >= 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Form Notifications"
            val descriptionText = "Notifications for form changes"
            val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
            val channel = android.app.NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: android.app.NotificationManager =
                getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Data class to represent form data structure
    data class Form(val formTitle: String = "", val formDescription: String = "") // Replace with your actual form data structure
}