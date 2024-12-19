package com.yogeshj.autoform.authentication.uploadForm

import android.app.Dialog
import android.content.Intent
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.yogeshj.autoform.R
import com.yogeshj.autoform.admin.requests.UploadFormSignUpModel
import com.yogeshj.autoform.databinding.ActivityUploadFormSignUpDetailsBinding

class UploadFormSignUpDetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUploadFormSignUpDetailsBinding

    private lateinit var dbRef: DatabaseReference

    private lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUploadFormSignUpDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        MobileAds.initialize(this@UploadFormSignUpDetailsActivity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.btnSubmit.setOnClickListener {
            showLoading()
            if(binding.etWebsiteLink.text.toString().isEmpty() || binding.etInstituteName.text.toString().isEmpty() || binding.etHeadName.text.toString().isEmpty()
                || binding.etAddress.text.toString().isEmpty() || binding.etPhone.text.toString().isEmpty() || binding.etInstituteMail.text.toString().isEmpty()
                || binding.etAppUsageMail.text.toString().isEmpty())
            {
                hideLoading()
                Toast.makeText(this@UploadFormSignUpDetailsActivity,"All fields are mandatory! Please fill up all the fields.",Toast.LENGTH_LONG).show()
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
                dbRef = FirebaseDatabase.getInstance().getReference("UploadFormRegistrationUsers")
                val uniqueKey = dbRef.push().key
                if (uniqueKey != null) {
                dbRef.child(uniqueKey).setValue(UploadFormSignUpModel(websiteLink,instituteName,headName,address,phone,instituteMail,loginMail,uniqueKey))
                    .addOnSuccessListener {
                        hideLoading()
                        startActivity(Intent(this@UploadFormSignUpDetailsActivity,UploadFormLoginActivity::class.java))
                        finish()
                        Toast.makeText(this@UploadFormSignUpDetailsActivity,"Please wait for sometime while we verify your details. We will get back to you soon via the email address provided with the login details.",Toast.LENGTH_LONG).show()
                    }.addOnFailureListener {
                        hideLoading()
                        Toast.makeText(this@UploadFormSignUpDetailsActivity, "Error submitting registration.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    hideLoading()
                    Toast.makeText(this@UploadFormSignUpDetailsActivity, "Error generating registration ID.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@UploadFormSignUpDetailsActivity)
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