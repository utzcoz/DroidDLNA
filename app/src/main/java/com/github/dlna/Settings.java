package com.github.dlna;

public class Settings {
    private static String UUID = "";

    public static boolean getRenderOn() {
        return true;
    }

    public static String getRenderName() {
        return "DroidDLNA Local Render";
    }

    public static String getDeviceName() {
        return "DroidDLNA Media Server";
    }

    public static int getSlideTime() {
        return 5;
    }

    public static void setUUID(String uuid) {
        UUID = uuid;
    }

    public static String getUUID() {
        return UUID;
    }
}
