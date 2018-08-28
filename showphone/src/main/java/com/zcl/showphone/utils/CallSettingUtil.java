package com.zcl.showphone.utils;

import android.app.Application;
import android.content.Context;

import com.zcl.showphone.FakeRingerActivity;
import com.zcl.showphone.activity.BlurFakeRingerActivity;
import com.zcl.showphone.activity.mi.MIFakeRingerActivity;

public class CallSettingUtil {


    public enum CallTheme {
        MI, ANDROID5, BLUR //436EEE
    }

    public static void setCallTheme(Context context, CallTheme theme) {

        PreferencesUtil.save(context, "setting", "theme_call", theme.toString());

    }

    public static CallTheme getCallTheme(Context context) {
        String name = PreferencesUtil.get(context, "setting", "theme_call");
        if (name == null) return CallTheme.ANDROID5;
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
