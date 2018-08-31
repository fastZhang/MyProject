package com.zcl.showphone.IFace;

import com.google.android.gms.ads.InterstitialAd;

public interface IAdListener {
    void onAdLoaded(InterstitialAd ad);
    void onASAdLoaded(com.u3k.app.external.InterstitialAd ad);
    void onAdClosed(InterstitialAd ad);


}
