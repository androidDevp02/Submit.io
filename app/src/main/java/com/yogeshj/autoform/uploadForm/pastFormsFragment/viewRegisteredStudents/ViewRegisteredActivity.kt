package com.yogeshj.autoform.uploadForm.pastFormsFragment.viewRegisteredStudents

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivityViewRegisteredBinding

class ViewRegisteredActivity : AppCompatActivity() {

    private lateinit var binding:ActivityViewRegisteredBinding

    private lateinit var adapter: ViewRegisteredAdapter
    private val datalist = ArrayList<ViewRegisteredModel>()

    private var examName:String?=null

    private lateinit var dialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewRegisteredBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()
        showLoading()
        FirstScreenActivity.auth=FirebaseAuth.getInstance()

        MobileAds.initialize(this@ViewRegisteredActivity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)


        adapter = ViewRegisteredAdapter(datalist, this)
        binding.studentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.studentsRecyclerView.adapter = adapter

        examName=intent.getStringExtra("heading")

        fetchRegisteredStudents()

        binding.back.setOnClickListener {
            finish()
        }

    }
    private fun fetchRegisteredStudents() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Payment")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datalist.clear()
                for (paymentSnapshot in snapshot.children) {
                    val examNameDb = paymentSnapshot.child("examName").value as? String
                    if (examNameDb == examName) {
                        val applicationFormDetailsSnapshot = paymentSnapshot.child("ApplicationFormDetails")
                        val name = applicationFormDetailsSnapshot.child("name").value as? String ?: "Unknown"
                        val imageId = applicationFormDetailsSnapshot.child("profilePic").value as? String

                        val detailsMap = mutableMapOf<String, String>()
                        for (detail in applicationFormDetailsSnapshot.children) {
                            val key = detail.key ?: continue
                            val value = detail.value as? String ?: continue
                            detailsMap[key] = value
                        }

                        val student = ViewRegisteredModel( imageId = imageId,name = name, details = detailsMap)
                        datalist.add(student)
                    }
                }
                adapter.notifyDataSetChanged()
                binding.registered.text="Registered: ${datalist.size}"
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Failed to retrieve data: ${error.message}")
                hideLoading()
            }
        })
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@ViewRegisteredActivity)
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