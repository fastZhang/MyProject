package com.funny.call.prank.you.ad;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.funny.call.prank.you.BuildConfig;
import com.funny.call.prank.you.IFace.IAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class LoadIBannerAd {


    static LoadIBannerAd instance;


    public static LoadIBannerAd getInstance(Activity context) {
        if (null == instance) {

            instance = new LoadIBannerAd(context);
        }

        return instance;
    }


    private LoadIBannerAd(Activity context) {

        initBanner(context);
    }


    AdView adView;

    private void initBanner(Activity context) {
        adView = new AdView(context);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId("ca-app-pub-4409839171902420/7969041782");

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }
        });


    }


    public LoadIBannerAd loadBanner() {
        if (adView != null && !adView.isLoading()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        return this;

    }

    public void showBanner(FrameLayout fl_main) {
        removeAd();

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;
        fl_main.addView(adView, 0, lp);

    }


    public void removeAd() {

        ViewParent viewParent = adView.getParent();
        if (viewParent != null) {
            ((ViewGroup) viewParent).removeAllViews();
        }
    }

}