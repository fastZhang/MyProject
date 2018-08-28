package com.zcl.showphone.utils;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PicPathUtils {

    private static final String hander1 = "img_header";
    private static final String hander2 = "header_work";

    private static final String bg = "/bg";

    private static final String shel = "file:///android_asset/";


    public static ArrayList<String> getHeaderPicPath(Context context) throws IOException {
        ArrayList<String> imagesPath = new ArrayList<>();
        List<String> imagesPathTemp = new ArrayList<>();
        Random random = new Random();

        imagesPathTemp.addAll(getHeaderPicPath(context, hander1));
        imagesPathTemp.addAll(getHeaderPicPath(context, hander2));


        while (imagesPathTemp.size() > 0) {
            int i = random.nextInt(imagesPathTemp.size());
            imagesPath.add(imagesPathTemp.remove(i));
        }


        return imagesPath;

    }

    public static String wrapHeaderPicPath(Context context, String path) {
        String string = null;
        try {
            if (path.contains(hander1)) {

                string = getWrapPath(context, hander1, path);

            } else if (path.contains(hander2)) {
                string = getWrapPath(context, hander2, path);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return string;
    }

    private static String getWrapPath(Context context, String head, String path) throws IOException {
        int i = getHeaderPicPath(context, head).indexOf(path);
        return getHeaderPicPath(context, head + bg).get(i);


    }

    public static List<String> getHeaderPicPath(Context context, String header) throws IOException {
        List<String> imagesPath = new ArrayList<>();


        final String HEADER = shel + header + "/";

        List<String> stringList = Arrays.asList(context.getAssets().list(header));


        for (String s : stringList) {
            if (s.contains("."))
                imagesPath.add(HEADER + s);
        }

        return imagesPath;

    }
}
