package com.yogeshj.autoform.authentication.uploadForm

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.appExit.ExitDialog
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.databinding.ActivityUploadFormLoginBinding
import com.yogeshj.autoform.uploadForm.UploadFormMainActivity

class UploadFormLoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUploadFormLoginBinding

    private lateinit var dialog:Dialog

    override fun onBackPressed() {

        ExitDialog.exit(this@UploadFormLoginActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUploadFormLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        Glide.with(this@UploadFormLoginActivity)
            .load(R.mipmap.ic_login_foreground)
            .apply(RequestOptions.circleCropTransform()).into(binding.logo)

        MobileAds.initialize(this@UploadFormLoginActivity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.logo.apply { alpha = 0f; translationY = -50f }
        binding.welcome.apply { alpha = 0f; translationY = -20f }
        binding.loginText.apply { alpha = 0f; translationY = -20f }
        binding.loginCard.apply { alpha = 0f; translationY = -20f }
        binding.btnLogin.apply { alpha = 0f; translationY = 20f }
        binding.btnSignup.apply { alpha = 0f; translationY = 20f }

        binding.logo.animate().alpha(1f).translationY(0f).setDuration(1000).start()
        binding.welcome.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()
        binding.loginText.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(400).start()
        binding.loginCard.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(600).start()
        binding.btnLogin.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(800).start()
        binding.btnSignup.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(1000).start()

        FirstScreenActivity.auth.signOut()

        binding.forgotPasswordText.setOnClickListener {
            showLoading()
            val builder = AlertDialog.Builder(this@UploadFormLoginActivity)
            builder.setTitle("Enter your email to reset the password")

            val input = EditText(this@UploadFormLoginActivity)
            input.inputType= InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            builder.setView(input)
            hideLoading()

            builder.setPositiveButton("Send Email") { _, _ ->
                showLoading()
                val email = input.text.toString()
                if(email.isNotEmpty()){
                    FirstScreenActivity.auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            hideLoading()
                            Toast.makeText(this@UploadFormLoginActivity,"Please check your email to reset the password.",Toast.LENGTH_LONG).show()
                            binding.forgotPasswordText.visibility = View.GONE
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.forgotPasswordText.visibility = View.VISIBLE
                            }, 60000)
                        }
                        .addOnFailureListener {
                            hideLoading()
                            Toast.makeText(this@UploadFormLoginActivity,"Failed to send email. Please try again later.",Toast.LENGTH_LONG).show()
                        }

                }
                else
                {
                    hideLoading()
                    Toast.makeText(this@UploadFormLoginActivity,"Email cannot be empty",Toast.LENGTH_LONG).show()
                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }


        binding.btnSignup.setOnClickListener {
            hideLoading()
            startActivity(Intent(this@UploadFormLoginActivity, UploadFormSignUpDetailsActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            showLoading()
            val email = binding.emailLogin.text.toString()
            val password = binding.passwordLogin.text.toString()


            if (email.isEmpty() || password.isEmpty()) {
                hideLoading()
                Snackbar.make(binding.emailLogin,"Email or password cannot be empty.",Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                hideLoading()
                Toast.makeText(this@UploadFormLoginActivity,"Enter a valid email address",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val db = FirebaseDatabase.getInstance().getReference("UploadFormUsers")
            db.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var flag = false
                    if (snapshot.exists()) {
                        for (snap in snapshot.children) {
                            val curr = snap.getValue(User::class.java)!!
                            if (curr.email.toString() == email) {
                                flag = true
                                break
                            }
                        }
                    }

                    if (!flag) {
                        hideLoading()
                        Snackbar.make(binding.emailLogin,"Incorrect email. Please enter a different email or register as a new user.",
                            Snackbar.LENGTH_LONG).show()
                    }
                    else
                    {
                        if (email.isNotEmpty() && password.isNotEmpty()) {
                            FirstScreenActivity.auth.signOut()
                            FirstScreenActivity.auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        val verification=FirstScreenActivity.auth.currentUser?.isEmailVerified
                                        if(verification==true){
                                            hideLoading()
                                            startActivity(Intent(this@UploadFormLoginActivity,UploadFormMainActivity::class.java))
                                            finish()
                                        }
                                        else
                                        {
                                            hideLoading()
                                            Toast.makeText(this@UploadFormLoginActivity,"Please verify your email first!",Toast.LENGTH_LONG).show()
                                        }

                                    }
                                }.addOnFailureListener {
                                    hideLoading()
                                    Toast.makeText(this@UploadFormLoginActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    hideLoading()
                }
            })
        }
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@UploadFormLoginActivity)
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