package com.github.dlna;

public interface MediaListener {
    void pause();

    void start();

    void stop();

    void endOfMedia();

    void positionChanged(int position);

    void durationChanged(int duration);
}
