package com.yogeshj.autoform.user.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.databinding.ActivityAwardsAndAchievementBinding

class AwardsAndAchievement : AppCompatActivity() {
    private lateinit var binding:ActivityAwardsAndAchievementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAwardsAndAchievementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirstScreenActivity.auth= FirebaseAuth.getInstance()

    }
}