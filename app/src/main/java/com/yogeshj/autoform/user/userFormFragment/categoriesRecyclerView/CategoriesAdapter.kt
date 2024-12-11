package com.yogeshj.autoform.user.userFormFragment.categoriesRecyclerView

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.user.userFormFragment.CardFormRecyclerView.CardFormModel
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.CategoriesRvItemBinding
import com.yogeshj.autoform.uploadForm.uploadNewFormFragment.FormDetails
import com.yogeshj.autoform.user.userFormFragment.UserFormFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private lateinit var dialog:Dialog

class CategoriesAdapter(private var dataList: ArrayList<CategoriesModel>, var context: Context) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: CategoriesRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CategoriesRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        initLoadingDialog()
        holder.binding.btn.text=dataList[position].category


        UserFormFragment.dataList.clear()

        holder.binding.btn.setOnClickListener {
            if(dataList[position].category=="All")
                addAllData()
            else
                addData(dataList[position].category)
        }

    }

    private fun addAllData() {
        showLoading()
        UserFormFragment.dataList.clear()
//        HomeScreenActivity.tempDataList.clear()
        val db = FirebaseDatabase.getInstance().getReference("UploadForm")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for(snap2 in snapshot.children) {
                        for (snap in snap2.children) {
                            val currentUser = snap.getValue(FormDetails::class.java)
                            if (currentUser != null) {
                                val deadlineStr = currentUser.deadline
                                val examDeadline = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(deadlineStr)

                                val currentDate = Calendar.getInstance().time

                                if (examDeadline != null && !examDeadline.before(currentDate)) {
                                    if (UserFormFragment.appliedFormNames.contains(currentUser.examName)) {
//                                    HomeScreenActivity.tempDataList.add(CardFormModel(currentUser.icon!!,currentUser.examName ?: "No exam name", currentUser.examHostName ?: "No host name",currentUser.deadline?:"DD/MM/YYYY",currentUser.examDate?:"DD/MM/YYYY", 0, currentUser.fees,currentUser.category!!,"Live"))
                                        continue
                                    }
                                    UserFormFragment.dataList.add(
                                        CardFormModel(
                                            currentUser.icon!!,
                                            currentUser.examName ?: "No exam name",
                                            currentUser.examHostName ?: "No host name",
                                            currentUser.deadline ?: "DD/MM/YYYY",
                                            currentUser.examDate ?: "DD/MM/YYYY",
                                            0,
                                            currentUser.fees,
                                            currentUser.category!!,
                                            currentUser.status!!
                                        )
                                    )
                                }
                                else{
                                    UserFormFragment.dataList.add(
                                        CardFormModel(
                                            currentUser.icon!!,
                                            currentUser.examName ?: "No exam name",
                                            currentUser.examHostName ?: "No host name",
                                            currentUser.deadline ?: "DD/MM/YYYY",
                                            currentUser.examDate ?: "DD/MM/YYYY",
                                            0,
                                            currentUser.fees ?: 0,
                                            currentUser.category!!,
                                            "Expired"
                                        )
                                    )
                                }
                            }
                        }
                    }
//                    HomeScreenActivity.dataList.addAll(HomeScreenActivity.tempDataList)
                    UserFormFragment.myAdapter.notifyDataSetChanged()
                    hideLoading()
                }
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })
    }

    private fun addData(category: Any) {
        showLoading()
        UserFormFragment.dataList.clear()
//        HomeScreenActivity.tempDataList.clear()
        val db = FirebaseDatabase.getInstance().getReference("UploadForm")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for(snap2 in snapshot.children)
                    {
                        for (snap in snap2.children) {
                            val currentUser = snap.getValue(FormDetails::class.java)
                            if (currentUser!=null && currentUser.category==category) {
                                val deadlineStr = currentUser.deadline
                                val examDeadline = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(deadlineStr)

                                val currentDate = Calendar.getInstance().time

                                if (examDeadline != null && !examDeadline.before(currentDate)) {
                                    if (UserFormFragment.appliedFormNames.contains(currentUser.examName)) {
//                                    HomeScreenActivity.tempDataList.add(CardFormModel(currentUser.icon!!,currentUser.examName ?: "No exam name", currentUser.examHostName ?: "No host name",currentUser.deadline?:"DD/MM/YYYY",currentUser.examDate?:"DD/MM/YYYY", 0, currentUser.fees,currentUser.category!!,"Live"))
                                        continue
                                    }
                                    UserFormFragment.dataList.add(
                                        CardFormModel(
                                            currentUser.icon!!,
                                            currentUser.examName ?: "No exam name",
                                            currentUser.examHostName ?: "No host name",
                                            currentUser.deadline ?: "DD/MM/YYYY",
                                            currentUser.examDate ?: "DD/MM/YYYY",
                                            0,
                                            currentUser.fees ?: 0,
                                            currentUser.category!!,
                                            currentUser.status!!
                                        )
                                    )
                                }
                            }
                        }
                    }
//                    HomeScreenActivity.dataList.addAll(HomeScreenActivity.tempDataList)

                    UserFormFragment.myAdapter.notifyDataSetChanged()
                    hideLoading()
                }
                hideLoading()
            }

            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })
    }

    private fun initLoadingDialog() {
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showLoading() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun hideLoading() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}