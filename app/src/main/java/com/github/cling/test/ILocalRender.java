package com.github.cling.test;

public interface ILocalRender {
    void setPlayURI(String playURI);

    void setVolume(int volume);

    void play();

    void pause();

    void stop();

    int getVolume();

    void setURIMetaData(String currentURIMetaData);

    void record();

    void next();

    void previous();

    void setNextURI(String nextURI);

    void setNextURIMetaData(String nextURI);

    void setPlayMode(String newPlayMode);

    void setRecordQualityMode(String newRecordQualityMode);

    void seek(String unit, String target);
}
