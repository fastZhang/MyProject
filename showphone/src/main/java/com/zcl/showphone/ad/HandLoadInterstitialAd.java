package com.zcl.showphone.ad;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.zcl.showphone.BuildConfig;
import com.zcl.showphone.IFace.IAdListener;

public class HandLoadInterstitialAd {

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


        initInterstitialAd(context, splashAd, "ca-app-pub-7835308551963221/4015269777");

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
