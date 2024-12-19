package com.yogeshj.autoform.admin.users.changeUserData

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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.admin.requests.UploadFormSignUpModel
import com.yogeshj.autoform.databinding.ActivityChangeUploadFormUserDataBinding

class ChangeUploadFormUserDataActivity : AppCompatActivity() {

    private lateinit var binding:ActivityChangeUploadFormUserDataBinding

    private lateinit var dbRef: DatabaseReference

    private lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChangeUploadFormUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()
        showLoading()

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        MobileAds.initialize(this@ChangeUploadFormUserDataActivity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        val loginMailIntent=intent.getStringExtra("email")

        //getting data into edittext
        dbRef= FirebaseDatabase.getInstance().getReference("UploadFormRegisteredUsers")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val currentUser = snap.getValue(UploadFormSignUpModel::class.java)!!
                        if(currentUser.loginMailId==loginMailIntent){

                            binding.etWebsiteLink.setText(currentUser.websitelink.toString())
                            binding.etInstituteName.setText(currentUser.instituteName.toString())
                            binding.etHeadName.setText(currentUser.headName.toString())
                            binding.etAddress.setText(currentUser.instituteAddress.toString())
                            binding.etPhone.setText(currentUser.instituteContact.toString())
                            binding.etInstituteMail.setText(currentUser.instituteMailId.toString())
                            binding.etAppUsageMail.setText(currentUser.loginMailId.toString())

                            break
                        }
                    }
                }
                else{
                    Toast.makeText(this@ChangeUploadFormUserDataActivity,"No records found",Toast.LENGTH_LONG).show()
                }
                hideLoading()
            }
            override fun onCancelled(error: DatabaseError) {
                hideLoading()
                Toast.makeText(this@ChangeUploadFormUserDataActivity,"Database error: ${error.message}",Toast.LENGTH_LONG).show()
            }
        })

        binding.btnSubmit.setOnClickListener {
            showLoading()
            if(binding.etWebsiteLink.text.toString().isEmpty() || binding.etInstituteName.text.toString().isEmpty() || binding.etHeadName.text.toString().isEmpty()
                || binding.etAddress.text.toString().isEmpty() || binding.etPhone.text.toString().isEmpty() || binding.etInstituteMail.text.toString().isEmpty()
                || binding.etAppUsageMail.text.toString().isEmpty())
            {
                hideLoading()
                Toast.makeText(this@ChangeUploadFormUserDataActivity,"All fields are mandatory! Please fill up all the fields.",
                    Toast.LENGTH_LONG).show()
            }
            else
            {
                val websiteLink=binding.etWebsiteLink.text.toString()
                val instituteName=binding.etInstituteName.text.toString()
                val headName=binding.etHeadName.text.toString()
                val address=binding.etAddress.text.toString()
                val phone=binding.etPhone.text.toString()
                val instituteMail=binding.etInstituteMail.text.toString()
                val loginMail=binding.etAppUsageMail.text.toString()

                dbRef= FirebaseDatabase.getInstance().getReference("UploadFormRegisteredUsers")
                dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (snap in snapshot.children) {
                                val currentUser = snap.getValue(UploadFormSignUpModel::class.java)!!
                                if(currentUser.loginMailId==loginMailIntent){

                                    val uniqueKey=snap.key!!

                                    val updateMap = mapOf(
                                        "websitelink" to websiteLink,
                                        "instituteName" to instituteName,
                                        "headName" to headName,
                                        "instituteAddress" to address,
                                        "instituteContact" to phone,
                                        "instituteMailId" to instituteMail,
                                        "loginMailId" to loginMail,
                                        "key" to uniqueKey
                                    )

                                    dbRef.child(uniqueKey).updateChildren(updateMap)
                                        .addOnSuccessListener {
                                            finish()
                                            Toast.makeText(this@ChangeUploadFormUserDataActivity,"Details saved successfully.",Toast.LENGTH_LONG).show()
                                        }.addOnFailureListener {
                                            Toast.makeText(this@ChangeUploadFormUserDataActivity, "Error saving details.", Toast.LENGTH_LONG).show()
                                        }
                                    break
                                }
                            }

                        }
                        else{
                            Toast.makeText(this@ChangeUploadFormUserDataActivity,"No records found",Toast.LENGTH_LONG).show()
                        }
                        hideLoading()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        hideLoading()
                        Toast.makeText(this@ChangeUploadFormUserDataActivity,"Database error: ${error.message}",Toast.LENGTH_LONG).show()
                    }
                })



            }
        }


    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@ChangeUploadFormUserDataActivity)
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