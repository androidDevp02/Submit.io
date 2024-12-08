package com.yogeshj.autoform.user.viewAppliedForms

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.yogeshj.autoform.databinding.ActivityViewAppliedFormsBinding
import com.yogeshj.autoform.uploadForm.ViewLinkRegistered.ViewLinkRegisteredModel

class  ViewAppliedFormsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityViewAppliedFormsBinding

    private lateinit var myAdapter: ViewAppliedFormsAdapter
    private lateinit var dataList: ArrayList<ViewAppliedFormsModel>

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
        binding=ActivityViewAppliedFormsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLoadingDialog()

        MobileAds.initialize(this@ViewAppliedFormsActivity)
        handler.post(loadAdRunnable)

        binding.navBar.apply { alpha = 0f; translationY = -30f }
        binding.recyclerForms.apply { alpha = 0f; translationY = 20f }

        binding.navBar.animate().alpha(1f).translationY(0f).setDuration(1000).start()
        binding.recyclerForms.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()


        showLoading()

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        binding.back.setOnClickListener {
            finish()
        }

        dataList=ArrayList()
        binding.recyclerForms.layoutManager=
            LinearLayoutManager(this@ViewAppliedFormsActivity, LinearLayoutManager.VERTICAL, false)
        myAdapter= ViewAppliedFormsAdapter(dataList,this@ViewAppliedFormsActivity)
        binding.recyclerForms.adapter = myAdapter
        val db = FirebaseDatabase.getInstance().getReference("Payment")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (paymentSnapshot in snapshot.children) {
                        val userId = paymentSnapshot.child("userId").value as? String
                        if (userId == FirstScreenActivity.auth.currentUser?.uid) {
                            val name = paymentSnapshot.child("name").value as? String ?: "Unknown"
                            val host = paymentSnapshot.child("host").value as? String?:"Unknown"
                            val icon = paymentSnapshot.child("icon").value as? String
                            dataList.add(ViewAppliedFormsModel(icon,name,host))
                        }
                    }

                    myAdapter.notifyDataSetChanged()
                }
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })
        val db2 = FirebaseDatabase.getInstance().getReference("LinkApplied")
        db2.child(FirstScreenActivity.auth.currentUser?.uid?:return).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val curr = snap.getValue(ViewLinkRegisteredModel::class.java)
                        if (curr!=null) {
                            dataList.add(ViewAppliedFormsModel(curr?.imageId,curr?.name,curr?.host))
                        }
                        myAdapter.notifyDataSetChanged()
                    }
                }
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })

    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@ViewAppliedFormsActivity)
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