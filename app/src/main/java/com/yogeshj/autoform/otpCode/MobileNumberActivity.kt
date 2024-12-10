package com.yogeshj.autoform.otpCode

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yogeshj.autoform.databinding.ActivityMobileNumberBinding
import java.util.concurrent.TimeUnit

class MobileNumberActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMobileNumberBinding

    private lateinit var auth:FirebaseAuth
    private var storedVerificationId:String?=""
    private lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMobileNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= Firebase.auth

        callbacks=object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Toast.makeText(this@MobileNumberActivity, "Verification Completed!", Toast.LENGTH_SHORT).show()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if(e is FirebaseAuthInvalidCredentialsException){

                }
                else if(e is FirebaseTooManyRequestsException){

                }
                else if(e is FirebaseAuthMissingActivityForRecaptchaException){

                }
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                storedVerificationId=verificationId
                resendToken=token

                Toast.makeText(this@MobileNumberActivity, "OTP Sent!", Toast.LENGTH_SHORT).show()
                val intent=Intent(this@MobileNumberActivity, OtpVerifyActivity::class.java)
                intent.putExtra("storedVerificationId",storedVerificationId)
                startActivity(intent)
            }

        }

        binding.getOTP.setOnClickListener {
            val phoneNumber = binding.mobileNo.text.toString()
            if (phoneNumber.isNotBlank()) {
                Toast.makeText(this@MobileNumberActivity, "Sending OTP to $phoneNumber", Toast.LENGTH_SHORT).show()
                startPhoneNumberVerification(phoneNumber)
            } else {
                Toast.makeText(this@MobileNumberActivity, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun startPhoneNumberVerification(phoneNumber:String){
        val options=PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91"+phoneNumber)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this@MobileNumberActivity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential:PhoneAuthCredential){
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if(it.isSuccessful){
                    val user=it.result?.user
                    Toast.makeText(this@MobileNumberActivity,"Hi1",Toast.LENGTH_LONG).show()
                    val intent=Intent(this@MobileNumberActivity, TempActivity::class.java)
                    startActivity(intent)
                }
                else{
                    if(it.exception is FirebaseAuthInvalidCredentialsException){

                    }
                }
            }
    }

//    private fun updateUI(user:FirebaseUser?=auth.currentUser){
//
//    }
//    companion object{
//        private const val TAG="PhoneAuthActivity"
//    }
}