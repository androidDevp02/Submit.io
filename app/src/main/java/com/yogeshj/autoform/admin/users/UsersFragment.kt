package com.yogeshj.autoform.admin.users

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import com.yogeshj.autoform.admin.requests.UploadFormSignUpModel
import com.yogeshj.autoform.databinding.FragmentUsersBinding

class UsersFragment : Fragment() {

    private lateinit var binding:FragmentUsersBinding

    private lateinit var myAdapter: AdminUsersAdapter
    private lateinit var dataList: ArrayList<AdminUsersModel>

    private lateinit var dialog:Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View {
        binding=FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLoadingDialog()
        showLoading()

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        MobileAds.initialize(requireContext())
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

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
        showLoading()
        dataList.clear()

        val db = FirebaseDatabase.getInstance().getReference("UploadFormRegisteredUsers")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {

                        val userInfo = snap.getValue(UploadFormSignUpModel::class.java)!!
                        val profilePicUrl = snap.child("profilePic").value?.toString()?: ""
                        dataList.add(AdminUsersModel(profilePicUrl,userInfo.headName.toString(),userInfo.loginMailId.toString(),false))
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



    private fun addNormalUsers() {
        showLoading()
        dataList.clear()

        val db = FirebaseDatabase.getInstance().getReference("UsersInfo")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {

                        val userInfo = snap.getValue(User::class.java)!!
                        val profilePicUrl = snap.child("profilePic").value?.toString()?: R.drawable.user_icon.toString()
                        dataList.add(AdminUsersModel(profilePicUrl,userInfo.name.toString(),userInfo.email.toString(),true))

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
        dialog = Dialog(requireContext())
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