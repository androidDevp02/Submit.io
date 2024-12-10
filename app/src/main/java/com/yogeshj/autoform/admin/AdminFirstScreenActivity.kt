package com.yogeshj.autoform.admin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.admin.requests.UploadFormSignUpAdapter
import com.yogeshj.autoform.admin.requests.UploadFormSignUpModel
import com.yogeshj.autoform.databinding.ActivityAdminFirstScreenBinding

class AdminFirstScreenActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAdminFirstScreenBinding
    lateinit var myAdapter: UploadFormSignUpAdapter
    lateinit var dataList: ArrayList<UploadFormSignUpModel>

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
        binding=ActivityAdminFirstScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        MobileAds.initialize(this@AdminFirstScreenActivity)
        handler.post(loadAdRunnable)




    }
}