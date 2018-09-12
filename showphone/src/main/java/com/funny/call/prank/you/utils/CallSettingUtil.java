package com.funny.call.prank.you.utils;

import android.app.Application;
import android.content.Context;

import com.funny.call.prank.you.FakeRingerActivity;
import com.funny.call.prank.you.activity.BlurFakeRingerActivity;
import com.funny.call.prank.you.activity.mi.MIFakeRingerActivity;
import com.funny.call.prank.you.activity.mi.SmEightFakeRingerActivity;

public class CallSettingUtil {


    public enum CallTheme {
        MI, ANDROID5, BLUR, SAMSUNG //436EEE
    }

    public static void setCallTheme(Context context, CallTheme theme) {

        PreferencesUtil.save(context, "setting", "theme_call", theme.toString());

    }

    public static CallTheme getCallTheme(Context context) {
        String name = PreferencesUtil.get(context, "setting", "theme_call");
        if (name == null) return CallTheme.BLUR;
        else
            return CallTheme.valueOf(name);
    }


    public static Class<?> getSetThemeClass(Application application) {

        return CallSettingUtil.getThemeClass(application, getCallTheme(application));

    }


    public static Class<?> getThemeClass(Application application, CallTheme theme) {

        if (theme == CallTheme.MI) {
            return MIFakeRingerActivity.class;

        } else if (theme == CallTheme.BLUR) {
            return BlurFakeRingerActivity.class;

        } else if (theme == CallTheme.ANDROID5) {
            return FakeRingerActivity.class;
        } else if (theme == CallTheme.SAMSUNG) {
            return SmEightFakeRingerActivity.class;
        }


        return BlurFakeRingerActivity.class;

    }


    public static boolean isThisCalltheme(Context application, CallTheme theme) {

        String name = PreferencesUtil.get(application, "setting", "theme_call");
        if (name == null) {
            return false;
        } else if (CallTheme.valueOf(name) == theme)
            return true;

        return false;

    }


}
