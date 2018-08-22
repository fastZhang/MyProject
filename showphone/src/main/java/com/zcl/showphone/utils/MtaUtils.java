package com.zcl.showphone.utils;

import android.content.Context;

import com.tencent.stat.StatConfig;

public class MtaUtils {


    public static boolean isAppLive() {
        // 获取在线参数onlineKey
        String onlineValue = StatConfig.getCustomProperty("applive", "1");
        if (!onlineValue.equalsIgnoreCase("0")) {
            return true;
            // do something
        }

        return false;
    }
}
