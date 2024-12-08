package com.yogeshj.autoform.uploadForm.viewForm

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivityViewFormsBinding
import com.yogeshj.autoform.uploadForm.FormDetails

class ViewFormsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityViewFormsBinding

    lateinit var myAdapter: ViewFormsAdapter
    lateinit var dataList: ArrayList<ViewFormModel>

    private lateinit var dialog: Dialog

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
        binding= ActivityViewFormsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()
        showLoading()

        MobileAds.initialize(this@ViewFormsActivity)
        handler.post(loadAdRunnable)

        binding.back.setOnClickListener{
            finish()
        }

        dataList = ArrayList()
        myAdapter = ViewFormsAdapter(dataList,this@ViewFormsActivity)
        binding.formDetailsRecyclerView.layoutManager=LinearLayoutManager(this@ViewFormsActivity,LinearLayoutManager.VERTICAL,false)
        binding.formDetailsRecyclerView.adapter = myAdapter

        val examName=intent.getStringExtra("heading")
        binding.header.text=examName
        val db=FirebaseDatabase.getInstance().getReference("UploadForm")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        for(examSnap in snap.children)
                        {
                            val currentUser = examSnap.getValue(FormDetails::class.java)
                            if (currentUser != null && examName == currentUser.examName) {


                                for (childSnap in examSnap.children) {
                                    var key = childSnap.key ?: ""
                                    val value = childSnap.value.toString()
                                    if(key=="icon")
                                    {
                                        Glide.with(this@ViewFormsActivity)
                                            .load(value)
                                            .placeholder(R.drawable.user_icon)
                                            .error(R.drawable.user_icon)
                                            .apply(RequestOptions.circleCropTransform()).into(binding.formLogo)
                                        continue
                                    }
                                    else if(key=="Application Form Details")
                                    {
                                        key="Details Required in the application form are: "
                                    }
                                    else if (key=="uid" || key=="importantDetails" || key=="paymentNumber")
                                        continue

                                    dataList.add(ViewFormModel(key, value))
                                }

                                myAdapter.notifyDataSetChanged()
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
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@ViewFormsActivity)
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