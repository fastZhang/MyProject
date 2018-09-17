package com.funny.call.prank.you;

import android.app.Application;
import android.os.Build;

import com.google.android.gms.ads.MobileAds;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.funny.call.prank.you.service.TraceServiceImpl;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        DaemonEnv.initialize(this, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        // Initialize the Mobile Ads SDK.

        if (BuildConfig.FLAVOR.equals(BuildConfig.gp)) {
            MobileAds.initialize(this, "ca-app-pub-4409839171902420~1510610499");
        }

        UMConfigure.init(this, "5b97bad68f4a9d7dc7000093", BuildConfig.FLAVOR, UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        // [可选]设置是否打开debug输出，上线时请关闭，Logcat标签为"MtaSDK"
//        StatConfig.setDebugEnable(true);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this);
    }


}
