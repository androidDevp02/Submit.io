package com.yogeshj.autoform.user.userFormFragment

import android.app.Dialog
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
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
import com.yogeshj.autoform.databinding.FragmentUserFormBinding
import com.yogeshj.autoform.user.userFormFragment.recommendationRecyclerView.RecommendationCardFormAdapter
import com.yogeshj.autoform.user.userFormFragment.recommendationRecyclerView.RecommendationCardFormModel
import com.yogeshj.autoform.uploadForm.FormDetails
import com.yogeshj.autoform.user.userFormFragment.profile.UpdateProfileActivity
import com.yogeshj.autoform.user.userFormFragment.searchForms.SearchActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UserFormFragment : Fragment() {

    private lateinit var binding:FragmentUserFormBinding

    companion object{
        lateinit var myAdapter: CardFormAdapter
        lateinit var dataList: ArrayList<CardFormModel>
        //        lateinit var tempDataList: ArrayList<CardFormModel>
        var appliedFormNames=HashSet<String>()
    }

    private lateinit var myAdapterCategory: CategoriesAdapter
    private lateinit var dataListCategory: ArrayList<CategoriesModel>

    private lateinit var myAdapterRecommendation: RecommendationCardFormAdapter
    private lateinit var dataListRecommendation: ArrayList<RecommendationCardFormModel>

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentUserFormBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        MobileAds.initialize(requireContext())
        handler.post(loadAdRunnable)

        initLoadingDialog()


        fetchAppliedForms {
            findFieldOfInterestCategories()
            setRecyclerViewCategory()
            setRecyclerView()
            hideLoading()
        }


        binding.recommendationSeeAll.setOnClickListener {
            startActivity(Intent(context, RecommendationSeeAllActivity::class.java))
        }

        binding.search.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }

        binding.loginIcon.setOnClickListener {
            FirstScreenActivity.auth.signOut()
            TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(Intent(context,FirstScreenActivity::class.java))
                .addNextIntent(Intent(context, IntroActivity::class.java))
                .startActivities()
        }

        binding.profilePic.setOnClickListener{
            startActivity(Intent(context, UpdateProfileActivity::class.java))
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

    private fun findFieldOfInterestCategories() {
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
                                Glide.with(requireContext())
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.user_icon)
                                    .error(R.drawable.user_icon)
                                    .apply(RequestOptions.circleCropTransform()).into(binding.profilePic)
                            }

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
        myAdapterRecommendation = RecommendationCardFormAdapter(dataListRecommendation,requireContext())
        binding.recyclerRecommendation.layoutManager=LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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

    private fun setRecyclerViewCategory() {
        showLoading()
        dataListCategory = ArrayList()
        binding.recyclerRecommendation.layoutManager=LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        dataListCategory.add(CategoriesModel("All"))
        dataListCategory.add(CategoriesModel("Engineering"))
        dataListCategory.add(CategoriesModel("Medical"))
        dataListCategory.add(CategoriesModel("Civil Services"))
        dataListCategory.add(CategoriesModel("Law"))
        dataListCategory.add(CategoriesModel("Design Courses"))
        dataListCategory.add(CategoriesModel("Hotel Management"))
        dataListCategory.add(CategoriesModel("National Defence Academy"))
        dataListCategory.add(CategoriesModel("Indian Statistical Service"))
        myAdapterCategory = CategoriesAdapter(dataListCategory,requireContext())
        binding.categoriesListRecyclerview.adapter = myAdapterCategory
        hideLoading()
    }


    private fun setRecyclerView() {
        showLoading()
//        tempDataList=ArrayList()
        dataList =ArrayList()
        myAdapter = CardFormAdapter(dataList,requireContext())
        binding.recyclerAll.layoutManager=LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerAll.adapter=myAdapter
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


    private fun initLoadingDialog() {
        dialog = Dialog(requireContext())
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