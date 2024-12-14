package com.yogeshj.autoform.user.userFormFragment.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.databinding.ActivityContactInformationBinding

class ContactInformation : AppCompatActivity() {
    private lateinit var binding: ActivityContactInformationBinding

    private lateinit var dialog:Dialog

    private val handler = Handler(Looper.getMainLooper())
    private val adInterval = 31_000L
    private val loadAdRunnable = object : Runnable {
        override fun run() {
            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
            handler.postDelayed(this, adInterval)
        }
    }

    private var currentUserUid:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityContactInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this@ContactInformation)
        handler.post(loadAdRunnable)

        binding.navBar.apply { alpha = 0f; translationY = -30f }
        binding.linearLayout.apply { alpha = 0f; translationY = 20f }

        binding.navBar.animate().alpha(1f).translationY(0f).setDuration(1000).start()

        binding.linearLayout.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()


        initLoadingDialog()
        showLoading()

        FirstScreenActivity.auth=FirebaseAuth.getInstance()

        currentUserUid=intent.getStringExtra("uid")
        if(currentUserUid==null)
        {
            currentUserUid=FirstScreenActivity.auth.currentUser!!.uid
        }

        //Add address data
        val db = FirebaseDatabase.getInstance().getReference("UsersInfo")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val currentUser = snap.getValue(User::class.java)!!
                        if (currentUser.uid ==currentUserUid) {
                            binding.phone.setText(currentUser.phone)
                            binding.email.setText(currentUser.email)
                            val address = snap.child("address").getValue(String::class.java)
                            if (address != null) {
                                binding.address.setText(address)
                            }
                        }
                    }
                }
                hideLoading()
            }
            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })

        binding.back.setOnClickListener {
            showLoading()

            val currentUserRef = db.child(currentUserUid!!)
            val updates = HashMap<String, Any>()

            if (binding.address.text.toString().isNotEmpty()) {
                updates["address"] = binding.address.text.toString()
            }

            if (binding.phone.text.toString().isNotEmpty()) {
                updates["phone"] = binding.phone.text.toString()
            }

            if (binding.email.text.toString().isNotEmpty()) {
                updates["email"] = binding.email.text.toString()
            }

            currentUserRef.updateChildren(updates).addOnSuccessListener {
                hideLoading()
                Toast.makeText(this@ContactInformation, "Details Saved Successfully", Toast.LENGTH_LONG).show()
                finish()
            }.addOnFailureListener {
                hideLoading()
                Toast.makeText(this@ContactInformation, "There was an error saving the details. Please try again later.", Toast.LENGTH_LONG).show()
            }
        }




    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@ContactInformation)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showLoading() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun hideLoading() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

}