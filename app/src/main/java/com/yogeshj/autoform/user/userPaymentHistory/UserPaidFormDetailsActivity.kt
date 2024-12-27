package com.yogeshj.autoform.user.userPaymentHistory

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivityUserPaidFormDetailsBinding

class UserPaidFormDetailsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUserPaidFormDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityUserPaidFormDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        FirstScreenActivity.auth=FirebaseAuth.getInstance()

        binding.back.setOnClickListener {
            finish()
        }

        val db = FirebaseDatabase.getInstance().getReference("Payment")
        val targetExamName = intent.getStringExtra("heading")

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (idSnapshot in dataSnapshot.children) {
                    val examName = idSnapshot.child("examName").getValue(String::class.java)
                    if (targetExamName == examName) {
                        val applicationDetailsSnapshot = idSnapshot.child("ApplicationFormDetails")

                        for (detail in applicationDetailsSnapshot.children) {
                            val key = detail.key
                            val value = detail.getValue(String::class.java)

                            Log.d("KEY_VAL", "$key $value")

                            val textInputLayout = TextInputLayout(this@UserPaidFormDetailsActivity).apply {
                                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                hint = key?.replaceFirstChar { it.uppercase() }
                            }
                            val editText = TextInputEditText(textInputLayout.context).apply {
                                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                setText(value)
                                textSize=16f
                                isFocusable = false
                                isClickable = false
                                inputType = android.text.InputType.TYPE_NULL
                            }
                            textInputLayout.addView(editText)
                            binding.editTextContainer.addView(textInputLayout)
                        }
                        break
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@UserPaidFormDetailsActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })




    }
}