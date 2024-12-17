package com.yogeshj.autoform.authentication.uploadForm

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.databinding.ActivityUploadFormSignUpBinding

class UploadFormSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadFormSignUpBinding

    private lateinit var dbRef: DatabaseReference

    private lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUploadFormSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        binding.logo.apply { alpha = 0f; translationY = -50f }
        binding.welcome.apply { alpha = 0f; translationY = -20f }
        binding.signupText.apply { alpha = 0f; translationY = -20f }
        binding.signupCard.apply { alpha = 0f; translationY = 20f }
        binding.btnSignUp.apply { alpha = 0f; translationY = 20f }
        binding.btnLogin.apply { alpha = 0f; translationY = 20f }

        startFadeInAndSlideUpAnimation(binding.logo, 300)
        startFadeInAndSlideUpAnimation(binding.welcome, 500)
        startFadeInAndSlideUpAnimation(binding.signupText, 700)
        startFadeInAndSlideUpAnimation(binding.signupCard, 900)
        startFadeInAndSlideUpAnimation(binding.btnSignUp, 1100)
        startFadeInAndSlideUpAnimation(binding.btnLogin, 1300)

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this@UploadFormSignUpActivity, UploadFormLoginActivity::class.java))
            finish()
        }

        binding.btnSignUp.setOnClickListener {
            showLoading()
            val name = binding.nameSignUp.text.toString()
            val email = binding.emailSignUp.text.toString()
            val password = binding.passwordSignUp.text.toString()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                hideLoading()
                Snackbar.make(binding.root,"Name, Email or password cannot be empty.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            FirstScreenActivity.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task->
                    if (task.isSuccessful) {
                        addUserToDatabase(name, email, FirstScreenActivity.auth.currentUser!!.uid)
                        FirstScreenActivity.auth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Toast.makeText(this@UploadFormSignUpActivity,"Please verify your email address before logging in",Toast.LENGTH_LONG).show()

                                val intent = Intent(this,UploadFormLoginActivity::class.java)
                                if (baseContext != this@UploadFormSignUpActivity) {
                                    finish()
                                }
                                startActivity(intent)
                            }
                            ?.addOnFailureListener {
                                Toast.makeText(this@UploadFormSignUpActivity,it.toString(), Toast.LENGTH_LONG).show()
                            }



                    }
                }.addOnFailureListener {err->
                    hideLoading()
                    Snackbar.make(binding.root,"Error: ${err.message}", Snackbar.LENGTH_LONG).show()
                }
        }

    }

    private fun startFadeInAndSlideUpAnimation(view: View, delay: Long) {
        view.alpha = 0f
        view.translationY = 50f


        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delay)
            .setDuration(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {
        dbRef = FirebaseDatabase.getInstance().getReference("UploadFormUsers")
        dbRef.child(uid).setValue(User(uid, name, email))
        hideLoading()
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@UploadFormSignUpActivity)
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