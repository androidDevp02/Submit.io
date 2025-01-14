package com.yogeshj.autoform.uploadForm.temporaryFiles

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.splashScreenAndIntroScreen.IntroActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivityFormDetailsBinding
import com.yogeshj.autoform.uploadForm.uploadNewFormFragment.FormDetails
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

class FormDetailsActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener {

    private lateinit var binding:ActivityFormDetailsBinding
    private lateinit var dbRef: DatabaseReference

    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var selectedImg:Uri

    private lateinit var dialog2:Dialog

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null)
        {
            if(data.data!=null)
            {
                selectedImg=data.data!!
                binding.uploadedImageView.setImageURI(selectedImg)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val lang=resources.getStringArray(R.array.category)
        val arrayAdapter= ArrayAdapter(this@FormDetailsActivity,R.layout.dropdown_item,lang)
        binding.categoryEditText.setAdapter(arrayAdapter)

        val status=resources.getStringArray(R.array.status)
        val arrayAdapter2= ArrayAdapter(this@FormDetailsActivity,R.layout.dropdown_item,status)
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
    private lateinit var currentEditText:TextInputEditText

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
            Toast.makeText(this@FormDetailsActivity, "Please select a future date", Toast.LENGTH_SHORT).show()
            DatePickerDialog(this@FormDetailsActivity, this, year, month, day).show()
        }
//        binding.examDateEditText.setText("$savedDay/$savedMonth/$savedYear")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFormDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()
        binding.mainScroll.apply { alpha = 0f; translationY = 30f }
        binding.main.apply { alpha = 0f; translationY = 30f }
        binding.navBar.apply { alpha = 0f; translationY = -30f }

        binding.navBar.animate().alpha(1f).translationY(0f).setDuration(1000).start()
        binding.mainScroll.animate().alpha(1f).translationY(0f).setDuration(1000).start()
        binding.main.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()

        showLoading()

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        binding.main.alpha = 0f
        binding.main.animate().alpha(1f).setDuration(500).setListener(null)


//        dialog=AlertDialog.Builder(this@FormDetailsActivity)
//            .setMessage("Updating Profile")
//            .setCancelable(false)

        database=FirebaseDatabase.getInstance()
        storage=FirebaseStorage.getInstance()
        binding.uploadButton.setOnClickListener {
            val intent=Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(intent,1)
        }

        binding.examDateEditText.setOnClickListener {
            currentEditText=binding.examDateEditText
            getDateTime()
            DatePickerDialog(this@FormDetailsActivity,this,year,month,day).show()
        }

        binding.deadlineEditText.setOnClickListener {
            currentEditText=binding.deadlineEditText
            getDateTime()
            DatePickerDialog(this@FormDetailsActivity,this,year,month,day).show()
        }

        binding.viewForms.setOnClickListener {
            startActivity(Intent(this@FormDetailsActivity, ViewUploadedFormsActivity::class.java))
        }

        binding.logout.setOnClickListener {
            FirstScreenActivity.auth.signOut()
            TaskStackBuilder.create(this@FormDetailsActivity)
                .addNextIntentWithParentStack(Intent(this@FormDetailsActivity,FirstScreenActivity::class.java))
                .addNextIntent(Intent(this@FormDetailsActivity, IntroActivity::class.java))
                .startActivities()
//            startActivity(Intent(this@FormDetailsActivity,FirstScreenActivity::class.java))
            finish()
        }


        dbRef = FirebaseDatabase.getInstance().getReference("UploadForm")

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation)
        binding.linkOption.startAnimation(fadeIn)
        binding.customFormOption.startAnimation(fadeIn)

        hideLoading()
        binding.applicationFormDetailsButton.setOnClickListener {
            showLoading()
            if(binding.examNameEditText.text.toString().isEmpty() || binding.examHostNameEditText.text.toString().isEmpty() || binding.categoryEditText.text.toString().isEmpty() || binding.examDateEditText.text.toString().isEmpty() || binding.deadlineEditText.text.toString().isEmpty() || binding.examDescriptionEditText.text.toString().isEmpty() || binding.eligibilityEditText.text.toString().isEmpty() || binding.uploadedImageView.toString().isEmpty() || binding.statusEditText.text.toString().isEmpty())
            {
                hideLoading()
                Toast.makeText(this@FormDetailsActivity,"All fields are mandatory to proceed further!", Toast.LENGTH_LONG).show()
            }
            else if (this::selectedImg.isInitialized) {
                // Proceed with upload


                //alert dialog to confirm that details cannot be edited now
                binding.chooseForm.visibility=View.VISIBLE
                binding.applicationFormDetailsButton.visibility=View.GONE
                hideLoading()
                //also freeze above edit texts

                //if link is clicked then show an alert dialog and disable the choose custom form button & vice versa
                binding.linkOption.setOnClickListener {

                    //show an alert
//                    binding.addCustomFieldsLayout.visibility=View.GONE

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Enter Link")

                    val input = EditText(this@FormDetailsActivity)
                    input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
                    builder.setView(input)

                    builder.setPositiveButton("OK") { _, _ ->
                        val link = input.text.toString()
                        if (link.isNotEmpty()) {
                            showLoading()
                            dbRef = FirebaseDatabase.getInstance().getReference("UploadForm")
                            val reference=storage.reference.child(binding.examNameEditText.text.toString()).child(Date().time.toString())
                            reference.putFile(selectedImg).addOnCompleteListener{
                                if(it.isSuccessful){
                                    reference.downloadUrl.addOnSuccessListener { uri ->
                                        dbRef.child(FirstScreenActivity.auth.currentUser!!.uid).child(binding.examNameEditText.text.toString()).setValue(
                                            FormDetails(FirstScreenActivity.auth.currentUser!!.uid,binding.examNameEditText.text.toString(),
                                                binding.examHostNameEditText.text.toString(),uri.toString(),binding.categoryEditText.text.toString(),binding.examDateEditText.text.toString(),binding.deadlineEditText.text.toString(),
                                                binding.examDescriptionEditText.text.toString(),binding.eligibilityEditText.text.toString(),"","",binding.feesEditText.text.toString().toInt(),binding.statusEditText.text.toString())
                                        ).addOnSuccessListener {

                                            dbRef.child(FirstScreenActivity.auth.currentUser!!.uid).child(binding.examNameEditText.text.toString()).child("link").setValue(link)
                                                .addOnSuccessListener {

//                                                Toast.makeText(this, "Link added successfully", Toast.LENGTH_SHORT).show()
                                                    startActivity(Intent(this@FormDetailsActivity,
                                                        FormDetailsActivity::class.java))
                                                    startActivity(Intent(this@FormDetailsActivity,
                                                        ViewUploadedFormsActivity::class.java))
                                                    hideLoading()
                                                    Toast.makeText(this@FormDetailsActivity,"Form & Link Uploaded Successfully!", Toast.LENGTH_LONG).show()
                                                    finish()
                                                }
                                                .addOnFailureListener {
                                                    hideLoading()
                                                    Toast.makeText(this, "Failed to add link. Please try again later!", Toast.LENGTH_SHORT).show()

                                                }
                                        }.addOnFailureListener {
                                            hideLoading()
                                            Toast.makeText(this@FormDetailsActivity,"Failed to save user details. Please try again later.",Toast.LENGTH_LONG).show()
                                        }
                                    }

                                }
                                else
                                {
                                    hideLoading()
                                    Toast.makeText(this@FormDetailsActivity,"Failed to Upload Image at the moment. Please try again later.",Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        else
                        {
                            hideLoading()
                            Toast.makeText(this@FormDetailsActivity,"Link cannot be empty.",Toast.LENGTH_LONG).show()
                        }
                    }
                    builder.setNegativeButton("Cancel") { dialog, _ ->
                        hideLoading()
                        dialog.cancel()
                    }

                    builder.show()
                }


                binding.customFormOption.setOnClickListener {
                    //show an alert too
                    binding.addCustomFieldsLayout.visibility=View.VISIBLE
//                    binding.linkOption.visibility=View.GONE

                    binding.addButton.setOnClickListener {
                        val db = FirebaseDatabase.getInstance().getReference("UsersInfo")
                        val uniqueKeysList = ArrayList<String>()

                        db.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (uidSnapshot in snapshot.children) {
                                        for (keySnapshot in uidSnapshot.children) {
                                            val key = keySnapshot.key
                                            if(key!=null && key=="to" && !uniqueKeysList.contains("school start year"))
                                            {
                                                uniqueKeysList.add("school start year")
                                            }
                                            else if(key!=null && key=="from" && !uniqueKeysList.contains("school end year"))
                                            {
                                                uniqueKeysList.add("school end year")
                                            }
                                            else if(key != null && key!="field1" && key!="field2" && key!="field3" && key!="uid" && !uniqueKeysList.contains(key)) {

                                                uniqueKeysList.add(key)
                                            }
                                        }
                                    }
                                    uniqueKeysList.add("other")
                                    showKeysMenu(uniqueKeysList)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@FormDetailsActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
                            }
                        })
//                        val editText=EditText(this)
//                        editText.hint="Enter Details"
//                        editText.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                        binding.formContainer.addView(editText)

                    }

                    binding.subtractButton.setOnClickListener{
                        val childCount=binding.formContainer.childCount
                        if (childCount>0) {
                            binding.formContainer.removeViewAt(childCount-1)
                        }
                    }

                    binding.done.setOnClickListener {

                        //alert dialog to show that the details are getting saved now
                        showLoading()
                        var flag=false
                        for (i in 0 until binding.formContainer.childCount) {
                            val editText = binding.formContainer.getChildAt(i) as EditText
                            if (editText.text.toString().isEmpty()) {
                                hideLoading()
                                Toast.makeText(this, "Please fill all custom fields", Toast.LENGTH_SHORT).show()
                                flag=true
                                break
                            }
                        }
                        if(!flag) {
                            dbRef = FirebaseDatabase.getInstance().getReference("UploadForm")

                            val reference=storage.reference.child(binding.examNameEditText.text.toString()).child(Date().time.toString())
                            reference.putFile(selectedImg).addOnCompleteListener{
                                if(it.isSuccessful){
                                    reference.downloadUrl.addOnSuccessListener { uri ->
                                        val sanitizedExamName = binding.examNameEditText.text.toString().replace(".", "_")
                                        dbRef.child(FirstScreenActivity.auth.currentUser!!.uid).child(sanitizedExamName).setValue(
                                            FormDetails(FirstScreenActivity.auth.currentUser!!.uid,sanitizedExamName,
                                                binding.examHostNameEditText.text.toString(),uri.toString(),binding.categoryEditText.text.toString(),binding.examDateEditText.text.toString(),binding.deadlineEditText.text.toString(),
                                                binding.examDescriptionEditText.text.toString(),binding.eligibilityEditText.text.toString(),"","",binding.feesEditText.text.toString().toInt(),binding.statusEditText.text.toString())
                                        ).addOnSuccessListener {
                                            val formData = ArrayList<String>()

                                            for (i in 0 until binding.formContainer.childCount) {
                                                val editText = binding.formContainer.getChildAt(i) as EditText
                                                val input = editText.text.toString()

                                                if (input.isNotEmpty()) {
                                                    formData.add(input)
                                                }
                                            }

                                            dbRef.child(FirstScreenActivity.auth.currentUser!!.uid)
                                                .child(binding.examNameEditText.text.toString())
                                                .child("Application Form Details").setValue(formData)
                                                .addOnSuccessListener {
                                                    hideLoading()
                                                    Toast.makeText(this@FormDetailsActivity,"Form submitted successfully",Toast.LENGTH_LONG).show()
                                                }
                                                .addOnFailureListener {
                                                    hideLoading()
                                                    Toast.makeText(this@FormDetailsActivity,"Failed to submit form",Toast.LENGTH_LONG).show()
                                                }
                                        }.addOnFailureListener {
                                            hideLoading()
                                            Toast.makeText(this@FormDetailsActivity,"Failed to save form details.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                                else{
                                    hideLoading()
                                    Toast.makeText(this@FormDetailsActivity,"Failed to Upload Image at the moment. Please try again later.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }
            else {
                hideLoading()
                Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showKeysMenu(keysList: ArrayList<String>) {
        val builder = AlertDialog.Builder(this@FormDetailsActivity)
        builder.setTitle("Select the fields which you want in your form")

        val keysArray = keysList.toTypedArray()

        builder.setItems(keysArray) { _, which ->
            val selectedKey = keysArray[which]
            val editText = EditText(this)
            if(selectedKey=="school start year")
                editText.setText("to")
            else if(selectedKey=="school end year")
                editText.setText("from")
            else if(selectedKey=="other")
                editText.hint = "Enter the detail you want"
            else
                editText.setText(selectedKey)
            editText.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            binding.formContainer.addView(editText)
        }

        builder.create().show()
    }


    private fun initLoadingDialog() {
        dialog2 = Dialog(this@FormDetailsActivity)
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog2.setContentView(R.layout.dialog_wait)
        dialog2.setCanceledOnTouchOutside(false)
        dialog2.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showLoading() {
        binding.root.alpha = 0.5f
        if (!dialog2.isShowing) {
            dialog2.show()
        }
    }

    private fun hideLoading() {
        if (dialog2.isShowing) {
            dialog2.dismiss()
            binding.root.alpha = 1f
        }
    }
}