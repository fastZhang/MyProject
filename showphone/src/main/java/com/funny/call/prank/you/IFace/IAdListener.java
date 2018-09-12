package com.funny.call.prank.you.IFace;

import com.google.android.gms.ads.InterstitialAd;

public interface IAdListener {
    void onAdLoaded(InterstitialAd ad);
    void onAdClosed(InterstitialAd ad);


}
