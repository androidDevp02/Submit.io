//package com.yogeshj.autoform.CardFormRecyclerView
//
//import android.app.AlertDialog
//import android.content.Context
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.RequestOptions
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.yogeshj.autoform.CardFormRecyclerView.CardFormModel
//import com.yogeshj.autoform.FirstScreenActivity
//import com.yogeshj.autoform.R
//import com.yogeshj.autoform.databinding.CardFormRvItemBinding
//import com.yogeshj.autoform.user.ExamDetailsActivity
//import com.yogeshj.autoform.user.HomeScreenActivity
//import com.yogeshj.autoform.user.viewAppliedForms.ViewAppliedFormsModel
//
//class CardFormAdapter(
//    private var dataList: ArrayList<CardFormModel>,
//    private val context: Context
//) : RecyclerView.Adapter<CardFormAdapter.ViewHolder>() {
//
//    inner class ViewHolder(val binding: CardFormRvItemBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = CardFormRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = dataList.size
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = dataList[position]
//        val currentUserId = FirstScreenActivity.auth.currentUser?.uid ?: return
//
//        // Fetch application status from Firebase
//        fetchApplicationStatus(currentUserId, item.examName) { isApplied ->
//            holder.binding.markAsAppliedButton.apply {
//                text = if (isApplied) "Applied" else "Mark as Applied"
//                isEnabled = !isApplied
//            }
//        }
//
//        // Load image using Glide
//        Glide.with(context)
//            .load(item.imageId)
//            .placeholder(R.drawable.user_icon)
//            .error(R.drawable.user_icon)
//            .apply(RequestOptions().centerCrop().circleCrop())
//            .into(holder.binding.logoImg)
//
//        // Bind other text and icons based on item properties
//        holder.binding.apply {
//            examHeading.text = item.examName
//            subHeading.text = item.examHost
//            deadlineInfo.text = item.deadline
//            examDateInfo.text = item.examDate
//            categoryInfo.text = item.category
//            feesInfo.text = "${item.fees}"
//
//            // Configure status-based icons and visibility
//            when (item.status) {
//                "Live" -> {
//                    statusIcon.setImageResource(R.drawable.live_icon)
//                    statusText.text = "Live"
//                    tentativeDate.visibility = View.GONE
//                    tentativeDeadline.visibility = View.GONE
//                }
//                "Upcoming" -> {
//                    statusIcon.setImageResource(R.drawable.upcoming_icon)
//                    statusText.text = "Upcoming"
//                    tentativeDate.visibility = View.VISIBLE
//                    tentativeDeadline.visibility = View.VISIBLE
//                }
//                "Expired" -> {
//                    statusIcon.setImageResource(R.drawable.expired_icon)
//                    statusText.text = "Expired"
//                    tentativeDate.visibility = View.GONE
//                    tentativeDeadline.visibility = View.GONE
//                }
//            }
//
//            // Set up "Mark as Applied" button behavior
//            markAsAppliedButton.setOnClickListener {
//                handleMarkAsApplied(holder, item, currentUserId)
//            }
//
//            // Set up "View Form" button behavior
//            viewFormButton.setOnClickListener {
//                openExamDetails(item)
//            }
//        }
//    }
//
//    private fun fetchApplicationStatus(userId: String, examName: String, callback: (Boolean) -> Unit) {
//        val dbRef = FirebaseDatabase.getInstance().getReference("LinkApplied")
//        dbRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val isApplied = snapshot.children.any { it.getValue(ViewAppliedFormsModel::class.java)?.name == examName }
//                callback(isApplied)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                callback(false)
//            }
//        })
//    }
//
//    private fun handleMarkAsApplied(holder: ViewHolder, item: CardFormModel, userId: String) {
//        val currentStatus = holder.binding.statusText.text.toString()
//
//        when {
//            currentStatus == "Live" && holder.binding.markAsAppliedButton.text == "Mark as Applied" -> {
//                AlertDialog.Builder(context).apply {
//                    setTitle("Confirm Application")
//                    setMessage("Are you sure you want to mark this form as applied? This action can't be undone!")
//                    setPositiveButton("Yes") { _, _ ->
//                        markFormAsApplied(item, userId)
//                        holder.binding.markAsAppliedButton.text = "Applied"
//                        holder.binding.markAsAppliedButton.isEnabled = false
//
//                        context.startActivity(Intent(context, HomeScreenActivity::class.java).apply {
//                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        })
//                    }
//                    setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
//                    show()
//                }
//            }
//            holder.binding.markAsAppliedButton.text != "Mark as Applied" -> {
//                Toast.makeText(context, "The form is already marked as applied!", Toast.LENGTH_LONG).show()
//            }
//            else -> {
//                Toast.makeText(context, "Please wait for the form to come live!", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    private fun markFormAsApplied(item: CardFormModel, userId: String) {
//        val dbRef = FirebaseDatabase.getInstance().getReference("LinkApplied")
//        dbRef.child(userId).child(item.examName).setValue(
//            ViewAppliedFormsModel(item.imageId, item.examName, item.examHost)
//        )
//    }
//
//    private fun openExamDetails(item: CardFormModel) {
//        val intent = Intent(context, ExamDetailsActivity::class.java).apply {
//            putExtra("heading", item.examName)
//            putExtra("subheading", item.examHost)
//            putExtra("registered", item.registered)
//            putExtra("fees", item.fees)
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        }
//        context.startActivity(intent)
//    }
//}
//
//
//
//











package com.yogeshj.autoform.CardFormRecyclerView

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.user.ExamDetailsActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.CardFormRvItemBinding
import com.yogeshj.autoform.uploadForm.FormDetails
import com.yogeshj.autoform.uploadForm.ViewLinkRegistered.ViewLinkRegisteredModel
import com.yogeshj.autoform.uploadForm.viewRegisteredStudents.ViewRegisteredModel
import com.yogeshj.autoform.user.HomeScreenActivity
import com.yogeshj.autoform.user.viewAppliedForms.ViewAppliedFormsModel

class CardFormAdapter(private var dataList: ArrayList<CardFormModel>, var context: Context) :
    RecyclerView.Adapter<CardFormAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: CardFormRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CardFormRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(dataList[position].imageId)
            .placeholder(R.drawable.user_icon)
            .error(R.drawable.user_icon)
            .apply(RequestOptions().centerCrop().circleCrop())
            .into(holder.binding.logoImg)

        holder.binding.examHeading.text=dataList[position].examName
        holder.binding.subHeading.text=dataList[position].examHost
        holder.binding.deadlineInfo.text=dataList[position].deadline
        holder.binding.examDateInfo.text=dataList[position].examDate
        holder.binding.categoryInfo.text=dataList[position].category
        if(dataList[position].status=="Live") {
            holder.binding.statusIcon.setImageResource(R.drawable.live_icon)
            holder.binding.statusText.text="Live"
            holder.binding.tentativeDate.visibility=View.GONE
            holder.binding.tentativeDeadline.visibility=View.GONE
        }
        else if(dataList[position].status=="Upcoming") {
            holder.binding.statusIcon.setImageResource(R.drawable.upcoming_icon)
            holder.binding.statusText.text="Upcoming"
            holder.binding.tentativeDate.visibility=View.VISIBLE
            holder.binding.tentativeDeadline.visibility=View.VISIBLE
        }
        else if(dataList[position].status=="Expired") {
            holder.binding.statusIcon.setImageResource(R.drawable.expired_icon)
            holder.binding.statusText.text="Expired"
            holder.binding.tentativeDate.visibility=View.GONE
            holder.binding.tentativeDeadline.visibility=View.GONE
        }
        holder.binding.feesInfo.text="${dataList[position].fees}"
        holder.binding.markAsAppliedButton.setOnClickListener {
            if(holder.binding.markAsAppliedButton.text=="Mark as Applied" && holder.binding.statusText.text=="Live"){
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Are you sure you want to mark this form as applied? This action can't be undone!")

                builder.setPositiveButton("Yes") { dialog, which ->
                    val dbRef = FirebaseDatabase.getInstance().getReference("LinkApplied")
                    dbRef.child(FirstScreenActivity.auth.currentUser!!.uid).child(dataList[position].examName).setValue(
                        ViewLinkRegisteredModel(dataList[position].imageId,dataList[position].examName,dataList[position].examHost)
                    )
//                    holder.binding.markAsAppliedButton.text="Applied"
//                    holder.binding.markAsAppliedButton.isEnabled = false
                    val intent=Intent(context,HomeScreenActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
                builder.setNegativeButton("Cancel") { dialog, which ->
                    dialog.cancel()
                }
                builder.show()
            }
            else if(holder.binding.markAsAppliedButton.text!="Mark as Applied"){
                Toast.makeText(context,"The form is already marked as filled!", Toast.LENGTH_LONG).show()
            }
            else if(holder.binding.statusText.text=="Upcoming"){
                Toast.makeText(context,"Please wait for the form to come live!", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(context,"The form is already expired!", Toast.LENGTH_LONG).show()
            }
        }


        holder.binding.viewFormButton.setOnClickListener {
            val intent=Intent(context, ExamDetailsActivity::class.java)
            intent.putExtra("heading",dataList[position].examName)
            intent.putExtra("subheading",dataList[position].examHost)
            intent.putExtra("registered",dataList[position].registered)
            intent.putExtra("fees",dataList[position].fees)
            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
