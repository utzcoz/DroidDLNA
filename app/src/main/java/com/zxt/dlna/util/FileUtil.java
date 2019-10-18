package com.zxt.dlna.util;

import android.os.Environment;
import android.text.TextUtils;

import com.zxt.dlna.util.DevMountInfo.DevInfo;

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

    public static String getFoldName(String path) {
        String sdPath = getSDPath();
        String foldPath = null;
        if (null != sdPath && path.contains(sdPath)) {
            if (!TextUtils.isEmpty(sdPath)) {
                int beginIndex = path.indexOf("/", sdPath.length()) + 1;
                int lastIndex = path.indexOf("/", sdPath.length() + 1);
                if (lastIndex == -1) {
                    lastIndex = path.length() - 1;
                }
                foldPath = path.substring(beginIndex, lastIndex);
                if (foldPath.contains(".")) {
                    foldPath = path.substring(beginIndex + 1, lastIndex);
                }
            }
        } else {
            String outSdPath = getOutSdPath();
            if (null != outSdPath && !TextUtils.isEmpty(outSdPath)) {
                int beginIndex = path.indexOf("/", outSdPath.length()) + 1;
                int lastIndex = path.indexOf("/", outSdPath.length() + 1);
                if (lastIndex == -1) {
                    lastIndex = path.length() - 1;
                }
                foldPath = "OUTSD-" + path.substring(beginIndex, lastIndex);
                if (foldPath.contains(".")) {
                    foldPath = path.substring(beginIndex + 1, lastIndex);
                }
            }
        }
        return foldPath;
    }

    public static String getOutSdPath() {
        String sdcard = "";
        DevMountInfo dev = DevMountInfo.getInstance();
        DevInfo info = dev.getExternalInfo();// Internal SD Card Informations
        if (null != info) {
            sdcard = info.getPath();
        } else {
            info = dev.getInternalInfo();// External SD Card Informations
            if (null != info) {
                sdcard = info.getPath();
            }
        }
        return sdcard;

    }
}
