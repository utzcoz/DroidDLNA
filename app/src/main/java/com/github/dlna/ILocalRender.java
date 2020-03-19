package com.github.dlna;

public interface ILocalRender {
    void setPlayURI(String playURI);

    void setVolume(int volume);

    void play();

    void pause();

    void stop();

    void seek(int position);

    int getVolume();

    void setURIMetaData(String currentURIMetaData);
}
