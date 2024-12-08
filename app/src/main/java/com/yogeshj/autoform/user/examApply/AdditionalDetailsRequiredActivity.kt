package com.yogeshj.autoform.user.examApply

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivityAdditionalDetailsRequiredBinding

class AdditionalDetailsRequiredActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAdditionalDetailsRequiredBinding
    private val editTextMap = HashMap<String, EditText>()

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
        binding=ActivityAdditionalDetailsRequiredBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()
        showLoading()

        FirstScreenActivity.auth = FirebaseAuth.getInstance()

        MobileAds.initialize(this@AdditionalDetailsRequiredActivity)
        handler.post(loadAdRunnable)

        val unfilledData = intent.extras

        if (unfilledData != null) {
            for (key in unfilledData.keySet()) {
                // Create a new TextView for each key
                val textView = TextView(this)
                textView.text = key.capitalize()
                textView.textSize = 18f
                textView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                val editText = EditText(this)
                editText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                editText.hint = "Enter your ${key.capitalize()}"
                editText.textSize = 16f

                binding.detailsContainer.addView(textView)
                binding.detailsContainer.addView(editText)

                editTextMap[key] = editText
            }
        }
        hideLoading()

        binding.saveBtn.setOnClickListener {
            saveAdditionalDetailsToFirebase()
        }
    }

    private fun saveAdditionalDetailsToFirebase() {
        showLoading()
        val userId = FirstScreenActivity.auth.currentUser!!.uid
        val dbRef = FirebaseDatabase.getInstance().getReference("UsersInfo").child(userId)

        val additionalData = HashMap<String, String>()
        for ((key, editText) in editTextMap) {
            val value = editText.text.toString()
            if (value.isNotEmpty()) {
                additionalData[key] = value
            }
        }

        if (additionalData.isNotEmpty()) {
            dbRef.updateChildren(additionalData as Map<String, Any>)
                .addOnSuccessListener {
                    hideLoading()
                    Toast.makeText(this, "Details saved successfully!", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener {
                    hideLoading()
                    Toast.makeText(this, "Failed to save details. Try again.", Toast.LENGTH_SHORT).show()
                }
        } else {
            hideLoading()
            Toast.makeText(this, "Please fill in all the required details!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun initLoadingDialog() {
        dialog = Dialog(this@AdditionalDetailsRequiredActivity)
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