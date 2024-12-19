package com.yogeshj.autoform.user.userFormFragment.recommendatioSeeAll

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.FirebaseDatabase
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.RecommendationSeeAllRvItemBinding
import com.yogeshj.autoform.uploadForm.ViewLinkRegistered.ViewLinkRegisteredModel
import com.yogeshj.autoform.user.UserMainActivity
import com.yogeshj.autoform.user.userFormFragment.examApply.ExamDetailsActivity
import com.yogeshj.autoform.user.userFormFragment.recommendationRecyclerView.RecommendationCardFormModel

class RecommendationSeeAllAdapter(private var dataList: ArrayList<RecommendationSeeAllModel>, var context: Context) :
    RecyclerView.Adapter<RecommendationSeeAllAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: RecommendationSeeAllRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RecommendationSeeAllRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
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
            holder.binding.tentativeDate.visibility= View.GONE
            holder.binding.tentativeDeadline.visibility= View.GONE
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
            if(holder.binding.markAsAppliedButton.text=="Mark Applied" && holder.binding.statusText.text=="Live"){
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Are you sure you want to mark this form as applied? This action can't be undone!")

                builder.setPositiveButton("Yes") { _, _ ->
                    val dbRef = FirebaseDatabase.getInstance().getReference("LinkApplied")
                    dbRef.child(FirstScreenActivity.auth.currentUser!!.uid).child(dataList[position].examName).setValue(
                        ViewLinkRegisteredModel(dataList[position].imageId,dataList[position].examName,dataList[position].examHost)
                    )
//                    holder.binding.markAsAppliedButton.text="Applied"
//                    holder.binding.markAsAppliedButton.isEnabled = false
                    val intent= Intent(context, UserMainActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                builder.show()
            }
            else if(holder.binding.markAsAppliedButton.text!="Mark Applied"){
                Toast.makeText(context,"The form is already marked as filled!", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(context,"Please wait for the form to come live!", Toast.LENGTH_LONG).show()
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