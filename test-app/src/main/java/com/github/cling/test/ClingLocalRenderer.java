package com.github.cling.test;

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
        private String nextPlayURI;
        private String nextURIMetaData;
        private String seekUnit;
        private String seekTarget;

        @Override
        public void setPlayURI(String playURI) {
            this.playURI = playURI;
        }

        @Override
        public String getPlayURI() {
            return playURI;
        }

        @Override
        public void setVolume(int volume) {
            this.volume = volume;
        }

        @Override
        public void play() {
            getControlPoint().start();
        }

        @Override
        public void pause() {
            getControlPoint().pause();
        }

        @Override
        public void stop() {
            getControlPoint().stop();
        }

        @Override
        public int getVolume() {
            return volume;
        }

        @Override
        public void setURIMetaData(String uriMetaData) {
            this.uriMetaData = uriMetaData;
        }

        @Override
        public String getURIMetaData() {
            return uriMetaData;
        }

        @Override
        public void setNextURI(String nextURI) {
            this.nextPlayURI = nextURI;
        }

        @Override
        public String getNextPlayURI() {
            return nextPlayURI;
        }

        @Override
        public void setNextURIMetaData(String nextURIMetaData) {
            this.nextURIMetaData = nextURIMetaData;
        }

        @Override
        public String getNextURIMetaData() {
            return nextURIMetaData;
        }

        @Override
        public void seek(String unit, String target) {
            seekUnit = unit;
            seekTarget = target;
        }

        @Override
        public String getSeekUnit() {
            return seekUnit;
        }

        @Override
        public String getSeekTarget() {
            return seekTarget;
        }
    }
}
