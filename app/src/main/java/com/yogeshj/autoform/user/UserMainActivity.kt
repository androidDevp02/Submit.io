package com.yogeshj.autoform.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.R
import com.yogeshj.autoform.admin.forms.FormsFragment
import com.yogeshj.autoform.admin.requests.VerifyOrganisationsFragment
import com.yogeshj.autoform.admin.users.UsersFragment
import com.yogeshj.autoform.databinding.ActivityUserMainBinding
import com.yogeshj.autoform.user.userAppliedFormFragments.UserAppliedFormsFragment
import com.yogeshj.autoform.user.userFormFragment.UserFormFragment
import com.yogeshj.autoform.user.userSubscriptionFragment.SubscriptionPaymentModel
import com.yogeshj.autoform.user.userSubscriptionFragment.UserSubscriptionFragment
import java.util.Calendar

class UserMainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUserMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

        binding.bottomNavigationBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.userForms -> replaceFragment(UserFormFragment())
                R.id.applied_forms -> replaceFragment(UserAppliedFormsFragment())
                R.id.premium -> replaceFragment(UserSubscriptionFragment())

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