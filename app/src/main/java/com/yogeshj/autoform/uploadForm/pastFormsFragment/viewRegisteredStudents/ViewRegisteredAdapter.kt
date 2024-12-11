package com.yogeshj.autoform.uploadForm.pastFormsFragment.viewRegisteredStudents

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ViewRegisteredRvItemBinding

class ViewRegisteredAdapter(private val dataList: ArrayList<ViewRegisteredModel>, var context: Context) :
    RecyclerView.Adapter<ViewRegisteredAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: ViewRegisteredRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        var isExpanded = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ViewRegisteredRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.studentNameTextView.text=dataList[position].name
        Glide.with(context)
            .load(dataList[position].imageId)
            .placeholder(R.drawable.user_icon)
            .error(R.drawable.user_icon)
            .apply(RequestOptions.circleCropTransform()).into(holder.binding.profileImageView)
        val detailsText = dataList[position].details.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        holder.binding.detailsTextView.text = detailsText

        holder.binding.dropdownButton.setOnClickListener {
            holder.isExpanded = !holder.isExpanded
            holder.binding.detailsLayout.visibility = if (holder.isExpanded) View.VISIBLE else View.GONE
            holder.binding.dropdownButton.setImageResource(if (holder.isExpanded) R.drawable.subtract_icon else R.drawable.add_icon)
        }

    }
}