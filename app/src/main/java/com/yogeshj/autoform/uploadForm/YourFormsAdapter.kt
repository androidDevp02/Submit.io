package com.yogeshj.autoform.uploadForm

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.AppliedFormRvItemBinding
import com.yogeshj.autoform.uploadForm.viewForm.ViewFormsActivity
import com.yogeshj.autoform.uploadForm.viewRegisteredStudents.ViewRegisteredActivity

class YourFormsAdapter(private var dataList: ArrayList<YourFormsModel>, var context: Context) :
    RecyclerView.Adapter<YourFormsAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: AppliedFormRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AppliedFormRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
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
            .apply(RequestOptions.circleCropTransform()).into(holder.binding.logoImg)

        holder.binding.examHeading.text=dataList[position].examName
        holder.binding.subHeading.text=dataList[position].examHost

        holder.binding.viewFormButton.setOnClickListener {
            val intent= Intent(context, ViewFormsActivity::class.java)
            intent.putExtra("heading",dataList[position].examName)
            intent.putExtra("subheading",dataList[position].examHost)
            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
        holder.binding.viewStudentsButton.setOnClickListener {
            val intent= Intent(context, ViewRegisteredActivity::class.java)
            intent.putExtra("heading",dataList[position].examName)
            intent.putExtra("subheading",dataList[position].examHost)
            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

}