package com.zcl.showphone;

import android.app.Application;
import android.os.Build;

import com.miui.zeus.mimo.sdk.MimoSdk;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.zcl.showphone.service.TraceServiceImpl;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //需要在 Application 的 onCreate() 中调用一次 DaemonEnv.initialize()
        DaemonEnv.initialize(this, TraceServiceImpl.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        // Initialize the Mobile Ads SDK.


//        mAppIdKey = SdkMain.init(this, APPID, CHANNELID);
        UMConfigure.init(this, "5b7d341bf29d9864d300001a", BuildConfig.FLAVOR, UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        // [可选]设置是否打开debug输出，上线时请关闭，Logcat标签为"MtaSDK"
//        StatConfig.setDebugEnable(true);
        // 基础统计API
        StatService.registerActivityLifecycleCallbacks(this);

        MimoSdk.init(this, "2882303761517860313", "5721786083313", "nPAeoAvi6Y/JiODrbCYZ7A==");
//        MimoSdk.setDebugOn(); // 打开调试，输出调试信息
//        MimoSdk.setStageOn(); // 打开测试请求开关，请求测试广告
    }


    private String APPID = "1180829000127151848";
    public static String AD_ID = "u3k-1180829000127151848-02-20180829171259";
    private String CHANNELID = BuildConfig.gp;

    public static String mAppIdKey;

}
