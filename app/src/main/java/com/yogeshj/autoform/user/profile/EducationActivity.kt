package com.yogeshj.autoform.user.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

        binding.navBar.apply { alpha = 0f; translationY = -30f }
        binding.linearLayout.apply { alpha = 0f; translationY = 20f }

        binding.navBar.animate().alpha(1f).translationY(0f).setDuration(1000).start()

        binding.linearLayout.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()

        showLoading()
        FirstScreenActivity.auth=FirebaseAuth.getInstance()

        val db = FirebaseDatabase.getInstance().getReference("UsersInfo")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val currentUser = snap.getValue(User::class.java)!!
                        if (currentUser.uid == FirstScreenActivity.auth.currentUser!!.uid) {
                            val edu_lvl=snap.child("education_level").getValue(String::class.java)
                            if (edu_lvl!=null) {
                                binding.educationalLvl.setText(edu_lvl)
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

            //complete it
        binding.back.setOnClickListener {
            showLoading()
            if(binding.educationalLvl.text.toString().isNotEmpty())
            {
                addToDB("education_level",binding.educationalLvl.text.toString())
            }
            if(binding.course.text.toString().isNotEmpty())
            {
                addToDB("course",binding.course.text.toString())
            }
            if(binding.college.text.toString().isNotEmpty())
            {
                addToDB("school",binding.college.text.toString())
            }
            if(binding.from.text.toString().isNotEmpty())
            {
                addToDB("from",binding.from.text.toString())
            }
            if(binding.to.text.toString().isNotEmpty())
            {
                addToDB("to",binding.to.text.toString())
            }
            if(binding.percentageCgpa.text.toString().isNotEmpty())
            {
                addToDB("cgpa",binding.percentageCgpa.text.toString())
            }
            hideLoading()
            Toast.makeText(this@EducationActivity,"Details Saved Successfully", Toast.LENGTH_LONG).show()
            finish()
        }

    }

    private fun addToDB(key: String, value: String) {
        val db = FirebaseDatabase.getInstance().getReference("UsersInfo")
        val currentUserRef = db.child(FirstScreenActivity.auth.currentUser!!.uid)
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