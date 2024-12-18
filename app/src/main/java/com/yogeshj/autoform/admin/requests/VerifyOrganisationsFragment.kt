package com.yogeshj.autoform.admin.requests

import android.app.Dialog
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
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
import com.yogeshj.autoform.splashScreenAndIntroScreen.IntroActivity
import com.yogeshj.autoform.databinding.FragmentVerifyOrganisationsBinding

class VerifyOrganisationsFragment : Fragment() {

    private lateinit var binding:FragmentVerifyOrganisationsBinding

    lateinit var myAdapter: UploadFormSignUpAdapter
    lateinit var dataList: ArrayList<UploadFormSignUpModel>

    private lateinit var dialog:Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentVerifyOrganisationsBinding.inflate(inflater,container,false)
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

        binding.logout.setOnClickListener {
            showLoading()
            FirstScreenActivity.auth.signOut()
            TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(
                    Intent(context, FirstScreenActivity::class.java))
                .addNextIntent(Intent(context, IntroActivity::class.java))
                .startActivities()
            hideLoading()
        }

        dataList =ArrayList()
        myAdapter = UploadFormSignUpAdapter(dataList,requireContext())
        binding.rvRegistrationList.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvRegistrationList.adapter = myAdapter
        val db = FirebaseDatabase.getInstance().getReference("UploadFormRegistrationUsers")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val curr=snap.getValue(UploadFormSignUpModel::class.java)
                        if (curr!=null) {
                            dataList.add(UploadFormSignUpModel(curr.websitelink,curr.instituteName,curr.headName,curr.instituteAddress,curr.instituteContact,curr.instituteMailId,curr.loginMailId,curr.key))

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