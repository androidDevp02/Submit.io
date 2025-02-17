package com.yogeshj.autoform

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
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

//Admin@123
//5267 3181 8797 5449

class FirstScreenActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFirstScreenBinding
    private lateinit var dialog:Dialog

    companion object{
        lateinit var auth: FirebaseAuth
    }

//    private val handler = Handler(Looper.getMainLooper())
//    private val adInterval = 31_000L
//    private val loadAdRunnable = object : Runnable {
//        override fun run() {
//            val adRequest = AdRequest.Builder().build()
//            binding.adView.loadAd(adRequest)
//            handler.postDelayed(this, adInterval)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFirstScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()
        showLoading()

        auth=FirebaseAuth.getInstance()

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
//        handler.post(loadAdRunnable)


        binding.title.apply { alpha = 0f; translationY = -30f }
        binding.loginAsUserCard.apply { alpha = 0f; translationX = -30f }
        binding.loginAsUploadformCard.apply { alpha = 0f; translationX = -30f }
        binding.title.animate().alpha(1f).translationY(0f).setDuration(1000).start()
        binding.loginAsUserCard.animate().alpha(1f).translationX(0f).setDuration(1000).setStartDelay(300).start()
//        binding.loginAsAdmin.animate().alpha(1f).translationX(0f).setDuration(700).setStartDelay(500).start()
        binding.loginAsUploadformCard.animate().alpha(1f).translationX(0f).setDuration(1000).setStartDelay(600).start()

        contactButtonListener()

        val currentUser = auth.currentUser
        if(currentUser!=null)
        {
            showLoading()
            val db = FirebaseDatabase.getInstance().getReference("Users")
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val curr = snap.getValue(User::class.java)!!
                            if (curr.uid == auth.currentUser!!.uid) {
                                hideLoading()
                                if(curr.email=="yogesh.jaiswal21b@iiitg.ac.in"){
                                    startActivity(Intent(this@FirstScreenActivity,AdminMainActivity::class.java))
                                    finish()
                                }
                                else{
                                    startActivity(Intent(this@FirstScreenActivity,UserMainActivity::class.java))
                                    finish()
                                }
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

            val db2 = FirebaseDatabase.getInstance().getReference("UploadFormUsers")
            db2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val curr = snap.getValue(User::class.java)!!
                            if (curr.uid == auth.currentUser!!.uid) {
                                hideLoading()
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
            hideLoading()
            startActivity(Intent(this@FirstScreenActivity,UserLoginActivity::class.java))
            finish()
        }

        binding.loginAsUploadformCard.setOnClickListener {
            hideLoading()
            startActivity(Intent(this@FirstScreenActivity,UploadFormLoginActivity::class.java))
            finish()
        }

    }

    private fun contactButtonListener() {
        binding.contactEmail.setOnClickListener {
            val email=binding.contactEmail.text.toString()
            val emailIntent=Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto",email,null));
            startActivity(Intent.createChooser(emailIntent,"Send email..."))
        }

        binding.contactPhone.setOnClickListener {
            val number=binding.contactPhone.text.toString().trim()
            val intent=Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(number)));
            startActivity(intent);
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
        binding.root.alpha = 0.5f
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun hideLoading() {
        if (dialog.isShowing) {
            dialog.dismiss()
            binding.root.alpha = 1f
        }
    }
}