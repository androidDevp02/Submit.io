package com.yogeshj.autoform.admin.forms

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yogeshj.autoform.R
import com.yogeshj.autoform.admin.forms.changeFormDetails.ChangeFormDetailsActivity
import com.yogeshj.autoform.databinding.AdminFormsRvItemBinding
import com.yogeshj.autoform.uploadForm.pastFormsFragment.viewRegisteredStudents.ViewRegisteredActivity


class AdminFormsAdapter(private var dataList: ArrayList<AdminFormsModel>, var context: Context) :
    RecyclerView.Adapter<AdminFormsAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: AdminFormsRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AdminFormsRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
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
        when (dataList[position].status) {
            "Live" -> {
                holder.binding.statusIcon.setImageResource(R.drawable.live_icon)
                holder.binding.statusText.text= context.getString(R.string.live_txt)
                holder.binding.tentativeDate.visibility= View.GONE
                holder.binding.tentativeDeadline.visibility= View.GONE
            }
            "Upcoming" -> {
                holder.binding.statusIcon.setImageResource(R.drawable.upcoming_icon)
                holder.binding.statusText.text= context.getString(R.string.upcoming_txt)
                holder.binding.tentativeDate.visibility= View.VISIBLE
                holder.binding.tentativeDeadline.visibility= View.VISIBLE
            }
            "Expired" -> {
                holder.binding.statusIcon.setImageResource(R.drawable.expired_icon)
                holder.binding.statusText.text= context.getString(R.string.expired_txt)
                holder.binding.tentativeDate.visibility= View.GONE
                holder.binding.tentativeDeadline.visibility= View.GONE
            }
        }

        holder.binding.feesInfo.text="${dataList[position].fees}"

        holder.binding.viewFormButton.setOnClickListener {
            val intent= Intent(context, ChangeFormDetailsActivity::class.java)
            intent.putExtra("examName",dataList[position].examName)
            intent.putExtra("examHostName",dataList[position].examHost)
            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        holder.binding.markAsAppliedButton.setOnClickListener {
            val intent= Intent(context, ViewRegisteredActivity::class.java)
            intent.putExtra("heading",dataList[position].examName)
//            intent.putExtra("examHostName",dataList[position].examHost)
            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}



