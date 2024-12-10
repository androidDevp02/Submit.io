package com.yogeshj.autoform.user.userFormFragment.searchForms

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
import com.yogeshj.autoform.user.userFormFragment.CardFormRecyclerView.CardFormModel
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivitySearchBinding
import com.yogeshj.autoform.uploadForm.FormDetails

class SearchActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySearchBinding

    private lateinit var myAdapter: SearchAdapter
    private lateinit var dataList: ArrayList<CardFormModel>

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
        binding=ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()
        showLoading()
        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        binding.search.requestFocus()

        MobileAds.initialize(this@SearchActivity)
        handler.post(loadAdRunnable)

        dataList=ArrayList()

        val db = FirebaseDatabase.getInstance().getReference("UploadForm")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        for(examSnap in snap.children)
                        {
                            val currentExam = examSnap.getValue(FormDetails::class.java)!!

                            dataList.add(CardFormModel(currentExam.icon.toString(),currentExam.examName.toString(),currentExam.examHostName.toString(),currentExam.deadline?:"DD/MM/YYYY",currentExam.examDate?:"DD/MM/YYYY",0,currentExam.fees,currentExam.category!!,"Live"))
                        }


                    }
                    setRecyclerView()
                }
                hideLoading()
            }
            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })


        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() != "") {
                    filterData(p0.toString())
                } else {
                    setRecyclerView()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


        binding.backImg.setOnClickListener {
            finish()
        }
    }

    private fun filterData(filterText: String) {
        val filterData = ArrayList<CardFormModel>()
        for (item in dataList) {
            if (item.examName.lowercase().contains(filterText.lowercase())) {
                filterData.add(item)
            }
        }
        myAdapter.filterList(filterList = filterData)
    }

    private fun setRecyclerView() {
        myAdapter = SearchAdapter(dataList, this@SearchActivity)
        binding.recycler.layoutManager = LinearLayoutManager(this@SearchActivity)
        binding.recycler.adapter = myAdapter
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@SearchActivity)
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