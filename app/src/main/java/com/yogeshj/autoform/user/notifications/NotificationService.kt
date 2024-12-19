package com.yogeshj.autoform.user.notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.uploadForm.uploadNewFormFragment.FormDetails

class NotificationService : Service() {

    companion object {
        const val CHANNEL_ID = "NotificationServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("NotificationService", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Listen for real-time database changes
        listenForDatabaseChanges()
//        Toast.makeText(this, "Notification Service Started", Toast.LENGTH_LONG).show()
        Log.d("NotificationService", "Service is running")

        // Return sticky so service keeps running
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // This service doesn't support binding.
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NotificationService", "Service destroyed")
    }

    private fun findFieldOfInterestCategories(callback: (Set<String>) -> Unit) {
        val category = HashSet<String>()
        val db2 = FirebaseDatabase.getInstance().getReference("UsersInfo")
        db2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val currentUser = snap.getValue(User::class.java)
                        if (currentUser != null && FirstScreenActivity.auth.currentUser?.uid == currentUser.uid) {
                            currentUser.field1?.let { category.add(it.toString()) }
                            currentUser.field2?.let { category.add(it.toString()) }
                            currentUser.field3?.let { category.add(it.toString()) }
                        }
                    }
                }
                callback(category)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("findFieldOfInterest", "Database error: ${error.message}")
                callback(emptySet())
            }
        })
    }

    private fun listenForDatabaseChanges() {
        val db = FirebaseDatabase.getInstance().getReference("UploadForm")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    findFieldOfInterestCategories { category ->
                        for (snap in snapshot.children) {
                            for (examSnap in snap.children) {
                                val currentUser = examSnap.getValue(FormDetails::class.java)
                                val childCategory = examSnap.child("category").getValue(String::class.java)
                                if (currentUser!=null && childCategory != null && category.contains(childCategory)) {
                                    val notification = Notification(applicationContext)
                                    notification.sendNotification(
                                        "New Form Uploaded",
                                        "A new form is uploaded for you."
                                    )
                                    Log.d("DatabaseChange", "Change detected in ${examSnap.key} with category $childCategory")
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("listenForDatabaseChanges", "Database error: ${error.message}")
            }
        })
    }
}
