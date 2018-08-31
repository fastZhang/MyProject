package com.zcl.showphone.utils;

import android.content.Context;

import java.io.IOException;

public class AssetsPathUtils {

    public static String[] getPathList(Context context, String header) throws IOException {
        String[] array = context.getAssets().list(header);

        return array;
    }
}
