package com.yogeshj.autoform.user

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.yogeshj.autoform.databinding.ActivityFieldSelectBinding
import com.yogeshj.autoform.user.userFormFragment.profile.ProfileInfoActivity

class FieldSelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFieldSelectBinding
    private var numSelected = 0
    private val selectedFields = mutableListOf<String>()
    private lateinit var dbRef: DatabaseReference

    private lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFieldSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        FirstScreenActivity.auth = FirebaseAuth.getInstance()

        MobileAds.initialize(this@FieldSelectActivity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

//        preSelectField()

        binding.examName.apply { alpha = 0f; translationY = -30f }
        binding.examHost.apply { alpha = 0f; translationY = -20f }
        binding.checkboxCard.apply { alpha = 0f; translationY = 30f }
        binding.continueBtn.apply { alpha = 0f; translationY = 50f }

        binding.examName.animate().alpha(1f).translationY(0f).setDuration(800).start()
        binding.examHost.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(200).start()
        binding.checkboxCard.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(400).start()
        binding.continueBtn.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(600).start()



        for (i in 0 until binding.linearLayout.childCount) {
            val view = binding.linearLayout.getChildAt(i)
            if (view is CheckBox)
                isCheck(view)
        }

        binding.continueBtn.setOnClickListener {
            if(selectedFields.size==0){
                Toast.makeText(this@FieldSelectActivity,"Please select at least 1 field",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            showLoading()

            val db = FirebaseDatabase.getInstance().getReference("Users")
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val currentUser = snap.getValue(User::class.java)!!
                            if (currentUser.uid == FirstScreenActivity.auth.currentUser!!.uid) {

                                dbRef = FirebaseDatabase.getInstance().getReference("UsersInfo")

                                if(selectedFields.size == 0) {
//                                    dbRef.child(currentUser.uid!!).child("name").setValue(currentUser.name)
//                                    dbRef.child(currentUser.uid!!).child("email").setValue(currentUser.email)
//                                    dbRef.child(currentUser.uid!!)
//                                        .child("field1").setValue(null)
//                                    dbRef.child(currentUser.uid!!)
//                                        .child("field2").setValue(null)
//                                    dbRef.child(currentUser.uid!!)
//                                        .child("field3").setValue(null)
                                } else if (selectedFields.size == 1) {
                                    dbRef.child(currentUser.uid!!).child("name").setValue(currentUser.name)
                                    dbRef.child(currentUser.uid!!).child("email").setValue(currentUser.email)
                                    dbRef.child(currentUser.uid!!)
                                        .child("field1").setValue(selectedFields[0])
                                    dbRef.child(currentUser.uid!!)
                                        .child("field2").setValue(null)
                                    dbRef.child(currentUser.uid!!)
                                        .child("field3").setValue(null)
                                } else if (selectedFields.size == 2) {
                                    dbRef.child(currentUser.uid!!).child("name").setValue(currentUser.name)
                                    dbRef.child(currentUser.uid!!).child("email").setValue(currentUser.email)
                                    dbRef.child(currentUser.uid!!)
                                        .child("field1").setValue(selectedFields[0])
                                    dbRef.child(currentUser.uid!!)
                                        .child("field2").setValue(selectedFields[1])
                                    dbRef.child(currentUser.uid!!)
                                        .child("field3").setValue(null)
                                } else if (selectedFields.size == 3) {
                                    dbRef.child(currentUser.uid!!).child("name").setValue(currentUser.name)
                                    dbRef.child(currentUser.uid!!).child("email").setValue(currentUser.email)
                                    dbRef.child(currentUser.uid!!)
                                        .child("field1").setValue(selectedFields[0])
                                    dbRef.child(currentUser.uid!!)
                                        .child("field2").setValue(selectedFields[1])
                                    dbRef.child(currentUser.uid!!)
                                        .child("field3").setValue(selectedFields[2])
                                }
                                hideLoading()
                                startActivity(Intent(this@FieldSelectActivity, ProfileInfoActivity::class.java))
                                finish()
                                break
                            }
                        }
                    }
                    hideLoading()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@FieldSelectActivity,"Database error: ${error.message}",Toast.LENGTH_LONG).show()
                    hideLoading()
                }
            })
        }
    }

    private fun isCheck(id: CheckBox) {
        id.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (numSelected < 3) {
                    numSelected++
                    selectedFields.add(id.text.toString())
                } else {
                    id.isChecked = false
                    Toast.makeText(this@FieldSelectActivity,"You can only select 3 fields", Toast.LENGTH_LONG).show()
                }
            }
            else {
                numSelected--
                selectedFields.remove(id.text.toString())
            }
        }
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@FieldSelectActivity)
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
