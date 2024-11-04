package com.yogeshj.autoform.user.viewAppliedForms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.AppliedFormRvItemBinding
import com.yogeshj.autoform.uploadForm.YourFormsModel

class ViewAppliedFormsAdapter(private var dataList: ArrayList<ViewAppliedFormsModel>, var context: Context) :
    RecyclerView.Adapter<ViewAppliedFormsAdapter.ViewHolder>() {

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
            .load(dataList[position].icon)
            .placeholder(R.drawable.user_icon)
            .error(R.drawable.user_icon)
            .apply(RequestOptions.circleCropTransform()).into(holder.binding.logoImg)

        holder.binding.examHeading.text=dataList[position].name
        holder.binding.subHeading.text=dataList[position].host

        holder.binding.viewStudentsButton.visibility=View.GONE
        holder.binding.viewFormButton.visibility=View.GONE
    }

}