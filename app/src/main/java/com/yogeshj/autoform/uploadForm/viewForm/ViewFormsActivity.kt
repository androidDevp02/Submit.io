package com.yogeshj.autoform.uploadForm.viewForm

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.CardFormRecyclerView.CardFormAdapter
import com.yogeshj.autoform.CardFormRecyclerView.CardFormModel
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivityViewFormsBinding
import com.yogeshj.autoform.recommendationRecyclerView.RecommendationCardFormAdapter
import com.yogeshj.autoform.recommendationRecyclerView.RecommendationCardFormModel
import com.yogeshj.autoform.uploadForm.FormDetails
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViewFormsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityViewFormsBinding

    lateinit var myAdapter: ViewFormsAdapter
    lateinit var dataList: ArrayList<ViewFormModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewFormsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}