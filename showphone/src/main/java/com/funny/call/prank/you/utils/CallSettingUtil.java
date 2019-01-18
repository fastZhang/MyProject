package com.funny.call.prank.you.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.funny.call.prank.you.App;
import com.funny.call.prank.you.activity.BlurRinger;
import com.funny.call.prank.you.activity.LG2Ringer;
import com.funny.call.prank.you.activity.MiRinger;
import com.funny.call.prank.you.activity.NormalRinger;
import com.funny.call.prank.you.activity.SMRinger;

public class CallSettingUtil {


    public enum CallTheme {
        MI, ANDROID5, BLUR, SAMSUNG, LG2//436EEE
    }

    public static void setCallTheme(Context context, CallTheme theme) {

        PreferencesUtil.save(context, "setting", "theme_call", theme.toString());

    }

    public static CallTheme getCallTheme() {
        String name = PreferencesUtil.get(App.context, "setting", "theme_call");
        if (name == null) return CallTheme.BLUR;
        else
            return CallTheme.valueOf(name);
    }


    public static Class<?> getSetThemeClass(Intent intent) {

        return CallSettingUtil.getThemeClass(getCallTheme(), intent);

    }


    public static Class<?> getThemeClass(CallTheme theme, Intent data) {

        if (theme == CallTheme.MI) {
            new MiRinger(App.context).setIntent(data).show();

            return MiRinger.class;

        } else if (theme == CallTheme.BLUR) {
            new BlurRinger(App.context).setIntent(data).show();

            return BlurRinger.class;
//            return BlurFakeRingerActivity.class;

        } else if (theme == CallTheme.ANDROID5) {
            new NormalRinger(App.context).setIntent(data).show();

            return NormalRinger.class;
        } else if (theme == CallTheme.SAMSUNG) {
            new SMRinger(App.context).setIntent(data).show();

            return SMRinger.class;
        } else if (theme == CallTheme.LG2) {
            new LG2Ringer(App.context).setIntent(data).show();

            return LG2Ringer.class;
        }


        return BlurRinger.class;

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
