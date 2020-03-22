package com.github.cling.test;

public interface IControlPoint {
    void pause();

    void start();

    void stop();

    void endOfMedia();

    void positionChanged(int position);

    void durationChanged(int duration);
}
