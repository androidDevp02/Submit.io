package com.yogeshj.autoform.user.userFormFragment.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.databinding.ActivityUpdateProfileBinding

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding


    //additional details
    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: ProfileOtherDetailsAdapter
    private val firebaseData = mutableMapOf<String, String>()


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        MobileAds.initialize(this@UpdateProfileActivity)
        handler.post(loadAdRunnable)

        binding.navBar.apply { alpha = 0f; translationY = -30f }
        binding.profileCard.apply { alpha = 0f; translationY = -30f }
        binding.contactInfoCard.apply { alpha = 0f; translationY = -30f }
        binding.educationCard.apply { alpha = 0f; translationY = -30f }
        binding.languagesCard.apply { alpha = 0f; translationY = -30f }
        binding.additionalDetailsCard.apply { alpha = 0f; translationY = -30f }

        binding.navBar.animate().alpha(1f).translationY(0f).setDuration(800).start()
        binding.profileCard.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(200).start()
        binding.contactInfoCard.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(300).start()
        binding.educationCard.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(400).start()
        binding.languagesCard.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(500).start()
        binding.additionalDetailsCard.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(600).start()

        showLoading()

        FirstScreenActivity.auth=FirebaseAuth.getInstance()

        val db = FirebaseDatabase.getInstance().getReference("UsersInfo")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (snap in snapshot.children) {
                        val currentUser = snap.getValue(User::class.java)!!
                        val profilePicUrl = snap.child("profilePic").value?.toString()
                        if (currentUser.uid==FirstScreenActivity.auth.currentUser!!.uid) {
                            Glide.with(this@UpdateProfileActivity).load(R.mipmap.ic_launcher).apply(RequestOptions.circleCropTransform()).into(binding.profilePic)
                            binding.name.text=currentUser.name
                            binding.dobGender.text="${currentUser.dob} ${currentUser.gender}"
                            binding.phone.text=currentUser.phone
                            binding.email.text=currentUser.email
                            if(profilePicUrl!=null)
                            {
                                Glide.with(this@UpdateProfileActivity)
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.user_icon)
                                    .error(R.drawable.user_icon)
                                    .apply(RequestOptions.circleCropTransform()).into(binding.profilePic)
                            }
                            hideLoading()
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


        binding.editProfile.setOnClickListener {
            startActivity(Intent(this@UpdateProfileActivity, ProfileInfoActivity::class.java))
        }

        binding.contactInfoCard.setOnClickListener {
            startActivity(Intent(this@UpdateProfileActivity, ContactInformation::class.java))
        }

        binding.educationCard.setOnClickListener {
            startActivity(Intent(this@UpdateProfileActivity, EducationActivity::class.java))
        }

//        binding.awardsCard.setOnClickListener {
//            startActivity(Intent(this@UpdateProfileActivity,AwardsAndAchievement::class.java))
//        }

        binding.languagesCard.setOnClickListener {
            startActivity(Intent(this@UpdateProfileActivity, LanguageActivity::class.java))
        }


        dbRef=FirebaseDatabase.getInstance().getReference("UsersInfo")
        binding.recyclerViewAdditionalDetails.layoutManager=LinearLayoutManager(this@UpdateProfileActivity)
        adapter= ProfileOtherDetailsAdapter(firebaseData)
        binding.recyclerViewAdditionalDetails.adapter=adapter
        fetchDataFromFirebase()

        binding.back.setOnClickListener {
            showLoading()
            val updatedData = adapter.getUpdatedData()
            val changesToUpdate = mutableMapOf<String, String>()

            for ((key, newValue) in updatedData) {
                val originalValue = firebaseData[key]
                if (newValue != originalValue) {
                    changesToUpdate[key] = newValue
                }
            }

            val userRef = dbRef.child(FirstScreenActivity.auth.currentUser!!.uid)
            for ((key, value) in changesToUpdate) {
                userRef.child(key).setValue(value).addOnCompleteListener {
                    hideLoading()
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to save changes", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            hideLoading()
            finish()
        }
    }
    private fun fetchDataFromFirebase() {
        showLoading()
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                firebaseData.clear()

                for (snap in snapshot.children) {
                    val uid = snap.child("uid").getValue(String::class.java)
                    if(uid==FirstScreenActivity.auth.currentUser!!.uid)
                    {

                        for (formSnapshot in snap.children) {
                            val key = formSnapshot.key
                            val value = formSnapshot.getValue(String::class.java)

                            if (key != null && value != null) {
                                if(key!="profilePic" && key!="uid" && key!="address" && key!="phone" && key!="email" &&
                                    key!="education_level" && key!="course" && key!="school" && key!="from" && key!="to"
                                    && key!="cgpa" && key!="language1" && key!="language2" && key!="language3" && key!="proficiency1"
                                    && key!="proficiency2" && key!="proficiency3" && key!="name" && key!="dob" && key!="gender" && key!="state"
                                    && key!="field1" && key!="field2" && key!="field3") {
                                    firebaseData[key]=value
                                }
                            }
                        }
                        adapter.notifyDataSetChanged()
                        hideLoading()
                        break
                    }
                }
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
                Log.e("Firebase", "Failed to read data", error.toException())
            }
        })
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@UpdateProfileActivity)
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