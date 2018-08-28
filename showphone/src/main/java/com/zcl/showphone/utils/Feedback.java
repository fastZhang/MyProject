package com.zcl.showphone.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import com.zcl.showphone.R;


public class Feedback {
    private String appName;
    private String emailAddress;
    private Context context;
    private final String MT = "mailto:";

    public Feedback(Activity activity) {
        this.context = activity;
        this.appName = activity.getString(R.string.app_name);

        this.emailAddress = MT + (AppInfoUtil.isGP() ? "johnkuok568@gmail.com" : "zhangl0186@gmail.com");
    }

    public void startFeedback() {
        emailUs();
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    private void emailUs() {
//        Intent data = new Intent(Intent.ACTION_SEND);
//        data.setData(Uri.parse(this.emailAddress));
//        data.putExtra("android.intent.extra.SUBJECT", "for " + this.appName);
//        data.putExtra("android.intent.extra.TEXT", "App Name: " + this.appName + " android\nApp Version:" + this.getVersion(this.context) + "\nSystem Version:" + Build.VERSION.RELEASE + "\nPhone:" + Build.MODEL + "\n\nYour Suggestion:\n");
//        this.context.startActivity(data);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(emailAddress));
        intent.putExtra("android.intent.extra.SUBJECT", "for " + this.appName);
        intent.putExtra("android.intent.extra.TEXT", "App Name: " + this.appName + " android\nApp Version:" + this.getVersion(this.context) + "\nSystem Version:" + Build.VERSION.RELEASE + "\nPhone:" + Build.MODEL + "\n\nYour Suggestion:\n");
        this.context.startActivity(intent);
    }

    private String getVersion(Context context) {
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
