package com.yogeshj.autoform.admin.users

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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

    fun deleteItem(position:Int){
//        Log.d("DELETED","Delete account performed")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Are you sure you want to delete ${dataList[position].name}'s account?")

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
        builder.setView(input)

        builder.setPositiveButton("Delete User") { _, _ ->
            if(dataList[position].isNormalUser)
            {
//                val db=FirebaseDatabase.getInstance().getReference("Users").child(dataList[position].uid)
//                db.removeValue()
//                    .addOnCompleteListener {
//                        if (it.isSuccessful) {
//                            dataList.removeAt(position)
//                            notifyDataSetChanged()
//                            Toast.makeText(context, "User not deleted successfully.", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(context, "Error deleting user data: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
//                        }
//                    }
                notifyItemChanged(position)
            }


        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            notifyItemChanged(position)
            dialog.cancel()
        }

        builder.show()

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