package com.yogeshj.autoform.admin.forms

import android.app.Dialog
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
import com.yogeshj.autoform.databinding.FragmentFormsBinding
import com.yogeshj.autoform.uploadForm.uploadNewFormFragment.FormDetails
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FormsFragment : Fragment() {

    private lateinit var binding:FragmentFormsBinding

    private lateinit var myAdapter: AdminFormsAdapter
    private lateinit var dataList: ArrayList<AdminFormsModel>

    private lateinit var dialog:Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentFormsBinding.inflate(inflater,container,false)
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

        setRecyclerView()
    }


    private fun setRecyclerView() {

        dataList =ArrayList()
        myAdapter = AdminFormsAdapter(dataList, requireContext())
        binding.recycler.layoutManager=LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = myAdapter
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
                                val examDeadline = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(deadlineStr!!)
                                val currentDate = Calendar.getInstance().time

                                if (examDeadline != null && examDeadline.before(currentDate)) {
                                    if(currentExam.status=="Live" || currentExam.status=="Upcoming"){
                                        examSnap.ref.child("status").setValue("Expired")
                                    }
                                    dataList.add(AdminFormsModel(currentExam.icon!!, currentExam.examName?:"No exam name",currentExam.examHostName?:"No host name",currentExam.deadline?:"DD/MM/YYYY",currentExam.examDate?:"DD/MM/YYYY",0,currentExam.fees,currentExam.category!!,currentExam.status!!))
                                    continue
                                }
                                dataList.add(AdminFormsModel(currentExam.icon!!, currentExam.examName?:"No exam name",currentExam.examHostName?:"No host name",currentExam.deadline?:"DD/MM/YYYY",currentExam.examDate?:"DD/MM/YYYY",0,currentExam.fees,currentExam.category!!,currentExam.status?:"Not Set"))
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