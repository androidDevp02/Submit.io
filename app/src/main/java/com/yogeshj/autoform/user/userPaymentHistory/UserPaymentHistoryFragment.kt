package com.yogeshj.autoform.user.userPaymentHistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.databinding.FragmentUserPaymentHistoryBinding

class UserPaymentHistoryFragment : Fragment() {

    private lateinit var binding:FragmentUserPaymentHistoryBinding

    private lateinit var dataList:ArrayList<UserPaymentHistoryModel>
    private lateinit var myAdapter:UserPaymentHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=FragmentUserPaymentHistoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        dataList=ArrayList()
        binding.recyclerForms.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        myAdapter= UserPaymentHistoryAdapter(dataList,requireContext())
        binding.recyclerForms.adapter = myAdapter
        val db = FirebaseDatabase.getInstance().getReference("Payment")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (paymentSnapshot in snapshot.children) {
                        val userId = paymentSnapshot.child("userId").value as? String
                        if (userId == FirstScreenActivity.auth.currentUser?.uid) {
                            val name = paymentSnapshot.child("examName").value as? String
                            val host = paymentSnapshot.child("examHostName").value as? String
                            val icon = paymentSnapshot.child("icon").value as? String
                            val paymentTime = paymentSnapshot.child("paymentTime").value as? String
                            val paymentDate = paymentSnapshot.child("paymentDate").value as? String
                            val amount = paymentSnapshot.child("amount").value
                            dataList.add(UserPaymentHistoryModel(icon,name,host,amount.toString().toInt(),paymentDate,paymentTime))
                        }
                    }

                    myAdapter.notifyDataSetChanged()
                }
//                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
//                hideLoading()
            }
        })

    }
}