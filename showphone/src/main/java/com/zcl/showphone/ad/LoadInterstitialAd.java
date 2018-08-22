package com.zcl.showphone.ad;

import android.content.Context;
import android.os.CountDownTimer;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class LoadInterstitialAd {

    public InterstitialAd getSplashAd() {
        return splashAd;
    }

    public InterstitialAd getHeaderAd() {
        return headerAd;
    }

    public InterstitialAd getStartAd() {
        return startAd;
    }

    private InterstitialAd splashAd;
    private InterstitialAd headerAd;
    private InterstitialAd startAd;


    public LoadInterstitialAd(Context context) {
        splashAd = new InterstitialAd(context);
        headerAd = new InterstitialAd(context);
        startAd = new InterstitialAd(context);


        initInterstitialAd(context, splashAd, "ca-app-pub-7835308551963221/7461473036");
        initInterstitialAd(context, headerAd, "ca-app-pub-7835308551963221/1948334040");
        initInterstitialAd(context, startAd, "ca-app-pub-7835308551963221/2533414136");

    }

    private void initInterstitialAd(Context context, InterstitialAd ad, String id) {
        ad.setAdUnitId(id);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startGame(ad);
            }

            @Override
            public void onAdFailedToLoad(int var1) {
            }
        });

        startGame(ad);
    }

    private void startGame(InterstitialAd ad) {
        if (!ad.isLoading() && !ad.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
//            AdRequest adRequest = new AdRequest.Builder().addTestDevice("B09B121E39796297E267CE7F263B3D26").build();
            ad.loadAd(adRequest);
        }

    }

    public void showInterstitial(InterstitialAd ad) {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (ad != null && ad.isLoaded()) {
            ad.show();
        } else {
            startGame(ad);
        }
    }

}
