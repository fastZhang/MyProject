package com.zcl.showphone;

import android.app.Application;
import android.os.Build;

import com.google.android.gms.ads.MobileAds;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.u3k.app.SdkMain;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.zcl.showphone.service.TraceServiceImpl;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        DaemonEnv.initialize(this, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        // Initialize the Mobile Ads SDK.

        if (BuildConfig.FLAVOR.equals(BuildConfig.gp)) {

            MobileAds.initialize(this, "ca-app-pub-7835308551963221~5623021028");


        }
        mAppIdKey = SdkMain.init(this, APPID, CHANNELID);


        // [可选]设置是否打开debug输出，上线时请关闭，Logcat标签为"MtaSDK"
//        StatConfig.setDebugEnable(true);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this);
    }


    private String APPID = "1180829000127151848";
    public static String AD_ID = "u3k-1180829000127151848-02-20180829171259";
    private String CHANNELID = BuildConfig.gp;

    public static String mAppIdKey;

}
