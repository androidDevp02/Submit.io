package com.yogeshj.autoform.admin.feedback

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FeedbackFragment.FeedbackModel
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.databinding.FragmentAdminFeedbackBinding


class AdminFeedbackFragment : Fragment() {

    private lateinit var binding:FragmentAdminFeedbackBinding

    private lateinit var myAdapter: AdminFeedbackAdapter
    private lateinit var dataList: ArrayList<FeedbackModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=FragmentAdminFeedbackBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirstScreenActivity.auth= FirebaseAuth.getInstance()
        dataList =ArrayList()
        myAdapter = AdminFeedbackAdapter(dataList,requireContext())
        binding.recyclerViewFeedback.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewFeedback.adapter= myAdapter
        val db= FirebaseDatabase.getInstance().getReference("Feedback")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    dataList.clear()
                    for (snap in snapshot.children) {
                        for(snap2 in snap.children){
                            val curr=snap2.getValue(FeedbackModel::class.java)
                            if(curr!=null)
                                dataList.add(curr)
                        }
                    }
                    myAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}