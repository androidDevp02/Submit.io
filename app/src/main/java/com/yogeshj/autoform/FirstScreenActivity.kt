package com.yogeshj.autoform

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.authentication.uploadForm.UploadFormLoginActivity
import com.yogeshj.autoform.authentication.user.UserLoginActivity
import com.yogeshj.autoform.databinding.ActivityFirstScreenBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.yogeshj.autoform.admin.AdminMainActivity
import com.yogeshj.autoform.uploadForm.UploadFormMainActivity
import com.yogeshj.autoform.user.UserMainActivity
import com.yogeshj.autoform.user.notifications.Notification
import com.yogeshj.autoform.user.notifications.NotificationService

//Admin@123
//5267 3181 8797 5449


class FirstScreenActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFirstScreenBinding
    private lateinit var dialog:Dialog

    companion object{
        lateinit var auth: FirebaseAuth
    }

    private val handler = Handler(Looper.getMainLooper())
    private val adInterval = 31_000L
    private val loadAdRunnable = object : Runnable {
        override fun run() {
            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
            handler.postDelayed(this, adInterval)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFirstScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val serviceIntent = Intent(this, NotificationService::class.java)
        startService(serviceIntent)

//        val notification=Notification(this)
//        notification.showNotification("New form","New form is uploaded")
//        Toast.makeText(this,"firstActivity",Toast.LENGTH_LONG).show()
        MobileAds.initialize(this) {}
        handler.post(loadAdRunnable)

        binding.title.apply { alpha = 0f; translationY = -30f }
        binding.loginAsUserCard.apply { alpha = 0f; translationX = -30f }
        binding.loginAsUploadformCard.apply { alpha = 0f; translationX = -30f }
        binding.title.animate().alpha(1f).translationY(0f).setDuration(1000).start()
        binding.loginAsUserCard.animate().alpha(1f).translationX(0f).setDuration(1000).setStartDelay(300).start()
//        binding.loginAsAdmin.animate().alpha(1f).translationX(0f).setDuration(700).setStartDelay(500).start()
        binding.loginAsUploadformCard.animate().alpha(1f).translationX(0f).setDuration(1000).setStartDelay(600).start()


        initLoadingDialog()

        showLoading()

        auth=FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if(currentUser!=null)
        {
            //go to users/admin page if already logged in
            val db = FirebaseDatabase.getInstance().getReference("Users")
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val curr = snap.getValue(User::class.java)!!
                            if (curr.uid == auth.currentUser!!.uid) {
                                if(curr.email=="yogesh.jaiswal21b@iiitg.ac.in"){
                                    startActivity(Intent(this@FirstScreenActivity,
                                        AdminMainActivity::class.java))
                                }
                                else
                                    startActivity(Intent(this@FirstScreenActivity,UserMainActivity::class.java))
                                finish()
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

            //go to upload form page if already logged in
//            Log.d("EMAIL C", auth.currentUser!!.email!!+" "+ auth.currentUser!!.uid)
            val db2 = FirebaseDatabase.getInstance().getReference("UploadFormUsers")
            db2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val curr = snap.getValue(User::class.java)!!
//                            Log.d("EMAILS",curr.email!!)
                            if (curr.uid == auth.currentUser!!.uid) {
//                                Toast.makeText(this@FirstScreenActivity,"Upload From user is logged in",Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@FirstScreenActivity,UploadFormMainActivity::class.java))
                                finish()
                                break
                            }
                        }
                    }
                    hideLoading()
                }

                override fun onCancelled(error: DatabaseError) {
                    hideLoading()
                }
            })



        }
        else
            hideLoading()


        binding.loginAsUserCard.setOnClickListener {
            startActivity(Intent(this@FirstScreenActivity,UserLoginActivity::class.java))
            finish()
        }

        binding.loginAsUploadformCard.setOnClickListener {
            startActivity(Intent(this@FirstScreenActivity,UploadFormLoginActivity::class.java))
            finish()
        }

    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@FirstScreenActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showLoading() {
        binding.root.visibility = View.INVISIBLE
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun hideLoading() {
        if (dialog.isShowing) {
            dialog.dismiss()
            binding.root.visibility = View.VISIBLE
        }
    }
}