package com.yogeshj.autoform.uploadForm.viewForm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yogeshj.autoform.databinding.ViewFormRvItemBinding

class ViewFormsAdapter(private val dataList: ArrayList<ViewFormModel>,var context: Context) :
    RecyclerView.Adapter<ViewFormsAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: ViewFormRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ViewFormRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.fieldLabel.text = dataList[position].keyPair
        holder.binding.fieldValue.text = dataList[position].valuePair
    }
}