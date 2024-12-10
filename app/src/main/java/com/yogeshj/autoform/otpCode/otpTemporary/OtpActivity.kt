package com.yogeshj.autoform.otpCode.otpTemporary

//import android.content.Intent
import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.View
//import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.FirebaseException
//import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
//import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
//import com.google.firebase.auth.PhoneAuthCredential
//import com.google.firebase.auth.PhoneAuthOptions
//import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.yogeshj.autoform.databinding.ActivityOtpBinding
//import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    private lateinit var binding:ActivityOtpBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var otp:String
    private lateinit var resendToken: ForceResendingToken
    private lateinit var phoneNumber:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
//        otp=intent.getStringExtra("OTP").toString()
//        resendToken=intent.getParcelableExtra("resendToken")!!
//        phoneNumber=intent.getStringExtra("phoneNumber")!!
//
//        addTextChangeListener()
//        resendOtpVisibility()
//        binding.otpProgressBar.visibility=View.INVISIBLE
//        binding.resendTextView.setOnClickListener {
//            resendVerificationCode()
//            resendOtpVisibility()
//        }
//
//        binding.verifyOTPBtn.setOnClickListener {
//            val typedOtp=(binding.otpEditText1.text.toString()+binding.otpEditText2.text.toString()
//                    +binding.otpEditText3.text.toString()+binding.otpEditText4.text.toString()+
//                    binding.otpEditText5.text.toString()+binding.otpEditText6.text.toString())
//
//            if(typedOtp.isNotEmpty())
//            {
//                if(typedOtp.length==6){
//                    val credential:PhoneAuthCredential=PhoneAuthProvider.getCredential(otp,typedOtp)
//                    binding.otpProgressBar.visibility=View.VISIBLE
//                    signInWithPhoneAuthCredential(credential)
//                }
//                else
//                {
//                    Toast.makeText(this@OtpActivity,"Please Enter a 6-digit Otp",Toast.LENGTH_LONG).show()
//                }
//            }
//            else
//            {
//                Toast.makeText(this@OtpActivity,"Please Enter Otp",Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    private fun resendOtpVisibility(){
//        binding.otpEditText1.setText("")
//        binding.otpEditText2.setText("")
//        binding.otpEditText3.setText("")
//        binding.otpEditText4.setText("")
//        binding.otpEditText5.setText("")
//        binding.otpEditText6.setText("")
//        binding.resendTextView.visibility=View.INVISIBLE
//        binding.resendTextView.isEnabled=false
//
//        Handler(Looper.myLooper()!!).postDelayed(Runnable {
//            binding.resendTextView.visibility=View.VISIBLE
//            binding.resendTextView.isEnabled=true
//        },60000)
//    }
//
//
//    private fun resendVerificationCode(){
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber(phoneNumber) // Phone number to verify
//            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//            .setActivity(this) // Activity (for callback binding)
//            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
//            .setForceResendingToken(resendToken)
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }
//
//    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//            // This callback will be invoked in two situations:
//            // 1 - Instant verification. In some cases the phone number can be instantly
//            //     verified without needing to send or enter a verification code.
//            // 2 - Auto-retrieval. On some devices Google Play services can automatically
//            //     detect the incoming verification SMS and perform verification without
//            //     user action.
//            signInWithPhoneAuthCredential(credential)
//        }
//
//        override fun onVerificationFailed(e: FirebaseException) {
//            // This callback is invoked in an invalid request for verification is made,
//            // for instance if the the phone number format is not valid.
//
//            if (e is FirebaseAuthInvalidCredentialsException) {
//                // Invalid request
//            } else if (e is FirebaseTooManyRequestsException) {
//                // The SMS quota for the project has been exceeded
//            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
//                // reCAPTCHA verification attempted with null Activity
//            }
//
//            // Show a message and update the UI
//        }
//
//        override fun onCodeSent(
//            verificationId: String,
//            token: PhoneAuthProvider.ForceResendingToken,
//        ) {
//            // The SMS verification code has been sent to the provided phone number, we
//            // now need to ask the user to enter the code and then construct a credential
//            // by combining the code with a verification ID.
//            otp=verificationId
//            resendToken=token
//            // Save verification ID and resending token so we can use them later
//        }
//    }
//
//
//    private fun addTextChangeListener(){
//        binding.otpEditText1.addTextChangedListener(EditTextWatcher(binding.otpEditText1))
//        binding.otpEditText2.addTextChangedListener(EditTextWatcher(binding.otpEditText2))
//        binding.otpEditText3.addTextChangedListener(EditTextWatcher(binding.otpEditText3))
//        binding.otpEditText4.addTextChangedListener(EditTextWatcher(binding.otpEditText4))
//        binding.otpEditText5.addTextChangedListener(EditTextWatcher(binding.otpEditText5))
//        binding.otpEditText6.addTextChangedListener(EditTextWatcher(binding.otpEditText6))
//    }
//
//    private fun sendToMain(){
//        startActivity(Intent(this@OtpActivity,MainActivity::class.java))
//    }
//
//    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    binding.otpProgressBar.visibility=View.INVISIBLE
//                    Toast.makeText(this@OtpActivity,"Logged In successfully!",Toast.LENGTH_LONG).show()
//                    sendToMain()
//                } else {
//                    // Sign in failed, display a message and update the UI
//                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
//                        // The verification code entered was invalid
//                    }
//                    // Update UI
//                }
//            }
//    }
//
//
//    inner class EditTextWatcher(private val view:View):TextWatcher{
//        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//        }
//
//        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//        }
//
//        override fun afterTextChanged(p0: Editable?) {
//            val text=p0.toString()
//            when(view.id){
//                R.id.otpEditText1 -> if(text.length==1) binding.otpEditText2.requestFocus()
//                R.id.otpEditText2 -> if(text.length==1) binding.otpEditText3.requestFocus() else if(text.isEmpty()) binding.otpEditText1.requestFocus()
//                R.id.otpEditText3 -> if(text.length==1) binding.otpEditText4.requestFocus() else if(text.isEmpty()) binding.otpEditText2.requestFocus()
//                R.id.otpEditText4 -> if(text.length==1) binding.otpEditText5.requestFocus() else if(text.isEmpty()) binding.otpEditText3.requestFocus()
//                R.id.otpEditText5 -> if(text.length==1) binding.otpEditText6.requestFocus() else if(text.isEmpty()) binding.otpEditText4.requestFocus()
//                R.id.otpEditText6 -> if(text.isEmpty()) binding.otpEditText5.requestFocus()
//            }
//        }
//
    }
}