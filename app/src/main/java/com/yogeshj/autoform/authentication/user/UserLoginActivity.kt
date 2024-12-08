package com.yogeshj.autoform.authentication.user

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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.user.FieldSelectActivity
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.authentication.admin.AdminFirstScreenActivity
import com.yogeshj.autoform.databinding.ActivityUserLoginBinding

class UserLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginBinding

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
        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        MobileAds.initialize(this@UserLoginActivity)
        handler.post(loadAdRunnable)

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
        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this@UserLoginActivity, UserSignUpActivity::class.java))
            finish()
        }

        binding.forgotPasswordText.setOnClickListener {
            val builder = AlertDialog.Builder(this@UserLoginActivity)
            builder.setTitle("Enter your email to reset the password")

            val input = EditText(this@UserLoginActivity)
            input.inputType=InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            builder.setView(input)

            builder.setPositiveButton("Send Email") { _, _ ->
                val email = input.text.toString()
                if(email.isNotEmpty()){
                    showLoading()
                    FirstScreenActivity.auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            hideLoading()
                            Toast.makeText(this@UserLoginActivity,"Please check your email to reset the password.",Toast.LENGTH_LONG).show()
                            binding.forgotPasswordText.visibility = View.GONE
                            Handler(Looper.getMainLooper()).postDelayed({
                                binding.forgotPasswordText.visibility = View.VISIBLE
                            }, 60000)
                        }
                        .addOnFailureListener {
                            hideLoading()
                            Toast.makeText(this@UserLoginActivity,"Failed to send email. Please try again later.",Toast.LENGTH_LONG).show()
                        }

                }
                else
                {
                    Toast.makeText(this@UserLoginActivity,"Email cannot be empty",Toast.LENGTH_LONG).show()
                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
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
            if(email=="yogesh.jaiswal21b@iiitg.ac.in"){
                FirstScreenActivity.auth.signOut()
                FirstScreenActivity.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        hideLoading()
                        if (it.isSuccessful) {
                            val verification=FirstScreenActivity.auth.currentUser?.isEmailVerified
                            if(verification==true){
                                startActivity(Intent(this@UserLoginActivity,AdminFirstScreenActivity::class.java))
                                finish()
                            }
                            else
                            {
                                Toast.makeText(this@UserLoginActivity,"Please verify your email first!",Toast.LENGTH_LONG).show()
                            }
                        }
                    }.addOnFailureListener {
                        hideLoading()
                        Toast.makeText(this@UserLoginActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
            }else {

                showLoading()
                val db = FirebaseDatabase.getInstance().getReference("Users")
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
                            Snackbar.make(
                                binding.emailLogin,
                                "Incorrect email. Please enter a different email or register as a new user.",
                                Snackbar.LENGTH_LONG
                            ).show()
                        } else {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                FirstScreenActivity.auth.signOut()
                                FirstScreenActivity.auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            hideLoading()
                                            val verification =
                                                FirstScreenActivity.auth.currentUser?.isEmailVerified
                                            if (verification == true) {
                                                startActivity(
                                                    Intent(
                                                        this@UserLoginActivity,
                                                        FieldSelectActivity::class.java
                                                    )
                                                )
                                                finish()
                                            } else {
                                                Toast.makeText(
                                                    this@UserLoginActivity,
                                                    "Please verify your email first!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }.addOnFailureListener {
                                        hideLoading()
                                        Toast.makeText(
                                            this@UserLoginActivity,
                                            it.localizedMessage,
                                            Toast.LENGTH_LONG
                                        ).show()
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
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@UserLoginActivity)
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