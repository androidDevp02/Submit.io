package com.yogeshj.autoform.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.databinding.ActivityUserMainBinding
import com.yogeshj.autoform.user.userAppliedFormFragments.UserAppliedFormsFragment
import com.yogeshj.autoform.FeedbackFragment.FeedbackFragment
import com.yogeshj.autoform.appExit.ExitDialog
import com.yogeshj.autoform.user.userFormFragment.UserFormFragment
import com.yogeshj.autoform.user.userPaymentHistory.UserPaymentHistoryFragment
import com.yogeshj.autoform.user.userSubscriptionFragment.UserSubscriptionFragment

class UserMainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUserMainBinding

    override fun onBackPressed() {

        ExitDialog.exit(this@UserMainActivity)
//        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        binding.bottomNavigationBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.userForms -> replaceFragment(UserFormFragment())
                R.id.applied_forms -> replaceFragment(UserAppliedFormsFragment())
                R.id.payment_history ->replaceFragment(UserPaymentHistoryFragment())
                R.id.premium -> replaceFragment(UserSubscriptionFragment())
                R.id.feedback->replaceFragment(FeedbackFragment())
                else -> {

                }
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.commit {
            replace(R.id.frame_layout,fragment)
        }
//        val fragmentManager=supportFragmentManager
//        val fragmentTransaction=fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frame_layout,fragment)
//        fragmentTransaction.commit()
    }

}