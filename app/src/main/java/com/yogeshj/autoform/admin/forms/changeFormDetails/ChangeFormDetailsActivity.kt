package com.yogeshj.autoform.admin.forms.changeFormDetails

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivityChangeFormDetailsBinding
import com.yogeshj.autoform.uploadForm.uploadNewFormFragment.FormDetails
import java.util.Calendar

class ChangeFormDetailsActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener {

    private lateinit var binding:ActivityChangeFormDetailsBinding

    override fun onResume() {
        super.onResume()
        val lang=resources.getStringArray(R.array.category)
        val arrayAdapter= ArrayAdapter(this@ChangeFormDetailsActivity,R.layout.dropdown_item,lang)
        binding.categoryEditText.setAdapter(arrayAdapter)

        val status=resources.getStringArray(R.array.status)
        val arrayAdapter2= ArrayAdapter(this@ChangeFormDetailsActivity,R.layout.dropdown_item,status)
        binding.statusEditText.setAdapter(arrayAdapter2)
    }

    private var day=0
    private var month=0
    private var year=0


    //    companion object{
    private var savedDay=0
    private var savedMonth=0
    private var savedYear=0
    //    }
    private lateinit var currentEditText: TextInputEditText

    private fun getDateTime(){
        val cal= Calendar.getInstance()
        day=cal.get(Calendar.DAY_OF_MONTH)
        month=cal.get(Calendar.MONTH)
        year=cal.get(Calendar.YEAR)
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedDay=p3
        savedMonth=p2
        savedYear=p1
        val currentCal = Calendar.getInstance()
        val selectedCal = Calendar.getInstance()

        selectedCal.set(savedYear, savedMonth, savedDay)

        if (selectedCal >= currentCal) {
            currentEditText.setText("$savedDay/${savedMonth + 1}/$savedYear")
        } else {
            Toast.makeText(this@ChangeFormDetailsActivity, "Please select a future date", Toast.LENGTH_SHORT).show()
            DatePickerDialog(this@ChangeFormDetailsActivity, this, year, month, day).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChangeFormDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        FirstScreenActivity.auth=FirebaseAuth.getInstance()

        val examName=intent.getStringExtra("examName")?:""
        val examHostName=intent.getStringExtra("examHostName")?:""

        binding.examDateEditText.setOnClickListener {
            currentEditText=binding.examDateEditText
            getDateTime()
            DatePickerDialog(this@ChangeFormDetailsActivity,this,year,month,day).show()
        }

        binding.deadlineEditText.setOnClickListener {
            currentEditText=binding.deadlineEditText
            getDateTime()
            DatePickerDialog(this@ChangeFormDetailsActivity,this,year,month,day).show()
        }

        val db = FirebaseDatabase.getInstance().getReference("UploadForm")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        for(examSnap in snap.children)
                        {
                            val currentExam = examSnap.getValue(FormDetails::class.java)
                            if (currentExam!=null && currentExam.examName==examName && currentExam.examHostName==examHostName) {
                                binding.examNameEditText.setText(currentExam.examName)
                                binding.examHostNameEditText.setText(currentExam.examHostName)
                                binding.categoryEditText.setText(currentExam.category)
                                binding.statusEditText.setText(currentExam.status)
                                binding.examDateEditText.setText(currentExam.examDate)
                                binding.deadlineEditText.setText(currentExam.deadline)
                                binding.examDescriptionEditText.setText(currentExam.examDescription)
                                binding.eligibilityEditText.setText(currentExam.eligibility)
                                binding.feesEditText.setText(currentExam.fees.toString())
                                Glide.with(this@ChangeFormDetailsActivity)
                                    .load(currentExam.icon)
                                    .placeholder(R.drawable.ic_form)
                                    .error(R.drawable.ic_form)
                                    .into(binding.uploadedImageView)
                            }
                        }


                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.back.setOnClickListener {

            if(binding.examNameEditText.text.toString().isEmpty() || binding.examHostNameEditText.text.toString().isEmpty() || binding.categoryEditText.text.toString().isEmpty() || binding.examDateEditText.text.toString().isEmpty() || binding.deadlineEditText.text.toString().isEmpty() || binding.examDescriptionEditText.text.toString().isEmpty() || binding.eligibilityEditText.text.toString().isEmpty() || binding.uploadedImageView.toString().isEmpty() || binding.statusEditText.text.toString().isEmpty())
            {
                Toast.makeText(this@ChangeFormDetailsActivity,"All fields are mandatory to proceed further!", Toast.LENGTH_LONG).show()
            }
            else{
//                    Toast.makeText(this@ChangeFormDetailsActivity,"Back pressed", Toast.LENGTH_LONG).show()

                val db2 = FirebaseDatabase.getInstance().getReference("UploadForm")
                db2.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (snap in snapshot.children) {
                                for(examSnap in snap.children)
                                {
                                    val currentExam = examSnap.getValue(FormDetails::class.java)
                                    if (currentExam!=null && currentExam.examName==examName && currentExam.examHostName==examHostName) {

                                        val updatedFields = mapOf(
                                            "examHostName" to binding.examHostNameEditText.text.toString(),
                                            "category" to binding.categoryEditText.text.toString(),
                                            "examDate" to binding.examDateEditText.text.toString(),
                                            "deadline" to binding.deadlineEditText.text.toString(),
                                            "examDescription" to binding.examDescriptionEditText.text.toString(),
                                            "eligibility" to binding.eligibilityEditText.text.toString(),
                                            "fees" to binding.feesEditText.text.toString().toInt(),
                                            "status" to binding.statusEditText.text.toString()
                                        )
                                        val examRef=examSnap.ref
                                        examRef.updateChildren(updatedFields).addOnSuccessListener {
                                            Toast.makeText(this@ChangeFormDetailsActivity,"Details saved successfully",Toast.LENGTH_LONG).show()
                                            finish()
                                        }.addOnFailureListener {
                                            Toast.makeText(this@ChangeFormDetailsActivity,"Failed to save details",Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }


                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })

            }
        }
    }
}