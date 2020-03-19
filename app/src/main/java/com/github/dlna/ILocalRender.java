package com.github.dlna;

public interface ILocalRender {
    void setType(String type);

    void setName(String name);

    void setPlayURI(String playURI);

    void setVolume(double volume);

    void play();

    void pause();

    void stop();

    void seek(int position);

    double getVolume();
}