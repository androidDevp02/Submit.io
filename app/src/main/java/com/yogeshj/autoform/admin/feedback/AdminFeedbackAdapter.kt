package com.yogeshj.autoform.admin.feedback

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yogeshj.autoform.FeedbackFragment.FeedbackModel
import com.yogeshj.autoform.databinding.FeedbackAdminRvItemBinding

class AdminFeedbackAdapter(private var dataList: ArrayList<FeedbackModel>, var context: Context) : RecyclerView.Adapter<AdminFeedbackAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: FeedbackAdminRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = FeedbackAdminRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvFeedback.text=dataList[position].feedback
        holder.binding.tvEmail.text=dataList[position].email

//        holder.binding.btnResolve.setOnClickListener {
//            val intent=Intent(context, ExamDetailsActivity::class.java)
//            intent.putExtra("heading",dataList[position].examName)
//            intent.putExtra("subheading",dataList[position].examHost)
//            intent.putExtra("registered",dataList[position].registered)
//            intent.putExtra("fees",dataList[position].fees)
//            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
//            context.startActivity(intent)
//        }
    }
}