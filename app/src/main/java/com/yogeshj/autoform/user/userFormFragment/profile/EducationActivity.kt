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
import com.yogeshj.autoform.databinding.ActivityEducationBinding

class EducationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEducationBinding

    private lateinit var dialog:Dialog

    private var currentUserUid:String?=null

    override fun onResume() {
        super.onResume()
        val edu=resources.getStringArray(R.array.education_level)
        val arrayAdapter= ArrayAdapter(this@EducationActivity,R.layout.dropdown_item,edu)
        binding.educationalLvl.setAdapter(arrayAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEducationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        showLoading()

        FirstScreenActivity.auth=FirebaseAuth.getInstance()
        MobileAds.initialize(this@EducationActivity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.navBar.apply { alpha = 0f; translationY = -30f }
        binding.linearLayout.apply { alpha = 0f; translationY = 20f }

        binding.navBar.animate().alpha(1f).translationY(0f).setDuration(1000).start()

        binding.linearLayout.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()


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
                            val eduLvl=snap.child("education_level").getValue(String::class.java)
                            if (eduLvl!=null) {
                                binding.educationalLvl.setText(eduLvl)
                            }

                            val course=snap.child("course").getValue(String::class.java)
                            if (course!=null) {
                                binding.course.setText(course)
                            }
                            val school=snap.child("school").getValue(String::class.java)
                            if (school!=null) {
                                binding.college.setText(school)
                            }
                            val from=snap.child("from").getValue(String::class.java)
                            if (from!=null) {
                                binding.from.setText(from)
                            }
                            val to=snap.child("to").getValue(String::class.java)
                            if (to!=null) {
                                binding.to.setText(to)
                            }
                            val cgpa=snap.child("cgpa").getValue(String::class.java)
                            if (cgpa!=null) {
                                binding.percentageCgpa.setText(cgpa)
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
            if(binding.educationalLvl.text.toString().isNotEmpty() )
            {
                if(resources.getStringArray(R.array.education_level).contains(binding.educationalLvl.text.toString()))
                    addToDB("education_level",binding.educationalLvl.text.toString())
                else
                {
                    hideLoading()
                    Toast.makeText(this@EducationActivity,"Please select a valid Educational Level",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if(binding.course.text.toString().isNotEmpty())
                {
                    addToDB("course",binding.course.text.toString())
                }
                else{
                    hideLoading()
                    Toast.makeText(this@EducationActivity,"Course cannot be empty",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if(binding.college.text.toString().isNotEmpty())
                {
                    addToDB("school",binding.college.text.toString())
                }
                else{
                    hideLoading()
                    Toast.makeText(this@EducationActivity,"College name cannot be empty",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if(binding.from.text.toString().isNotEmpty())
                {
                    addToDB("from",binding.from.text.toString())
                }
                else{
                    hideLoading()
                    Toast.makeText(this@EducationActivity,"Please Enter the start year.",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if(binding.to.text.toString().isNotEmpty())
                {
                    addToDB("to",binding.to.text.toString())
                }
                else{
                    hideLoading()
                    Toast.makeText(this@EducationActivity,"Please enter the end year.",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if(binding.percentageCgpa.text.toString().isNotEmpty())
                {
                    addToDB("cgpa",binding.percentageCgpa.text.toString())
                }
                else{
                    hideLoading()
                    Toast.makeText(this@EducationActivity,"Please enter your percentage/CGPA.",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            hideLoading()
            Toast.makeText(this@EducationActivity,"Details Saved Successfully", Toast.LENGTH_LONG).show()
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
        dialog = Dialog(this@EducationActivity)
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