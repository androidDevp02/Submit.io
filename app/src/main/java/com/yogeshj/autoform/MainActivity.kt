//package com.yogeshj.autoform
//
//import android.Manifest
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Build
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import com.yogeshj.autoform.databinding.ActivityMainBinding
//
//const val CHANNEL_ID="channelId"
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding:ActivityMainBinding
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding=ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        createNotificationChannel()
//
//        val builder=NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
//        builder.setSmallIcon(R.drawable.logo)
//            .setContentTitle("Submit.io")
//            .setContentTitle("This is a test notification")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//        with(NotificationManagerCompat.from(this@MainActivity)){
//            if (ActivityCompat.checkSelfPermission(
//                    applicationContext,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return
//            }
//            notify(1,builder.build())
//        }
//
//    }
//
//    private fun createNotificationChannel() {
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            val channel=NotificationChannel(CHANNEL_ID,"Submit.io App",NotificationManager.IMPORTANCE_DEFAULT)
//            channel.description="Test notification of Submit.io"
//
//            val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//}