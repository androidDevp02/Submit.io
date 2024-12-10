package com.yogeshj.autoform.admin.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.R
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.admin.requests.UploadFormSignUpModel
import com.yogeshj.autoform.databinding.FragmentUsersBinding

class UsersFragment : Fragment() {

    private lateinit var binding:FragmentUsersBinding

    private lateinit var myAdapter: AdminUsersAdapter
    private lateinit var dataList: ArrayList<AdminUsersModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View {
        binding=FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataList=ArrayList()
        binding.recycler.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        myAdapter= AdminUsersAdapter(dataList,requireContext())
        binding.recycler.adapter = myAdapter

        addNormalUsers()

        binding.normalUsersBtn.setOnClickListener {
            addNormalUsers()
        }

        binding.uploadFormUsersBtn.setOnClickListener {
            addUploadFormUsers()
        }

    }

    private fun addUploadFormUsers() {
        dataList.clear()


        val db = FirebaseDatabase.getInstance().getReference("UploadFormRegisteredUsers")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {

                        val userInfo = snap.getValue(UploadFormSignUpModel::class.java)!!
                        val profilePicUrl = snap.child("profilePic").value?.toString()?: ""
                        dataList.add(AdminUsersModel(profilePicUrl,userInfo.headName.toString(),userInfo.instituteMailId.toString()))

                    }
                    myAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }



    private fun addNormalUsers() {
        dataList.clear()


        val db = FirebaseDatabase.getInstance().getReference("UsersInfo")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {

                        val userInfo = snap.getValue(User::class.java)!!
                        val profilePicUrl = snap.child("profilePic").value?.toString()?: R.drawable.user_icon.toString()
                        dataList.add(AdminUsersModel(profilePicUrl,userInfo.name.toString(),userInfo.email.toString()))

                    }
                    myAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}