package com.yogeshj.autoform.uploadForm.ViewLinkRegistered

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.AppliedFormRvItemBinding


class ViewLinkRegisteredAdapter(private val dataList: ArrayList<ViewLinkRegisteredModel>, var context: Context) :
    RecyclerView.Adapter<ViewLinkRegisteredAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: AppliedFormRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AppliedFormRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.examHeading.text=dataList[position].name
        holder.binding.subHeading.text=dataList[position].host
        Glide.with(context)
            .load(dataList[position].imageId)
            .placeholder(R.drawable.user_icon)
            .error(R.drawable.user_icon)
            .apply(RequestOptions.circleCropTransform()).into(holder.binding.logoImg)


    }
}