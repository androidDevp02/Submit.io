package com.yogeshj.autoform.user.userFormFragment.profile

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.admin.users.changeUserData.ChangeUserDataActivity
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.databinding.ActivityProfileInfoBinding
import com.yogeshj.autoform.user.UserMainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Suppress("DEPRECATION")
class ProfileInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileInfoBinding
    private lateinit var dbRef:DatabaseReference

    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var selectedImg: Uri?=null

    lateinit var currentUser: User

    private lateinit var dialog:Dialog

    private var currentUserUid:String?=null
    private var backToAdminScreen=false

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data!=null)
        {
            if(data.data!=null)
            {
                selectedImg=data.data
                binding.profileImg.setImageURI(selectedImg)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val gender=resources.getStringArray(R.array.gender)
        val arrayAdapter= ArrayAdapter(this@ProfileInfoActivity,R.layout.dropdown_item,gender)
        binding.gender.setAdapter(arrayAdapter)

        val states=resources.getStringArray(R.array.state)
        val arrayAdapter2= ArrayAdapter(this@ProfileInfoActivity,R.layout.dropdown_item,states)
        binding.state.setAdapter(arrayAdapter2)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        MobileAds.initialize(this@ProfileInfoActivity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        binding.heading.apply { alpha = 0f; translationY = -20f }
        binding.imageFrame.apply { alpha = 0f; translationY = -20f }
        binding.mainScroll.apply { alpha = 0f; translationY = -20f }
        binding.continueBtn.apply { alpha = 0f; translationY = -20f }

        binding.heading.animate().alpha(1f).translationY(0f).setDuration(1000).start()
        binding.imageFrame.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(200).start()
        binding.mainScroll.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(400).start()
        binding.continueBtn.animate().alpha(1f).translationY(0f).setDuration(1000).setStartDelay(600).start()



        showLoading()

        FirstScreenActivity.auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        storage=FirebaseStorage.getInstance()

        currentUserUid=intent.getStringExtra("uid")
        backToAdminScreen=intent.getBooleanExtra("backToAdminScreen",false)
        if(currentUserUid==null)
        {
            currentUserUid=FirstScreenActivity.auth.currentUser!!.uid
        }

        binding.uploadButton.setOnClickListener {
            val intent=Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(intent,1)
        }

        val db = FirebaseDatabase.getInstance().getReference("Users")
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hideLoading()
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val profilePicUrl = snap.child("profilePic").value?.toString()
                        val curr = snap.getValue(User::class.java)
                        if (curr!=null && curr.uid==currentUserUid) {
                            currentUser=curr
                            binding.name.setText(currentUser.name)
                            binding.email.setText(currentUser.email)
                            if(profilePicUrl!=null)
                                break
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })
        var picPresent=false

        val db2 = FirebaseDatabase.getInstance().getReference("UsersInfo")
        db2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hideLoading()
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val profilePicUrl = snap.child("profilePic").value?.toString()
                        val curr = snap.getValue(User::class.java)
                        if (curr!=null && curr.uid==currentUserUid) {
                            currentUser=curr
                            binding.name.setText(currentUser.name)
                            binding.email.setText(currentUser.email)
                            binding.phone.setText(currentUser.phone)
                            binding.dob.setText(currentUser.dob)
                            binding.gender.setText(currentUser.gender)
                            binding.state.setText(currentUser.state)
                            if(profilePicUrl!=null)
                            {
                                picPresent=true
                                Glide.with(this@ProfileInfoActivity)
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.user_icon)
                                    .error(R.drawable.user_icon)
                                    .into(binding.profileImg)
                            }
                            break
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                hideLoading()
            }
        })


        binding.continueBtn.setOnClickListener {
            showLoading()

            if(binding.name.text.toString().isEmpty() || binding.phone.text.toString().isEmpty() || binding.email.text.toString().isEmpty() || binding.dob.text.toString().isEmpty() || binding.gender.text.toString().isEmpty() || binding.state.text.toString().isEmpty())
            {
                hideLoading()
                Toast.makeText(this@ProfileInfoActivity,"All fields are mandatory!",Toast.LENGTH_LONG).show()
            }
            else if(binding.phone.text.toString().length!=10)
            {
                hideLoading()
                Toast.makeText(this@ProfileInfoActivity,"Phone number should be of 10 digits",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.email.text.toString()).matches()){
                hideLoading()
                Toast.makeText(this@ProfileInfoActivity,"Enter a valid email address",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else if(!isValidDate(binding.dob.text.toString(), "dd/MM/yyyy")){
                hideLoading()
                Toast.makeText(this@ProfileInfoActivity,"Enter a valid date of birth (DD/MM/YYYY)",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else if (!resources.getStringArray(R.array.gender).contains(binding.gender.text.toString())) {
                hideLoading()
                Toast.makeText(this@ProfileInfoActivity, "Please select a valid gender", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else if (!resources.getStringArray(R.array.state).contains(binding.state.text.toString())) {
                hideLoading()
                Toast.makeText(this@ProfileInfoActivity, "Please select a valid state", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            else
            {
                if (selectedImg!=null) {
                    val reference=storage.reference.child("Profile_pic").child(Date().time.toString())
                    reference.putFile(selectedImg!!).addOnCompleteListener{
                        if(it.isSuccessful){
                            reference.downloadUrl.addOnSuccessListener { uri ->
                                dbRef = FirebaseDatabase.getInstance().getReference("UsersInfo")
                                dbRef.child(currentUser.uid!!).child("uid").setValue(currentUserUid)
                                dbRef.child(currentUser.uid!!).child("phone").setValue(binding.phone.text.toString())
                                dbRef.child(currentUser.uid!!).child("dob").setValue(binding.dob.text.toString())
                                dbRef.child(currentUser.uid!!).child("gender").setValue(binding.gender.text.toString())
                                dbRef.child(currentUser.uid!!).child("state").setValue(binding.state.text.toString())
                                dbRef.child(currentUser.uid!!).child("profilePic").setValue(uri.toString())
                                hideLoading()
                                if(backToAdminScreen)
                                {
                                    startActivity(Intent(this@ProfileInfoActivity, ChangeUserDataActivity::class.java))
                                    finish()
                                }
                                else {
                                    startActivity(Intent(this@ProfileInfoActivity, UserMainActivity::class.java))
                                    finish()
                                }

                            }
                        }
                        else {
                            hideLoading()
                            Toast.makeText(this@ProfileInfoActivity,"Image upload unsuccessful.",Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else if(!picPresent)
                {
                    hideLoading()
                    Toast.makeText(this@ProfileInfoActivity,"Please Upload an Image.",Toast.LENGTH_LONG).show()
                }
                else
                {
                    dbRef = FirebaseDatabase.getInstance().getReference("UsersInfo")
                    dbRef.child(currentUser.uid!!).child("phone").setValue(binding.phone.text.toString())
                    dbRef.child(currentUser.uid!!).child("dob").setValue(binding.dob.text.toString())
                    dbRef.child(currentUser.uid!!).child("gender").setValue(binding.gender.text.toString())
                    dbRef.child(currentUser.uid!!).child("state").setValue(binding.state.text.toString())
                    hideLoading()
                    if(backToAdminScreen)
                    {
//                        startActivity(Intent(this@ProfileInfoActivity, ChangeUserDataActivity::class.java))
                        finish()
                    }
                    else {
                        startActivity(Intent(this@ProfileInfoActivity, UserMainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun isValidDate(date: String, format: String): Boolean {
        return try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.isLenient = false
            val parsedDate = sdf.parse(date)

            // check if the date is not in the future
            if (parsedDate != null && parsedDate.after(Date())) {
                return false
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun initLoadingDialog() {
        dialog = Dialog(this@ProfileInfoActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showLoading() {
        binding.root.alpha = 0.5f
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun hideLoading() {
        if (dialog.isShowing) {
            dialog.dismiss()
            binding.root.alpha = 1f
        }
    }
}