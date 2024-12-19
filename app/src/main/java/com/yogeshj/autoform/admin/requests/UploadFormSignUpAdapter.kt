package com.yogeshj.autoform.admin.requests

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.databinding.RegistrationRvItemBinding


class UploadFormSignUpAdapter(private var dataList: ArrayList<UploadFormSignUpModel>, var context: Context) :
    RecyclerView.Adapter<UploadFormSignUpAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: RegistrationRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RegistrationRvItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvInstituteName.text=dataList[position].instituteName
        holder.binding.tvHeadName.text=dataList[position].headName
        holder.binding.tvAddress.text=dataList[position].instituteAddress
        holder.binding.tvPhone.text=dataList[position].instituteContact
        holder.binding.tvInstituteMail.text=dataList[position].instituteMailId
        holder.binding.tvLoginMail.text=dataList[position].loginMailId
        holder.binding.tvWebsiteLink.text=dataList[position].websitelink


        holder.binding.btnVerify.setOnClickListener {
            val uniqueKey=dataList[position].key
            if (uniqueKey != null) {
                FirstScreenActivity.auth.createUserWithEmailAndPassword(dataList[position].loginMailId!!,"Temporary@123")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = task.result?.user?.uid
                            if (uid != null) {
                                addUserToDatabase(dataList[position].instituteName!!,dataList[position].loginMailId!!,uid)
                                val registeredRef = FirebaseDatabase.getInstance().getReference("UploadFormRegisteredUsers")
                                registeredRef.child(uniqueKey).setValue(dataList[position])
                                    .addOnSuccessListener {
                                        FirstScreenActivity.auth.currentUser?.sendEmailVerification()
                                        val registrationRef = FirebaseDatabase.getInstance().getReference("UploadFormRegistrationUsers")
                                        registrationRef.child(uniqueKey).removeValue()
                                            .addOnSuccessListener {
                                                FirstScreenActivity.auth.sendPasswordResetEmail(dataList[position].loginMailId!!)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(context,"Verification complete. Password reset email sent to ${dataList[position].loginMailId}.", Toast.LENGTH_LONG).show()
                                                        dataList.removeAt(position)
                                                        notifyItemRemoved(position)
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(
                                                            context,"Failed to send reset email. Try again later.",Toast.LENGTH_LONG).show()
                                                    }
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context,"Error removing registration entry.",Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context,"Error saving to registered users.",Toast.LENGTH_SHORT).show()
                                    }
                            }
                            else {
                                Toast.makeText(context,"Error: UID is null.",Toast.LENGTH_SHORT).show()
                            }
                        }else {
                            Toast.makeText(context,"Error creating user: ${task.exception?.message}",Toast.LENGTH_LONG).show()
                        }
                    }
            }
            else {
                Toast.makeText(context,"Error: Unique key is null.",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun addUserToDatabase(name: String, email: String, uid: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("UploadFormUsers")
        dbRef.child(uid).setValue(User(uid, name, email))
    }
}
