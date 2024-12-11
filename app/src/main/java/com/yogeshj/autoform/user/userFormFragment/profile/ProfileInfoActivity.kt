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
import com.yogeshj.autoform.user.HomeScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.authentication.User
import com.yogeshj.autoform.databinding.ActivityProfileInfoBinding
import com.yogeshj.autoform.user.UserMainActivity
import java.util.Date


class ProfileInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileInfoBinding
    private lateinit var dbRef:DatabaseReference

    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var selectedImg: Uri?=null

    lateinit var currentUser: User

    private lateinit var dialog:Dialog

    private val handler = Handler(Looper.getMainLooper())
    private val adInterval = 31_000L
    private val loadAdRunnable = object : Runnable {
        override fun run() {
            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
            handler.postDelayed(this, adInterval)
        }
    }

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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initLoadingDialog()

        MobileAds.initialize(this@ProfileInfoActivity)
        handler.post(loadAdRunnable)

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
                        if (curr!=null && curr.uid== FirstScreenActivity.auth.currentUser!!.uid) {
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
                        if (curr!=null && curr.uid== FirstScreenActivity.auth.currentUser!!.uid) {
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
            else
            {
                if (selectedImg!=null) {
                    val reference=storage.reference.child("Profile_pic").child(Date().time.toString())
                    reference.putFile(selectedImg!!).addOnCompleteListener{
                        if(it.isSuccessful){
                            reference.downloadUrl.addOnSuccessListener { uri ->
                                dbRef = FirebaseDatabase.getInstance().getReference("UsersInfo")
                                dbRef.child(currentUser.uid!!).child("uid").setValue(FirstScreenActivity.auth.currentUser!!.uid)
                                dbRef.child(currentUser.uid!!).child("phone").setValue(binding.phone.text.toString())
                                dbRef.child(currentUser.uid!!).child("dob").setValue(binding.dob.text.toString())
                                dbRef.child(currentUser.uid!!).child("gender").setValue(binding.gender.text.toString())
                                dbRef.child(currentUser.uid!!).child("state").setValue(binding.state.text.toString())
                                dbRef.child(currentUser.uid!!).child("profilePic").setValue(uri.toString())
                                hideLoading()
                                startActivity(Intent(this@ProfileInfoActivity, UserMainActivity::class.java))
                                finish()
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
                    startActivity(Intent(this@ProfileInfoActivity, UserMainActivity::class.java))
                    finish()
                }
            }
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
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    private fun hideLoading() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}