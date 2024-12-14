package com.yogeshj.autoform.splashScreenAndIntroScreen

import android.app.TaskStackBuilder
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.yogeshj.autoform.FirstScreenActivity
import com.yogeshj.autoform.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("splash", MODE_PRIVATE)
        val edit: SharedPreferences.Editor = sharedPreferences.edit()

        Handler(Looper.getMainLooper()).postDelayed(
            {
                binding.text.visibility = View.VISIBLE
                binding.text.alpha = 0f
                binding.text.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .withEndAction {
                        if (sharedPreferences.getBoolean("isMain", false)) {
                            startActivity(Intent(this@SplashScreenActivity, FirstScreenActivity::class.java))
                            finish()
                        } else {
                            edit.putBoolean("isMain", true)
                            edit.apply()

                            TaskStackBuilder.create(this@SplashScreenActivity)
                                .addNextIntentWithParentStack(Intent(this@SplashScreenActivity,
                                    FirstScreenActivity::class.java))
                                .addNextIntent(Intent(this@SplashScreenActivity, IntroActivity::class.java))
                                .startActivities()
                        }
                    }
                    .start()
            }, 0
        )

    }
}