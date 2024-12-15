package com.yogeshj.autoform.user.userFormFragment.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.ArrayAdapter
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
import com.yogeshj.autoform.databinding.ActivityLanguageBinding

class LanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageBinding

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

    override fun onResume() {
        super.onResume()
        val proficiency=resources.getStringArray(R.array.proficiency)
        val arrayAdapter= ArrayAdapter(this@LanguageActivity,R.layout.dropdown_item,proficiency)
        binding.proficiency1.setAdapter(arrayAdapter)
        binding.proficiency2.setAdapter(arrayAdapter)
        binding.proficiency3.setAdapter(arrayAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        MobileAds.initialize(this@LanguageActivity)
        handler.post(loadAdRunnable)

        binding.navBar.apply { alpha = 0f; translationY = -30f }
        binding.linearLayout.apply { alpha = 0f; translationY = 20f }

        binding.navBar.animate().alpha(1f).translationY(0f).setDuration(1000).start()

        binding.linearLayout.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()


        showLoading()

        FirstScreenActivity.auth=FirebaseAuth.getInstance()

        currentUserUid=intent.getStringExtra("uid")
        if(currentUserUid==null)
        {
            currentUserUid=FirstScreenActivity.auth.currentUser!!.uid
        }

        val db = FirebaseDatabase.getInstance().getReference("UsersInfo")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val currentUser = snap.getValue(User::class.java)!!
                        if (currentUser.uid ==currentUserUid) {
                            val language1=snap.child("language1").getValue(String::class.java)
                            if (language1!=null) {
                                binding.language1.setText(language1)
                            }
                            val proficiency1=snap.child("proficiency1").getValue(String::class.java)
                            if (proficiency1!=null) {
                                binding.proficiency1.setText(proficiency1)
                            }
                            val language2=snap.child("language2").getValue(String::class.java)
                            if (language2!=null) {
                                binding.language2.setText(language2)
                            }
                            val proficiency2=snap.child("proficiency2").getValue(String::class.java)
                            if (proficiency2!=null) {
                                binding.proficiency2.setText(proficiency2)
                            }
                            val language3=snap.child("language3").getValue(String::class.java)
                            if (language3!=null) {
                                binding.language3.setText(language3)
                            }
                            val proficiency3=snap.child("proficiency3").getValue(String::class.java)
                            if (proficiency3!=null) {
                                binding.proficiency3.setText(proficiency3)
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
            if(binding.language1.text.toString().isNotEmpty() && binding.proficiency1.text.toString().isNotEmpty())
            {
                if(resources.getStringArray(R.array.proficiency).contains(binding.proficiency1.text.toString())){
                    addToDB("language1",binding.language1.text.toString())
                    addToDB("proficiency1",binding.proficiency1.text.toString())
                }
                else
                {
                    hideLoading()
                    Toast.makeText(this@LanguageActivity,"Please select a valid proficiency for ${binding.language1.text.toString()}",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }
            if(binding.language2.text.toString().isNotEmpty() && binding.proficiency2.text.toString().isNotEmpty())
            {
                if(resources.getStringArray(R.array.proficiency).contains(binding.proficiency2.text.toString())){
                    addToDB("language2",binding.language2.text.toString())
                    addToDB("proficiency2",binding.proficiency2.text.toString())
                }
                else
                {
                    hideLoading()
                    Toast.makeText(this@LanguageActivity,"Please select a valid proficiency for ${binding.language2.text.toString()}",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

            }
            if(binding.language3.text.toString().isNotEmpty() && binding.proficiency3.text.toString().isNotEmpty())
            {
                if(resources.getStringArray(R.array.proficiency).contains(binding.proficiency3.text.toString())){
                    addToDB("language3",binding.language3.text.toString())
                    addToDB("proficiency3",binding.proficiency3.text.toString())
                }
                else
                {
                    hideLoading()
                    Toast.makeText(this@LanguageActivity,"Please select a valid proficiency for ${binding.language3.text.toString()}",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

            }

            hideLoading()
            Toast.makeText(this@LanguageActivity,"Details Saved Successfully", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    private fun addToDB(key: String, value: String) {
        val db = FirebaseDatabase.getInstance().getReference("UsersInfo")
        val currentUserRef = db.child(currentUserUid!!)
        val updates = HashMap<String, Any>()
        updates[key]=value
        currentUserRef.updateChildren(updates).addOnSuccessListener {
//                    Toast.makeText(this@ContactInformation,"Details Saved Successfully",Toast.LENGTH_LONG).show()
        }
//                    .addOnFailureListener {
//                    Toast.makeText(this@ContactInformation,"There was an error saving the details. Please try again later.",Toast.LENGTH_LONG).show()
//                }
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@LanguageActivity)
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