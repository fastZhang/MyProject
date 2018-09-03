package com.zcl.showphone.ad;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.zcl.showphone.App;
import com.zcl.showphone.BuildConfig;
import com.zcl.showphone.IFace.IAdListener;

public class LoadInterstitialAd {
    private static final String TAG = "HandLoadInterstitialAd";

    public InterstitialAd getSplashAd() {
        return splashAd;
    }

    public InterstitialAd getHeaderAd() {
        return headerAd;
    }

    public InterstitialAd getStartAd() {
        return startAd;
    }

    public InterstitialAd getTiggerAd() {
        return tiggerAd;
    }

    private InterstitialAd splashAd;
    private InterstitialAd headerAd;
    private InterstitialAd startAd;
    private InterstitialAd tiggerAd;


    public LoadInterstitialAd(Activity context) {
        if (!(BuildConfig.FLAVOR.equals(BuildConfig.gp))) {
            return;
        }

        splashAd = new InterstitialAd(context);
        headerAd = new InterstitialAd(context);
//        startAd = new InterstitialAd(context);
        tiggerAd = new InterstitialAd(context);


        initInterstitialAd(context, splashAd, "ca-app-pub-7835308551963221/7461473036");
        initInterstitialAd(context, headerAd, "ca-app-pub-7835308551963221/1948334040");
//        initInterstitialAd(context, startAd, "ca-app-pub-7835308551963221/2533414136");
        initInterstitialAd(context, tiggerAd, "ca-app-pub-7835308551963221/3328572744");

        startGame(splashAd);
//        startGame(tiggerAd);


    }

    private void initInterstitialAd(Context context, InterstitialAd ad, String id) {
        ad.setAdUnitId(id);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
//                startGame(ad);
                if (null != context)
                    ((IAdListener) context).onAdClosed(ad);
            }

            @Override
            public void onAdLoaded() {
                if (null != context)
                    ((IAdListener) context).onAdLoaded(ad);

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

    public boolean showInterstitial(InterstitialAd ad) {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (ad != null && ad.isLoaded()) {
            ad.show();
            return true;
        } else {
//            startGame(ad);
            return false;

        }
    }


}