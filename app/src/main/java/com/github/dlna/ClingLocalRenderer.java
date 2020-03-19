package com.github.dlna;

public class ClingLocalRenderer {
    private static MediaListener mediaListener;

    public static void setMediaListener(MediaListener mediaListener) {
        ClingLocalRenderer.mediaListener = mediaListener;
    }

    public static MediaListener getMediaListener() {
        return mediaListener;
    }
}
