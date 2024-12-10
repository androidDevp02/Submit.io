package com.yogeshj.autoform.user

import android.app.Dialog
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.user.userFormFragment.CardFormRecyclerView.CardFormAdapter
import com.yogeshj.autoform.user.userFormFragment.CardFormRecyclerView.CardFormModel
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.splashScreenAndIntroScreen.IntroActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.user.userFormFragment.categoriesRecyclerView.CategoriesAdapter
import com.yogeshj.autoform.user.userFormFragment.categoriesRecyclerView.CategoriesModel
import com.yogeshj.autoform.databinding.ActivityHomeScreenBinding
import com.yogeshj.autoform.user.userFormFragment.profile.UpdateProfileActivity
import com.yogeshj.autoform.user.userFormFragment.recommendationRecyclerView.RecommendationCardFormAdapter
import com.yogeshj.autoform.user.userFormFragment.recommendationRecyclerView.RecommendationCardFormModel
import com.yogeshj.autoform.uploadForm.FormDetails
import com.yogeshj.autoform.user.userFormFragment.RecommendationSeeAllActivity
import com.yogeshj.autoform.user.userFormFragment.searchForms.SearchActivity
import com.yogeshj.autoform.user.userSubscriptionFragment.SubscriptionActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var myAdapterRecommendation: RecommendationCardFormAdapter
    private lateinit var dataListRecommendation: ArrayList<RecommendationCardFormModel>
//    private lateinit var tempDataListRecommendation: ArrayList<RecommendationCardFormModel>

    private lateinit var myAdapterCategory: CategoriesAdapter
    private lateinit var dataListCategory: ArrayList<CategoriesModel>



    private lateinit var binding:ActivityHomeScreenBinding

    private lateinit var dialog:Dialog


    companion object{
        lateinit var myAdapter: CardFormAdapter
        lateinit var dataList: ArrayList<CardFormModel>
//        lateinit var tempDataList: ArrayList<CardFormModel>
        var appliedFormNames=HashSet<String>()
    }


//    override fun onResume() {
//        super.onResume()
//
//    }

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
        binding=ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        initLoadingDialog()
//        FirstScreenActivity.auth=FirebaseAuth.getInstance()
//        initializeUIAnimations()
//        setupEventListeners()
//        showLoading()
//
//        MobileAds.initialize(this@HomeScreenActivity)
//        handler.post(loadAdRunnable)
//
//        val dbRef = FirebaseDatabase.getInstance().getReference("SubscriptionPayment")
//        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                appliedFormNames.clear()
//                if (snapshot.exists()) {
//                    for (snap in snapshot.children) {
//                        val subscriptionDetails = snap.getValue(SubscriptionPaymentModel::class.java)
//                        if (subscriptionDetails?.userId == FirstScreenActivity.auth.currentUser?.uid) {
//                            val endDate = subscriptionDetails?.endDate
//
//                            if (endDate != null && System.currentTimeMillis() > endDate) {
//                                // Subscription expired
//                                binding.subscribe.text = "Subscribe"
//                                snap.ref.removeValue()
//                            }
//                            else
//                                binding.subscribe.text="Premium Member"
//                            break
//                        }
//                    }
//                }
//                hideLoading()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                hideLoading()
//            }
//        })
//
//        fetchAppliedForms {
//            setRecyclerViewRecommendation()
//            setRecyclerViewCategory()
//            setRecyclerView()
//            hideLoading()
//        }
    }

    private fun initializeUIAnimations() {
        binding.profilePic.apply { alpha = 0f; translationY = -30f }
        binding.helloTxt.apply { alpha = 0f; translationY = -30f }
        binding.name.apply { alpha = 0f; translationY = -30f }
        binding.loginIcon.apply { alpha = 0f; translationY = -30f }
        binding.search.apply { alpha = 0f; translationY = 30f }
        binding.recommendation.apply { alpha = 0f; translationY = 30f }
        binding.recyclerRecommendation.apply { alpha = 0f; translationY = 30f }
        binding.categoriesListRecyclerview.apply { alpha = 0f; translationY = 30f }
        binding.recyclerAll.apply { alpha = 0f; translationY = 30f }

        binding.profilePic.animate().alpha(1f).translationY(0f).setDuration(1000).start()
        binding.helloTxt.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()
        binding.name.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()
        binding.loginIcon.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()
        binding.search.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()
        binding.recommendation.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()
        binding.recyclerRecommendation.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()
        binding.categoriesListRecyclerview.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()
        binding.recyclerAll.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()

    }

    private fun setupEventListeners() {

        binding.subscribe.setOnClickListener {
            if(binding.subscribe.text=="Subscribe")
                startActivity(Intent(this@HomeScreenActivity, SubscriptionActivity::class.java))
        }

        binding.recommendationSeeAll.setOnClickListener {
            startActivity(Intent(this@HomeScreenActivity, RecommendationSeeAllActivity::class.java))
        }

//        binding.viewForms.setOnClickListener {
//            startActivity(Intent(this@HomeScreenActivity,ViewAppliedFormsActivity::class.java))
//        }

        binding.search.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.loginIcon.setOnClickListener {
            FirstScreenActivity.auth.signOut()
            TaskStackBuilder.create(this@HomeScreenActivity)
                .addNextIntentWithParentStack(Intent(this@HomeScreenActivity,FirstScreenActivity::class.java))
                .addNextIntent(Intent(this@HomeScreenActivity, IntroActivity::class.java))
                .startActivities()
            finish()
        }

        binding.profilePic.setOnClickListener{
            startActivity(Intent(this@HomeScreenActivity, UpdateProfileActivity::class.java))
        }
    }

    private fun fetchAppliedForms(onComplete: () -> Unit) {
        showLoading()
        val dbRef = FirebaseDatabase.getInstance().getReference("LinkApplied")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                appliedFormNames.clear()
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        if (snap.key == FirstScreenActivity.auth.currentUser?.uid) {
                            for (exams in snap.children) {
                                val name = exams.child("name").value as? String ?: "Unknown"
                                appliedFormNames.add(name)
                            }
                            break
                        }
                    }
                }
                hideLoading()
                onComplete()
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })
    }

    private fun setRecyclerViewRecommendation() {
        showLoading()
        val category=HashSet<String>()
        val db2 = FirebaseDatabase.getInstance().getReference("UsersInfo")
        db2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {

                        val profilePicUrl = snap.child("profilePic").value?.toString()
                        val currentUser = snap.getValue(User::class.java)
                        if (currentUser!=null && FirstScreenActivity.auth.currentUser!!.uid==currentUser.uid) {
                            if(currentUser.field1!=null)
                                category.add(currentUser.field1.toString())
                            if(currentUser.field2!=null)
                                category.add(currentUser.field2.toString())
                            if(currentUser.field3!=null)
                                category.add(currentUser.field3.toString())
                            if(profilePicUrl!=null)
                            {
                                Glide.with(this@HomeScreenActivity)
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.user_icon)
                                    .error(R.drawable.user_icon)
                                    .apply(RequestOptions.circleCropTransform()).into(binding.profilePic)
                            }


                            //heading name
                            binding.name.text=currentUser.name.toString()
                            for(value in category)
                                Log.d("cat",value)
                            fetchRecommendedForms(category)
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

    private fun fetchRecommendedForms(category:Set<String>) {
        showLoading()
//        tempDataListRecommendation=ArrayList()
        dataListRecommendation = ArrayList()
        myAdapterRecommendation = RecommendationCardFormAdapter(dataListRecommendation, this@HomeScreenActivity)
        binding.recyclerRecommendation.layoutManager=LinearLayoutManager(this@HomeScreenActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerRecommendation.adapter = myAdapterRecommendation
        Log.d("Categories", "User Interested Categories: $category")
        val db = FirebaseDatabase.getInstance().getReference("UploadForm")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        for(examSnap in snap.children)
                        {

                            val currentUser = examSnap.getValue(FormDetails::class.java)
                            val childCategory = examSnap.child("category").getValue(String::class.java)
                            if (currentUser!=null && childCategory!=null && category.contains(childCategory)) {

                                val deadlineStr = currentUser.deadline
                                val examDeadline = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(deadlineStr)
                                val currentDate = Calendar.getInstance().time

                                if (examDeadline != null) {
                                    if((currentUser.status=="Live" || currentUser.status=="Upcoming") && examDeadline.before(currentDate)){
                                        examSnap.ref.child("status").setValue("Expired")
                                        continue
                                    }
                                    if(examDeadline.before(currentDate))
                                            continue
                                    if(appliedFormNames.contains(currentUser.examName))
                                        continue
                                    dataListRecommendation.add(RecommendationCardFormModel(currentUser.icon!!, currentUser.examName?:"No exam name",currentUser.examHostName?:"No host name",currentUser.deadline?:"DD/MM/YYYY",currentUser.examDate?:"DD/MM/YYYY",0,currentUser.fees,currentUser.category!!,currentUser.status!!))
                                }
                            }
                        }
                    }
                    myAdapterRecommendation.notifyDataSetChanged()
                }
                hideLoading()
            }
            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })
    }

    private fun setRecyclerView() {
        showLoading()
//        tempDataList=ArrayList()
        dataList =ArrayList()
        myAdapter = CardFormAdapter(dataList,this@HomeScreenActivity)
        binding.recyclerAll.layoutManager=LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerAll.adapter = myAdapter
        val db = FirebaseDatabase.getInstance().getReference("UploadForm")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        for(examSnap in snap.children)
                        {
                            val currentExam = examSnap.getValue(FormDetails::class.java)
                            if (currentExam!=null) {
                                val deadlineStr = currentExam.deadline
                                val examDeadline = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(deadlineStr)
                                val currentDate = Calendar.getInstance().time



                                if (examDeadline != null && examDeadline.before(currentDate)) {
                                    if(currentExam.status=="Live" || currentExam.status=="Upcoming"){
                                        examSnap.ref.child("status").setValue("Expired")
                                    }
                                    dataList.add(CardFormModel(currentExam.icon!!, currentExam.examName?:"No exam name",currentExam.examHostName?:"No host name",currentExam.deadline?:"DD/MM/YYYY",currentExam.examDate?:"DD/MM/YYYY",0,currentExam.fees,currentExam.category!!,currentExam.status!!))
                                    continue
                                }

                                if(appliedFormNames.contains(currentExam.examName)) {
//                                        tempDataList.add(CardFormModel(currentExam.icon!!, currentExam.examName?:"No exam name",currentExam.examHostName?:"No host name",currentExam.deadline?:"DD/MM/YYYY",currentExam.examDate?:"DD/MM/YYYY",0,currentExam.fees?:0,currentExam.category!!,currentExam.status?:"Not Set"))
                                    continue
                                }
                                    dataList.add(CardFormModel(currentExam.icon!!, currentExam.examName?:"No exam name",currentExam.examHostName?:"No host name",currentExam.deadline?:"DD/MM/YYYY",currentExam.examDate?:"DD/MM/YYYY",0,currentExam.fees,currentExam.category!!,currentExam.status?:"Not Set"))
                            }
                        }


                    }
//                    dataList.addAll(tempDataList)
                    myAdapter.notifyDataSetChanged()
                }
                hideLoading()
            }
            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })
    }

    private fun setRecyclerViewCategory() {
        showLoading()
        dataListCategory = ArrayList()
        binding.recyclerRecommendation.layoutManager=LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        dataListCategory.add(CategoriesModel("All"))
        dataListCategory.add(CategoriesModel("Engineering"))
        dataListCategory.add(CategoriesModel("Medical"))
        dataListCategory.add(CategoriesModel("Civil Services"))
        dataListCategory.add(CategoriesModel("Law"))
        dataListCategory.add(CategoriesModel("Design Courses"))
        dataListCategory.add(CategoriesModel("Hotel Management"))
        dataListCategory.add(CategoriesModel("National Defence Academy"))
        dataListCategory.add(CategoriesModel("Indian Statistical Service"))
        myAdapterCategory = CategoriesAdapter(dataListCategory, this@HomeScreenActivity)
        binding.categoriesListRecyclerview.adapter = myAdapterCategory
        hideLoading()
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@HomeScreenActivity)
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