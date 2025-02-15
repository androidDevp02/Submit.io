package com.yogeshj.autoform.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar

object InterstitialAds {

     var mInterstitialAd:InterstitialAd?=null

    fun loadInterstitial(context: Activity){
        InterstitialAd.load(context,"ca-app-pub-2095090407853200/9798196488",AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.show(context)
                }
            })
    }

}