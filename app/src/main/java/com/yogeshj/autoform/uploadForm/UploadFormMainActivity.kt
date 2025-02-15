package com.yogeshj.autoform.uploadForm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivityUploadFormMainBinding
import com.yogeshj.autoform.uploadForm.pastFormsFragment.PastFormFragment
import com.yogeshj.autoform.uploadForm.uploadNewFormFragment.UploadFormFragment
import com.yogeshj.autoform.FeedbackFragment.FeedbackFragment
import com.yogeshj.autoform.appExit.ExitDialog

class UploadFormMainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUploadFormMainBinding

    override fun onBackPressed() {

        ExitDialog.exit(this@UploadFormMainActivity)
//        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUploadFormMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        binding.bottomNavigationBarUploadForm.setOnItemSelectedListener {
            when(it.itemId){
                R.id.upload -> replaceFragment(UploadFormFragment())
                R.id.past_forms -> replaceFragment(PastFormFragment())
                R.id.feedback->replaceFragment(FeedbackFragment())
                else -> {

                }
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.commit {
            replace(R.id.frame_layout_upload_form,fragment)
        }
    }

}