package com.yogeshj.autoform.splashScreenAndIntroScreen

import android.os.Bundle
import com.yogeshj.autoform.R
import io.github.dreierf.materialintroscreen.MaterialIntroActivity
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder

class IntroActivity:MaterialIntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(
            SlideFragmentBuilder()
            .backgroundColor(R.color.primaryColor)
            .buttonsColor(R.color.accentColor)
            .image(R.drawable.logo)
            .title("What is Submit.io?")
            .description("Submit.io is a one-stop app for students who have passed or are appearing for Class XII exams. It helps them find and fill important forms for college admissions in fields like engineering, medical, and more.")
            .build()
        )
        addSlide(
            SlideFragmentBuilder()
            .backgroundColor(R.color.primaryColor)
            .buttonsColor(R.color.accentColor)
            .image(R.drawable.intro_img2)
            .title("What Students Can Do")
            .description("Students can easily find forms based on their field of interest, such as engineering or medical. They can fill out these forms directly in the app or get redirected to the official websites, ensuring they never miss a form submission.")
            .build()
        )
        addSlide(
            SlideFragmentBuilder()
            .backgroundColor(R.color.primaryColor)
            .buttonsColor(R.color.accentColor)
            .image(R.drawable.intro_img3)
            .title("What Organizations Can Do")
            .description("Organizations or institutions can upload their forms for exams or admissions, making it easy for students to apply. They can also manage payments through the app, simplifying the entire process for everyone.")
            .build()
        )
    }
}