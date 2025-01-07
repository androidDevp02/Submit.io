plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.yogeshj.autoform"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yogeshj.autoform"
        minSdk = 25
        targetSdk = 34
        versionCode = 8
        versionName = "1.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures{
        viewBinding=true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.play.services.ads.lite)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //glide for rounded image
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)

    //razorpay
    implementation(libs.razorpay.checkout)


    implementation(libs.circleimageview)


    //profile photo
    implementation(libs.imagepicker)

    //onboarding screen material-intro-screen
    implementation(libs.material.intro.screen)

    implementation(libs.library)

    //google adds
    implementation(libs.play.services.ads)

    //fragment
    implementation(libs.androidx.fragment.ktx)

    implementation(libs.google.firebase.auth.ktx)

    //swipe icon & colors
    implementation("it.xabaras.android:recyclerview-swipedecorator:1.4")
}