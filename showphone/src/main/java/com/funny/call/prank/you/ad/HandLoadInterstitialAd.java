package com.funny.call.prank.you.ad;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.funny.call.prank.you.App;
import com.funny.call.prank.you.BuildConfig;
import com.funny.call.prank.you.IFace.IAdListener;

/**
 * 挂断
 */
public class HandLoadInterstitialAd {


    private static final String TAG = "HandLoadInterstitialAd";
    static HandLoadInterstitialAd instance;


    public static HandLoadInterstitialAd getInstance(Context context) {
        if (null == instance) {

            instance = new HandLoadInterstitialAd(context);
        }

        return instance;
    }

    public InterstitialAd getSplashAd() {
        return splashAd;
    }

    private InterstitialAd splashAd;



    private HandLoadInterstitialAd(Context context) {
        if (!(BuildConfig.FLAVOR.equals(BuildConfig.gp))) {
            return;
        }

        splashAd = new InterstitialAd(context);


        initInterstitialAd(context, splashAd, "ca-app-pub-4409839171902420/5522007692");

    }

    private void initInterstitialAd(Context context, InterstitialAd ad, String id) {
        ad.setAdUnitId(id);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdLoaded() {


            }
        });

    }

    public void startGame(InterstitialAd ad) {
        if (ad != null && !ad.isLoading() && !ad.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
//            AdRequest adRequest = new AdRequest.Builder().addTestDevice("B09B121E39796297E267CE7F263B3D26").build();
            ad.loadAd(adRequest);
        }

    }

    public void showInterstitial(InterstitialAd ad) {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (ad != null && ad.isLoaded()) {
            ad.show();
        }
    }



}
