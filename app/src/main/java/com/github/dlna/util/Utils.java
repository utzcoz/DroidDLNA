package com.github.dlna.util;

public class Utils {

    public static final String MANUFACTURER = android.os.Build.MANUFACTURER;
    public static final String DMR_NAME = "MSI MediaRenderer";

    public static final String DMR_DESC = "MSI MediaRenderer";
    public static final String DMR_MODEL_URL = "http://4thline.org/projects/cling/mediarenderer/";

    public static int getRealTime(String paramString) {
        int i = paramString.indexOf(":");
        int j = 0;
        if (i > 0) {
            String[] arrayOfString = paramString.split(":");
            j = Integer.parseInt(arrayOfString[2]) + 60
                    * Integer.parseInt(arrayOfString[1]) + 3600
                    * Integer.parseInt(arrayOfString[0]);
        }
        return j;
    }

    public static String secToTime(long paramLong) {
        int time = Long.valueOf(paramLong).intValue();
        String timeStr;
        int hour;
        int minute;
        int second;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
                        + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }
}
