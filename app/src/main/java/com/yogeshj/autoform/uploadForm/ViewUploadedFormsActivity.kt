package com.yogeshj.autoform.uploadForm

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
import com.yogeshj.autoform.databinding.ActivityViewUploadedFormsBinding

class ViewUploadedFormsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityViewUploadedFormsBinding

    private lateinit var myAdapter: YourFormsAdapter
    private lateinit var dataList: ArrayList<YourFormsModel>

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
        binding=ActivityViewUploadedFormsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        MobileAds.initialize(this@ViewUploadedFormsActivity)
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
        binding.recyclerForms.layoutManager=LinearLayoutManager(this@ViewUploadedFormsActivity,LinearLayoutManager.VERTICAL, false)
        myAdapter= YourFormsAdapter(dataList,this@ViewUploadedFormsActivity)
        binding.recyclerForms.adapter = myAdapter
        val db = FirebaseDatabase.getInstance().getReference("UploadForm")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for(snap2 in snapshot.children) {
                        for (snap in snap2.children) {
                            val currentUser = snap.getValue(FormDetails::class.java)!!
                            if (currentUser.uid == FirstScreenActivity.auth.currentUser!!.uid) {
                                dataList.add(YourFormsModel(currentUser.uid, currentUser.icon, currentUser.examName, currentUser.examHostName))
                            }
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

    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@ViewUploadedFormsActivity)
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