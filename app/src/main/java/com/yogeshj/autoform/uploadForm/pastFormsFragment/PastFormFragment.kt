package com.yogeshj.autoform.uploadForm.pastFormsFragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.yogeshj.autoform.databinding.FragmentPastFormBinding
import com.yogeshj.autoform.uploadForm.uploadNewFormFragment.FormDetails


class PastFormFragment : Fragment() {

    private lateinit var binding:FragmentPastFormBinding

    private lateinit var myAdapter: YourFormsAdapter
    private lateinit var dataList: ArrayList<YourFormsModel>

    private lateinit var dialog:Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentPastFormBinding.inflate(inflater,container,false)
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

//        binding.navBar.apply { alpha = 0f; translationY = -30f }
        binding.recyclerForms.apply { alpha = 0f; translationY = 20f }

//        binding.navBar.animate().alpha(1f).translationY(0f).setDuration(1000).start()
        binding.recyclerForms.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()

        dataList=ArrayList()
        binding.recyclerForms.layoutManager=LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        myAdapter= YourFormsAdapter(dataList,requireContext())
        binding.recyclerForms.adapter = myAdapter
        val db = FirebaseDatabase.getInstance().getReference("UploadForm")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for(snap2 in snapshot.children) {
                        for (snap in snap2.children) {
                            val currentUser = snap.getValue(FormDetails::class.java)!!
                            if (currentUser.uid == FirstScreenActivity.auth.currentUser!!.uid) {
                                dataList.add(YourFormsModel(currentUser.uid, currentUser.icon, currentUser.examName, currentUser.examHostName))
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