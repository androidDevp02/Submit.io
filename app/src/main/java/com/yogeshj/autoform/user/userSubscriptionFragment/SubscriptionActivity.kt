package com.yogeshj.autoform.user.userSubscriptionFragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivitySubscriptionBinding
import com.yogeshj.autoform.user.UserMainActivity
import org.json.JSONObject
import java.util.Calendar

class SubscriptionActivity : AppCompatActivity(), PaymentResultWithDataListener {

    private lateinit var binding:ActivitySubscriptionBinding
    private var selectedOption: String = ""
    private var fees=0

    private lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        Toast.makeText(this@SubscriptionActivity,"${Calendar.getInstance().time}",Toast.LENGTH_LONG).show()
        binding.back.setOnClickListener {
            finish()
        }

        selectedOption=intent.getStringExtra("selectedOption")!!
        fees=intent.getIntExtra("fees",0)

        binding.amount.text="${selectedOption}, Rs${fees}"


        Checkout.preload(applicationContext)
        val co = Checkout()
        co.setKeyID("rzp_test_0DG18Nx16j11Ph")

        binding.confirmButton.setOnClickListener {
            initPayment()
        }

    }

    private fun initPayment() {
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name", "Submit.io")
            options.put("description", "Fees for $selectedOption subscription")
            options.put("image", "https://media.tradly.app/images/marketplace/34521/razor_pay_icon-ICtywSbN.png")
            options.put("theme.color", "#3399cc")
            options.put("currency", "INR")
            options.put("amount", (fees * 100).toString())

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            val preFill = JSONObject()
            preFill.put("contact","7982582284")
//            options.put("prefill",preFill)
            co.open(this@SubscriptionActivity as Activity?, options)
        } catch (e: Exception) {
            Toast.makeText(this@SubscriptionActivity,"Error initializing",Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        showLoading()
        val userId = FirstScreenActivity.auth.currentUser?.uid
        val userEmail=FirstScreenActivity.auth.currentUser?.email
        val paymentRefUser = FirebaseDatabase.getInstance().getReference("SubscriptionPayment")
        val paymentId = paymentRefUser.push().key
        val planType=selectedOption
        val startDate=System.currentTimeMillis()
        val endDate=calculateEndDate(planType,startDate)
        val paymentDate=Calendar.getInstance().time

        val paymentData = SubscriptionPaymentModel(userId,userEmail,planType,startDate,endDate,paymentId,fees.toString(),"success",paymentDate.toString())



        val paymentRefHis = FirebaseDatabase.getInstance().getReference("SubscriptionPaymentHistory")
        paymentRefHis.child(paymentId!!).setValue(paymentData)

        paymentRefUser.child(paymentId).setValue(paymentData)
            .addOnSuccessListener {

                Toast.makeText(this@SubscriptionActivity,"Payment Success",Toast.LENGTH_LONG).show()
                hideLoading()
                val intent = Intent(this@SubscriptionActivity, UserMainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this@SubscriptionActivity, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                hideLoading()
            }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(this@SubscriptionActivity, "Error: $p2", Toast.LENGTH_LONG).show()
    }

    private fun calculateEndDate(planType: String, startDate: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startDate
        when (planType) {
            "Weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "Monthly" -> calendar.add(Calendar.MONTH, 1)
            "Yearly" -> calendar.add(Calendar.YEAR, 1)
        }
        return calendar.timeInMillis
    }


    private fun initLoadingDialog() {
        dialog = Dialog(this@SubscriptionActivity)
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