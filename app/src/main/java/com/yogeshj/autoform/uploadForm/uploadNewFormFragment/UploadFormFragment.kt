package com.yogeshj.autoform.uploadForm.uploadNewFormFragment

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
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.FragmentUploadFormBinding
import com.yogeshj.autoform.splashScreenAndIntroScreen.IntroActivity
import com.yogeshj.autoform.uploadForm.pastFormsFragment.PastFormFragment
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

//check profile pic in uploading a custom form

@Suppress("DEPRECATION")
class UploadFormFragment : Fragment(),DatePickerDialog.OnDateSetListener {

    private lateinit var binding:FragmentUploadFormBinding

    private lateinit var dbRef: DatabaseReference

    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var selectedImg: Uri

    private lateinit var dialog2:Dialog

    private val customFormOptionChosen=HashSet<String>()

    @Deprecated("Deprecated in Java")
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
        val arrayAdapter= ArrayAdapter(requireContext(),R.layout.dropdown_item,lang)
        binding.categoryEditText.setAdapter(arrayAdapter)

        val status=resources.getStringArray(R.array.status)
        val arrayAdapter2= ArrayAdapter(requireContext(),R.layout.dropdown_item,status)
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
            Toast.makeText(context, "Please select a future date", Toast.LENGTH_SHORT).show()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }
//        binding.examDateEditText.setText("$savedDay/$savedMonth/$savedYear")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentUploadFormBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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


//        dialog=AlertDialog.Builder(context)
//            .setMessage("Updating Profile")
//            .setCancelable(false)

        database= FirebaseDatabase.getInstance()
        storage= FirebaseStorage.getInstance()
        binding.uploadButton.setOnClickListener {
            val intent= Intent()
            intent.action= Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(intent,1)
        }

        binding.examDateEditText.setOnClickListener {
            currentEditText=binding.examDateEditText
            getDateTime()
            DatePickerDialog(requireContext(),this,year,month,day).show()
        }

        binding.deadlineEditText.setOnClickListener {
            currentEditText=binding.deadlineEditText
            getDateTime()
            DatePickerDialog(requireContext(),this,year,month,day).show()
        }


        binding.logout.setOnClickListener {
            FirstScreenActivity.auth.signOut()
            TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(Intent(context,FirstScreenActivity::class.java))
                .addNextIntent(Intent(context, IntroActivity::class.java))
                .startActivities()
//            startActivity(Intent(context,FirstScreenActivity::class.java))
        }


        dbRef = FirebaseDatabase.getInstance().getReference("UploadForm")

        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in_animation)
        binding.linkOption.startAnimation(fadeIn)
        binding.customFormOption.startAnimation(fadeIn)

        hideLoading()
        binding.applicationFormDetailsButton.setOnClickListener {
            showLoading()
            if(binding.examNameEditText.text.toString().isEmpty() || binding.examHostNameEditText.text.toString().isEmpty() || binding.categoryEditText.text.toString().isEmpty() || binding.examDateEditText.text.toString().isEmpty() || binding.deadlineEditText.text.toString().isEmpty() || binding.examDescriptionEditText.text.toString().isEmpty() || binding.eligibilityEditText.text.toString().isEmpty() || binding.uploadedImageView.toString().isEmpty() || binding.statusEditText.text.toString().isEmpty())
            {
                hideLoading()
                Toast.makeText(context,"All fields are mandatory to proceed further!", Toast.LENGTH_LONG).show()
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

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Enter Link")

                    val input = EditText(context)
                    input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
                    builder.setView(input)

                    builder.setPositiveButton("OK") { _, _ ->
                        val link = input.text.toString()
                        if (link.isNotEmpty()) {
                            showLoading()
                            dbRef = FirebaseDatabase.getInstance().getReference("UploadForm")
                            val reference=storage.reference.child(binding.examNameEditText.text.toString()).child(
                                Date().time.toString())
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
                                                    val transaction = parentFragmentManager.beginTransaction()
                                                    transaction.replace(R.id.frame_layout_upload_form, PastFormFragment())
                                                    transaction.addToBackStack(null)
                                                    transaction.commit()

//                                                    startActivity(Intent(context,FormDetailsActivity::class.java))
//                                                    startActivity(Intent(context,ViewUploadedFormsActivity::class.java))
                                                    hideLoading()
                                                    Toast.makeText(context,"Form & Link Uploaded Successfully!", Toast.LENGTH_LONG).show()
//                                                    finish()
                                                }
                                                .addOnFailureListener {
                                                    hideLoading()
                                                    Toast.makeText(context, "Failed to add link. Please try again later!", Toast.LENGTH_SHORT).show()

                                                }
                                        }.addOnFailureListener {
                                            hideLoading()
                                            Toast.makeText(context,"Failed to save user details. Please try again later.",
                                                Toast.LENGTH_LONG).show()
                                        }
                                    }

                                }
                                else
                                {
                                    hideLoading()
                                    Toast.makeText(context,"Failed to Upload Image at the moment. Please try again later.",
                                        Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        else
                        {
                            hideLoading()
                            Toast.makeText(context,"Link cannot be empty.", Toast.LENGTH_LONG).show()
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
                                            if(key!=null &&(key=="education_level" || key=="course" || key=="school" || key=="from" || key=="to" || key=="cgpa"))
                                            {
                                                if(!uniqueKeysList.contains("Most recent education details") && !customFormOptionChosen.contains("Most recent education details"))
                                                {
                                                    uniqueKeysList.add("Most recent education details")
                                                }
                                            }
                                            else if(key!=null &&(key=="language1" || key=="proficiency1"))
                                            {
                                                if(!uniqueKeysList.contains("Language & Proficiency 1") && !customFormOptionChosen.contains("Language & Proficiency 1"))
                                                {
                                                    uniqueKeysList.add("Language & Proficiency 1")
                                                }
                                            }
                                            else if(key!=null &&(key=="language2"|| key=="proficiency2"))
                                            {
                                                if(!uniqueKeysList.contains("Language & Proficiency 2") && !customFormOptionChosen.contains("Language & Proficiency 2"))
                                                {
                                                    uniqueKeysList.add("Language & Proficiency 2")
                                                }
                                            }
                                            else if(key!=null &&(key=="language3"|| key=="proficiency3"))
                                            {
                                                if(!uniqueKeysList.contains("Language & Proficiency 3") && !customFormOptionChosen.contains("Language & Proficiency 3"))
                                                {
                                                    uniqueKeysList.add("Language & Proficiency 3")
                                                }
                                            }
                                            else if(key != null && key!="field1" && key!="field2" && key!="field3" && key!="uid" && !uniqueKeysList.contains(key) && !customFormOptionChosen.contains(key)) {

                                                uniqueKeysList.add(key)
                                            }
                                        }
                                    }
                                    uniqueKeysList.add("other")
                                    showKeysMenu(uniqueKeysList)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "Error fetching data", Toast.LENGTH_SHORT).show()
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
                            val lastChild=binding.formContainer.getChildAt(childCount-1)
                            if (lastChild is EditText) {
                                val text = lastChild.text?.toString() ?: "No text available"
                                if(text=="CGPA"){
                                    customFormOptionChosen.remove("Most recent education details")
                                    binding.formContainer.removeViewAt(childCount-1)
                                    binding.formContainer.removeViewAt(childCount-2)
                                    binding.formContainer.removeViewAt(childCount-3)
                                    binding.formContainer.removeViewAt(childCount-4)
                                    binding.formContainer.removeViewAt(childCount-5)
                                    binding.formContainer.removeViewAt(childCount-6)
                                }
                                else if(text=="Proficiency1")
                                {
                                    customFormOptionChosen.remove("Language & Proficiency 1")
                                    binding.formContainer.removeViewAt(childCount-1)
                                    binding.formContainer.removeViewAt(childCount-2)
                                }
                                else if(text=="Proficiency2")
                                {
                                    customFormOptionChosen.remove("Language & Proficiency 2")
                                    binding.formContainer.removeViewAt(childCount-1)
                                    binding.formContainer.removeViewAt(childCount-2)
                                }
                                else if(text=="Proficiency3")
                                {
                                    customFormOptionChosen.remove("Language & Proficiency 3")
                                    binding.formContainer.removeViewAt(childCount-1)
                                    binding.formContainer.removeViewAt(childCount-2)
                                }
                                else {
                                    customFormOptionChosen.remove(text)
                                    binding.formContainer.removeViewAt(childCount-1)
                                }

//                                Toast.makeText(context, "Removed: $text", Toast.LENGTH_SHORT).show()
                            }

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
                                Toast.makeText(context, "Please fill all custom fields", Toast.LENGTH_SHORT).show()
                                flag=true
                                break
                            }

                        }
                        if(!flag) {
                            dbRef = FirebaseDatabase.getInstance().getReference("UploadForm")

                            val reference=storage.reference.child(binding.examNameEditText.text.toString()).child(
                                Date().time.toString())
                            reference.putFile(selectedImg).addOnCompleteListener{
                                if(it.isSuccessful){
                                    reference.downloadUrl.addOnSuccessListener { uri ->
                                        val sanitizedExamName = binding.examNameEditText.text.toString().replace(".", "_")
                                        dbRef.child(FirstScreenActivity.auth.currentUser!!.uid).child(sanitizedExamName).setValue(
                                            FormDetails(
                                                FirstScreenActivity.auth.currentUser!!.uid,sanitizedExamName,
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
                                                    Toast.makeText(context,"Form submitted successfully",Toast.LENGTH_LONG).show()
                                                    val transaction = parentFragmentManager.beginTransaction()
                                                    transaction.replace(R.id.frame_layout_upload_form, PastFormFragment())
                                                    transaction.addToBackStack(null)
                                                    transaction.commit()
                                                }
                                                .addOnFailureListener {
                                                    hideLoading()
                                                    Toast.makeText(context,"Failed to submit form",Toast.LENGTH_LONG).show()
                                                }
                                        }.addOnFailureListener {
                                            hideLoading()
                                            Toast.makeText(context,"Failed to save form details.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                                else{
                                    hideLoading()
                                    Toast.makeText(context,"Failed to Upload Image at the moment. Please try again later.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }
            else {
                hideLoading()
                Toast.makeText(context, "Please select an image!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showKeysMenu(keysList: ArrayList<String>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select the fields which you want in your form")

        val keysArray = keysList.toTypedArray()

        builder.setItems(keysArray) { _, which ->
            val selectedKey = keysArray[which]

            if (selectedKey == "Most recent education details") {
                val educationFields = arrayOf("Education_level", "Course", "School", "From", "To", "CGPA")
                for (field in educationFields) {
                    val editText = EditText(context)
                    editText.setText(field)
                    editText.isFocusable=false
                    editText.isEnabled=false
                    editText.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    binding.formContainer.addView(editText)
                }
                customFormOptionChosen.add("Most recent education details")
            }
            else if(selectedKey=="Language & Proficiency 1")
            {
                val languageFields = arrayOf("Language1","Proficiency1")
                for (field in languageFields) {
                    val editText = EditText(context)
                    editText.setText(field)
                    editText.isFocusable=false
                    editText.isEnabled=false
                    editText.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    binding.formContainer.addView(editText)
                }
                customFormOptionChosen.add("Language & Proficiency 1")
            }
            else if(selectedKey=="Language & Proficiency 2")
            {
                val languageFields = arrayOf("Language2","Proficiency2")
                for (field in languageFields) {
                    val editText = EditText(context)
                    editText.setText(field)
                    editText.isFocusable=false
                    editText.isEnabled=false
                    editText.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    binding.formContainer.addView(editText)
                }
                customFormOptionChosen.add("Language & Proficiency 2")
            }
            else if(selectedKey=="Language & Proficiency 3")
            {
                val languageFields = arrayOf("Language3", "Proficiency3")
                for (field in languageFields) {
                    val editText = EditText(context)
                    editText.setText(field)
                    editText.isFocusable=false
                    editText.isEnabled=false
                    editText.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    binding.formContainer.addView(editText)
                }
                customFormOptionChosen.add("Language & Proficiency 3")
            }
            else {
                val editText = EditText(context)
                editText.setText(selectedKey)
                editText.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                binding.formContainer.addView(editText)
                if(selectedKey!="other"){
                    customFormOptionChosen.add(selectedKey)
                    editText.isFocusable=false
                    editText.isEnabled=false
                }
                else{
                    editText.hint = "Enter the detail you want"
                }
            }
        }

        builder.create().show()
    }


//    private fun showKeysMenu(keysList: ArrayList<String>) {
//        val builder = AlertDialog.Builder(context)
//        builder.setTitle("Select the fields which you want in your form")
//
//        val keysArray = keysList.toTypedArray()
//
//        builder.setItems(keysArray) { _, which ->
//            val selectedKey = keysArray[which]
//            val editText = EditText(context)
//            if(selectedKey=="school start year")
//                editText.setText("to")
//            else if(selectedKey=="school end year")
//                editText.setText("from")
//            else if(selectedKey=="other")
//                editText.hint = "Enter the detail you want"
//            else
//                editText.setText(selectedKey)
//            editText.layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            binding.formContainer.addView(editText)
//        }
//
//        builder.create().show()
//    }


    private fun initLoadingDialog() {
        dialog2 = Dialog(requireContext())
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog2.setContentView(R.layout.dialog_wait)
        dialog2.setCanceledOnTouchOutside(false)
        dialog2.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showLoading() {
        if (!dialog2.isShowing) {
            dialog2.show()
        }
    }

    private fun hideLoading() {
        if (dialog2.isShowing) {
            dialog2.dismiss()
        }
    }
}