package com.zcl.showphone.IFace;

import com.google.android.gms.ads.InterstitialAd;

public interface IAdListener {
    void onAdLoaded(InterstitialAd ad);
    void onAdClosed(InterstitialAd ad);


}
