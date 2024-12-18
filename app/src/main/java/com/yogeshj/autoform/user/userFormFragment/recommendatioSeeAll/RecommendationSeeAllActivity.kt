package com.yogeshj.autoform.user.userFormFragment.recommendatioSeeAll

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.databinding.ActivityRecommendationSeeAllBinding
import com.yogeshj.autoform.uploadForm.uploadNewFormFragment.FormDetails
import com.yogeshj.autoform.user.userFormFragment.UserFormFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class RecommendationSeeAllActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRecommendationSeeAllBinding

    private lateinit var dialog:Dialog

    private lateinit var myAdapter: RecommendationSeeAllAdapter
    private lateinit var dataList: ArrayList<RecommendationSeeAllModel>

    var appliedFormNames=HashSet<String>()

    val category=HashSet<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRecommendationSeeAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()
        showLoading()

        FirstScreenActivity.auth = FirebaseAuth.getInstance()
        MobileAds.initialize(this@RecommendationSeeAllActivity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.back.setOnClickListener {
            finish()
        }

        fetchAppliedForms {
            findFieldOfInterestCategories()
            hideLoading()
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
                onComplete()
                hideLoading()
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
                        val currentUser = snap.getValue(User::class.java)
                        if (currentUser!=null && FirstScreenActivity.auth.currentUser!!.uid==currentUser.uid) {
                            if(currentUser.field1!=null)
                                category.add(currentUser.field1.toString())
                            if(currentUser.field2!=null)
                                category.add(currentUser.field2.toString())
                            if(currentUser.field3!=null)
                                category.add(currentUser.field3.toString())

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
        dataList= ArrayList()
        myAdapter=RecommendationSeeAllAdapter(dataList,this@RecommendationSeeAllActivity)
        binding.recycler.layoutManager=LinearLayoutManager(this@RecommendationSeeAllActivity, LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = myAdapter
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
//                            Log.d("EXAM",currentUser!!.examName+" "+currentUser!!.category)
                            if (currentUser!=null && childCategory!=null && category.contains(childCategory)) {

                                val deadlineStr = currentUser.deadline
                                val examDeadline = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(deadlineStr!!)
                                val currentDate = Calendar.getInstance().time

                                if (examDeadline != null) {
                                    if((currentUser.status=="Live" || currentUser.status=="Upcoming") && examDeadline.before(currentDate)){
                                        examSnap.ref.child("status").setValue("Expired")
                                        continue
                                    }
                                    if(examDeadline.before(currentDate))
                                        continue
                                    if(UserFormFragment.appliedFormNames.contains(currentUser.examName))
                                        continue
                                    dataList.add(RecommendationSeeAllModel(currentUser.icon!!, currentUser.examName?:"No exam name",currentUser.examHostName?:"No host name",currentUser.deadline?:"DD/MM/YYYY",currentUser.examDate?:"DD/MM/YYYY",0,currentUser.fees,currentUser.category!!,currentUser.status!!))
                                }
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
        dialog = Dialog(this@RecommendationSeeAllActivity)
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