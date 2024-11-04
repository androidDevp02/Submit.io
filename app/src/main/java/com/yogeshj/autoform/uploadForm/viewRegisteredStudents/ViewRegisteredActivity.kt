package com.yogeshj.autoform.uploadForm.viewRegisteredStudents

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.databinding.ActivityViewRegisteredBinding

class ViewRegisteredActivity : AppCompatActivity() {

    private lateinit var binding:ActivityViewRegisteredBinding

    private lateinit var adapter: ViewRegisteredAdapter
    private val datalist = ArrayList<ViewRegisteredModel>()

    private var examName:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewRegisteredBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        val currentUserId = FirstScreenActivity.auth.currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().getReference("Payment")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Failed to retrieve data: ${error.message}")
            }
        })
    }
}