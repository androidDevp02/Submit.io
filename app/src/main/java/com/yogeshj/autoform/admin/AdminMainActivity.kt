package com.yogeshj.autoform.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.yogeshj.autoform.R
import com.yogeshj.autoform.admin.forms.FormsFragment
import com.yogeshj.autoform.admin.requests.VerifyOrganisationsFragment
import com.yogeshj.autoform.admin.users.UsersFragment
import com.yogeshj.autoform.databinding.ActivityAdminMainBinding

class AdminMainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAdminMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        replaceFragment(UsersFragment())

        binding.bottomNavigationBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.users -> replaceFragment(UsersFragment())
                R.id.requests -> replaceFragment(VerifyOrganisationsFragment())
                R.id.forms -> replaceFragment(FormsFragment())

                else -> {

                }
            }
            true
        }

    }

    private fun replaceFragment(fragment:Fragment){
        supportFragmentManager.commit {
            replace(R.id.frame_layout,fragment)
        }
//        val fragmentManager=supportFragmentManager
//        val fragmentTransaction=fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frame_layout,fragment)
//        fragmentTransaction.commit()
    }
}