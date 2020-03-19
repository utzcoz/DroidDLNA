package com.github.dlna;

public class ClingLocalRenderer {
    private static final ILocalRender localRender = new ILocalRenderImpl();
    private static IControlPoint controlPoint;

    public static void setControlPoint(IControlPoint controlPoint) {
        ClingLocalRenderer.controlPoint = controlPoint;
    }

    public static IControlPoint getControlPoint() {
        return controlPoint;
    }

    public static ILocalRender getLocalRender() {
        return localRender;
    }

    private static final class ILocalRenderImpl implements ILocalRender {
        private String playURI;
        private int volume;
        private String uriMetaData;

        @Override
        public void setPlayURI(String playURI) {
            this.playURI = playURI;
        }

        @Override
        public void setVolume(int volume) {
            this.volume = volume;
        }

        @Override
        public void play() {

        }

        @Override
        public void pause() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void seek(int position) {

        }

        @Override
        public int getVolume() {
            return 0;
        }

        @Override
        public void setURIMetaData(String uriMetaData) {
            this.uriMetaData = uriMetaData;
        }
    }
}
