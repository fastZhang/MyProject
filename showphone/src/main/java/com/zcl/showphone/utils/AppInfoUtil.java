package com.zcl.showphone.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppInfoUtil {


    public static String getVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;

        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException var5) {
            var5.printStackTrace();
            return null;
        }
    }
}
