package com.yogeshj.autoform.admin.users

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yogeshj.autoform.R
import com.yogeshj.autoform.admin.users.changeUserData.ChangeUploadFormUserDataActivity
import com.yogeshj.autoform.admin.users.changeUserData.ChangeUserDataActivity
import com.yogeshj.autoform.databinding.AdminUsersRvItemBinding


class AdminUsersAdapter(private var dataList: ArrayList<AdminUsersModel>, var context: Context) :
    RecyclerView.Adapter<AdminUsersAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: AdminUsersRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AdminUsersRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context).load(dataList[position].imageId).error(R.drawable.user_icon).into(holder.binding.profilePic)
        holder.binding.nameTxt.text = dataList[position].name
        holder.binding.emailTxt.text = dataList[position].email



        holder.itemView.setOnClickListener {
            if(dataList[position].isNormalUser)
            {
                val intent= Intent(context, ChangeUserDataActivity::class.java)
                intent.putExtra("name",dataList[position].name)
                intent.putExtra("email",dataList[position].email)
                intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            else{
                val intent= Intent(context, ChangeUploadFormUserDataActivity::class.java)
                intent.putExtra("email",dataList[position].email)
                intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }

        }
    }

}