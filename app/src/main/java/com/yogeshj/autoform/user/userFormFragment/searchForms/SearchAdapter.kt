package com.yogeshj.autoform.user.userFormFragment.searchForms

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yogeshj.autoform.user.userFormFragment.CardFormRecyclerView.CardFormModel
import com.yogeshj.autoform.databinding.SearchRvBinding
import com.yogeshj.autoform.user.userFormFragment.examApply.ExamDetailsActivity

class SearchAdapter(private var dataList: ArrayList<CardFormModel>, var context: Context) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: SearchRvBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SearchRvBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context).load(dataList[position].imageId).into(holder.binding.img)
        holder.binding.searchTxt.text = dataList[position].examName



        holder.itemView.setOnClickListener {
            val intent=Intent(context, ExamDetailsActivity::class.java)
            intent.putExtra("heading",dataList[position].examName)
            intent.putExtra("subheading",dataList[position].examHost)
            intent.putExtra("registered",dataList[position].registered)
            intent.putExtra("fees",dataList[position].fees)
            intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterList: ArrayList<CardFormModel>) {
        dataList = filterList
        notifyDataSetChanged()
    }

}