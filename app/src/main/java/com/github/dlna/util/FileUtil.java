package com.github.dlna.util;

import android.os.Environment;

public class FileUtil {

    public static final String LOGO = "ic_launcher.png";
    public static final String VIDEO_THUMB_PATH = "/msi/.videothumb";
    public static final String IMAGE_DOWNLOAD_PATH = "/msi/downloadimages/";

    public static String getSDPath() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return "";

    }

    public static String getFileSuffix(String pathName) {
        String suffix = "";

        if (null != pathName) {
            int lastIndexOf = pathName.lastIndexOf(".");
            if (-1 != lastIndexOf) {
                suffix = pathName.substring(lastIndexOf);
            }
        }

        return suffix;
    }

}
