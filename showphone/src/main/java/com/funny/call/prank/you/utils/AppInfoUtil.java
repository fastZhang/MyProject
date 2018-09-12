package com.funny.call.prank.you.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.funny.call.prank.you.BuildConfig;

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

    public static boolean isGP() {
        return BuildConfig.FLAVOR.equals(BuildConfig.gp);
    }

}
