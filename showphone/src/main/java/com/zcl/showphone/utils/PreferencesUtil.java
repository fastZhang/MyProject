package com.zcl.showphone.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class PreferencesUtil {
    public PreferencesUtil() {
    }

    public static void save(Context context, String PrefsName, String key, String val) {
        SharedPreferences settings = context.getSharedPreferences(PrefsName, 0);
        Editor editor = settings.edit();
        editor.putString(key, val);
        editor.commit();
    }

    public static Map<String, ?> getAll(Context context, String PrefsName) {
        SharedPreferences settings = context.getSharedPreferences(PrefsName, 0);
        return settings.getAll();
    }

    public static void remove(Context context, String PrefsName, String key) {
        SharedPreferences settings = context.getSharedPreferences(PrefsName, 0);
        Editor editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void save(Context context, String PrefsName, HashMap<String, String> keyValues) {
        SharedPreferences settings = context.getSharedPreferences(PrefsName, 0);
        Editor editor = settings.edit();
        Iterator iter = keyValues.entrySet().iterator();

        while (iter.hasNext()) {
            Entry<String, String> entry = (Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            editor.putString(key, val);
        }

        editor.commit();
    }

    public static String get(Context context, String PrefsName, String key) {
        try {
            SharedPreferences sharedata = context.getSharedPreferences(PrefsName, 0);
            String val = sharedata.getString(key, (String) null);
            return val;
        } catch (Exception var5) {
            var5.printStackTrace();
        } catch (Throwable var6) {
            var6.printStackTrace();
        }

        return null;
    }
}
