package com.yogeshj.autoform.otpCode

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yogeshj.autoform.databinding.ActivityOtpVerifyBinding

class OtpVerifyActivity : AppCompatActivity() {

    private lateinit var binding:ActivityOtpVerifyBinding
    private var storedVerificationId:String?=""
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=Firebase.auth

        storedVerificationId=intent.getStringExtra("storedVerificationId")

        binding.verifyOTP.setOnClickListener {
            verifyPhoneNumberWithCode(storedVerificationId,binding.otp.text.toString())
        }

    }

    private fun verifyPhoneNumberWithCode(verificationId:String?,code:String){
        val credential=PhoneAuthProvider.getCredential(verificationId!!,code)
        singInWithPhoneAuthCredential(credential)
    }

    private fun singInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if(it.isSuccessful){
                    val user=it.result?.user

                    val intent=Intent(this@OtpVerifyActivity, TempActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    if(it.exception is FirebaseAuthInvalidCredentialsException){

                    }
                }
            }
    }
}