package com.yogeshj.autoform.user.userFormFragment.profile

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yogeshj.autoform.R

class ProfileOtherDetailsAdapter(private val firebaseData: MutableMap<String, String>) : RecyclerView.Adapter<ProfileOtherDetailsAdapter.DetailViewHolder>() {

    private val updatedData: MutableMap<String, String> = firebaseData.toMutableMap()

    inner class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textInputLayout: TextInputLayout = itemView.findViewById(R.id.text_input_layout)
        val textInputEditText: TextInputEditText = itemView.findViewById(R.id.text_input_edit_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.other_details_rv_item, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val key = firebaseData.keys.toList()[position]
        val value = firebaseData[key]

        holder.textInputEditText.setText(value)
        holder.textInputLayout.hint = key


        holder.textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updatedData[key] = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun getUpdatedData(): Map<String, String> {
        return updatedData
    }

    override fun getItemCount(): Int {
        return firebaseData.size
    }


}
