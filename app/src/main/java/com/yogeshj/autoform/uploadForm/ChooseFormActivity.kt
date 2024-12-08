package com.yogeshj.autoform.uploadForm

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivityChooseFormBinding


class ChooseFormActivity : AppCompatActivity() {

    private lateinit var binding:ActivityChooseFormBinding
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChooseFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirstScreenActivity.auth=FirebaseAuth.getInstance()
        val exam=intent.getStringExtra("exam")
        val host=intent.getStringExtra("host")

        dbRef = FirebaseDatabase.getInstance().getReference("UploadForm")



        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation)
        binding.linkOption.startAnimation(fadeIn)
        binding.customFormOption.startAnimation(fadeIn)

        binding.linkOption.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Enter Link")

            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                val link = input.text.toString()
                if (link.isNotEmpty()) {
                    dbRef.child(FirstScreenActivity.auth.currentUser!!.uid).child(exam!!).child("link").setValue(link)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Link added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to add link. Please try again later!", Toast.LENGTH_SHORT).show()
                        }

                }
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }


        binding.customFormOption.setOnClickListener {
            val intent= Intent(this@ChooseFormActivity,FillApplicationFormDetailsActivity::class.java)
            intent.putExtra("exam",exam)
            intent.putExtra("host",host)
            startActivity(intent)
            finish()
        }

    }
}