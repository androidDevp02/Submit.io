package com.yogeshj.autoform.uploadForm

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.databinding.ActivityFillApplicationFormDetailsBinding
import java.util.ArrayList

class FillApplicationFormDetailsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityFillApplicationFormDetailsBinding
    private lateinit var dbRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFillApplicationFormDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        dbRef = FirebaseDatabase.getInstance().getReference("UploadForm")
        binding.addButton.setOnClickListener {
            val editText=EditText(this)
            editText.hint="Enter Details"
            editText.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
            binding.formContainer.addView(editText)

        }


        binding.subtractButton.setOnClickListener{
            val childCount=binding.formContainer.childCount
            if (childCount>0) {
                binding.formContainer.removeViewAt(childCount-1)
            }
        }
        binding.done.setOnClickListener {
            val formData = ArrayList<String>()

            for (i in 0 until binding.formContainer.childCount) {
                val editText = binding.formContainer.getChildAt(i) as EditText
                val input = editText.text.toString()

                if (input.isNotEmpty()) {
                    formData.add(input)
                }
            }

            dbRef.child(FirstScreenActivity.auth.currentUser!!.uid).child(intent.getStringExtra("exam")!!).child("Application Form Details").setValue(formData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Form submitted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to submit form", Toast.LENGTH_SHORT).show()
                }

        }
    }
}