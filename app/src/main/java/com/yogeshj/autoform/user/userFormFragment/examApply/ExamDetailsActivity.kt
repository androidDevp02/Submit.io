package com.yogeshj.autoform.user.userFormFragment.examApply

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivityExamDetailsBinding
import com.yogeshj.autoform.uploadForm.uploadNewFormFragment.FormDetails

class ExamDetailsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityExamDetailsBinding

    private var containsLink=false
    private var linkURL=""

    private lateinit var dialog:Dialog

    private lateinit var status:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityExamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navBar.apply { alpha = 0f; translationY = -30f }
        binding.headingCard.apply { alpha = 0f; translationY = 20f }

        binding.navBar.animate().alpha(1f).translationY(0f).setDuration(1000).start()
        binding.headingCard.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()


        initLoadingDialog()



        MobileAds.initialize(this@ExamDetailsActivity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)


        FirstScreenActivity.auth = FirebaseAuth.getInstance()

        binding.back.setOnClickListener {
            finish()
        }

        showLoading()
        val examName=intent.getStringExtra("heading")
        val db = FirebaseDatabase.getInstance().getReference("UploadForm")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for(snap2 in snapshot.children) {
                        for (snap in snap2.children) {
                            val currentUser = snap.getValue(FormDetails::class.java)!!
                            val child = snap.child("examName").getValue(String::class.java)
                            if (child == examName) {

                                val link = snap.child("link").getValue(String::class.java)
                                if(link!=null)
                                {
                                    containsLink=true
                                    linkURL=link
                                }
                                Glide.with(this@ExamDetailsActivity)
                                    .load(currentUser.icon)
                                    .placeholder(R.drawable.user_icon)
                                    .error(R.drawable.user_icon)
                                    .apply(RequestOptions.circleCropTransform()).into(binding.logo)

                                status=currentUser.status.toString()

                                binding.examName.text = currentUser.examName
                                binding.examHost.text = currentUser.examHostName
                                binding.examDate.text = currentUser.examDate
                                binding.examDeadline.text=currentUser.deadline
                                binding.examFees.text=currentUser.fees.toString()
//                            binding.registered.text=
                                binding.examDescriptionText.text = currentUser.examDescription
                                binding.eligibilityText.text = currentUser.eligibility
//                                binding.impDetailsText.text = currentUser.importantDetails
//                            setUpButtonListeners()

                                break
                            }
                        }
                    }
                }
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })
        setUpButtonListeners()

        binding.applyBtn.setOnClickListener {
            if(containsLink)
            {
                val intent = Intent(Intent.ACTION_VIEW,Uri.parse(linkURL))
                startActivity(intent)
            }
            else if(status=="Live")
            {
                val intent=Intent(this@ExamDetailsActivity, ReviewDataActivity::class.java)
                intent.putExtra("heading",examName)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this@ExamDetailsActivity,"Sorry, The form is already expired!",Toast.LENGTH_LONG).show()
            }

        }


        //Apply -> Applied
        val payDb = FirebaseDatabase.getInstance().getReference("Payment")
        showLoading()
        payDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val currentUser = snap.getValue(PaymentDataModel::class.java)!!
                        val child = snap.child("examName").getValue(String::class.java)
                        if (currentUser.userId== FirstScreenActivity.auth.currentUser!!.uid && child==examName) {
                            binding.appliedBtn.visibility=View.VISIBLE
                            binding.applyBtn.visibility=View.GONE
                            break
                        }
                    }
                }
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })

    }

    private fun setUpButtonListeners() {
        binding.eligibilityBtn.setOnClickListener {
            binding.examDescriptionText.visibility=View.GONE
            binding.eligibilityText.visibility=View.VISIBLE
            binding.eligibilityScroll.visibility=View.VISIBLE
//            binding.impDetailsText.visibility=View.GONE
        }

//        binding.impDetailsBtn.setOnClickListener {
//            binding.examDescriptionText.visibility=View.GONE
//            binding.eligibilityText.visibility=View.GONE
////            binding.impDetailsText.visibility=View.VISIBLE
////            binding.impDetailsScroll.visibility=View.VISIBLE
//        }

        binding.examDescriptionBtn.setOnClickListener {
            binding.examDescriptionText.visibility=View.VISIBLE
            binding.examDescriptionScroll.visibility=View.VISIBLE
            binding.eligibilityText.visibility=View.GONE
//            binding.impDetailsText.visibility=View.GONE
        }
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@ExamDetailsActivity)
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