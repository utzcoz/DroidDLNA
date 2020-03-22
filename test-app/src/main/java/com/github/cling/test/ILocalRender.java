package com.github.cling.test;

public interface ILocalRender {
    void setPlayURI(String playURI);

    String getPlayURI();

    void setVolume(int volume);

    void play();

    void pause();

    void stop();

    int getVolume();

    void setURIMetaData(String currentURIMetaData);

    String getURIMetaData();

    void setNextURI(String nextURI);

    String getNextPlayURI();

    void setNextURIMetaData(String nextURI);

    String getNextURIMetaData();

    void seek(String unit, String target);

    String getSeekUnit();

    String getSeekTarget();
}
