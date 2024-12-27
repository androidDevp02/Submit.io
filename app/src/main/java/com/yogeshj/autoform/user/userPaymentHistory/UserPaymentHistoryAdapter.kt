package com.yogeshj.autoform.user.userPaymentHistory

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.PaymentHistoryRvItemBinding
import com.yogeshj.autoform.user.userAppliedFormFragments.ViewAppliedFormsModel

class UserPaymentHistoryAdapter(private var dataList: ArrayList<UserPaymentHistoryModel>, var context: Context) :
    RecyclerView.Adapter<UserPaymentHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: PaymentHistoryRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = PaymentHistoryRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
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
        holder.binding.paymentAmount.text=dataList[position].amount.toString()
        holder.binding.paymentDate.text=dataList[position].paymentDate
        holder.binding.paymentTime.text=dataList[position].paymentTime

        holder.binding.viewFormDetails.setOnClickListener {
            val intent=Intent(context,UserPaidFormDetailsActivity::class.java)
            intent.putExtra("heading",dataList[position].name)
            intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

    }

}