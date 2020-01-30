package com.github.dlna;

public class Settings {
    private static String UUID = "";

    public static String getRenderName() {
        return "DroidDLNA Local Render";
    }

    public static void setUUID(String uuid) {
        UUID = uuid;
    }

    public static String getUUID() {
        return UUID;
    }
}
