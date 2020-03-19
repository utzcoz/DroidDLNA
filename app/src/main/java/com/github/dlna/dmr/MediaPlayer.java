package com.github.dlna.dmr;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.github.dlna.DevicesActivity;
import com.github.dlna.MediaListener;
import com.github.dlna.util.Action;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelMute;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

import java.net.URI;

public class MediaPlayer {
    private static final String TAG = "GstMediaPlayer";

    final private UnsignedIntegerFourBytes instanceId;
    final private LastChange avTransportLastChange;
    final private LastChange renderingControlLastChange;

    // We'll synchronize read/writes to these fields
    private volatile TransportInfo currentTransportInfo = new TransportInfo();
    private PositionInfo currentPositionInfo = new PositionInfo();
    private MediaInfo currentMediaInfo = new MediaInfo();
    private double storedVolume;

    private Context context;

    MediaPlayer(UnsignedIntegerFourBytes instanceId, Context context,
                LastChange avTransportLastChange,
                LastChange renderingControlLastChange) {
        super();
        this.instanceId = instanceId;
        this.context = context;
        this.avTransportLastChange = avTransportLastChange;
        this.renderingControlLastChange = renderingControlLastChange;
    }

    UnsignedIntegerFourBytes getInstanceId() {
        return instanceId;
    }

    private LastChange getAvTransportLastChange() {
        return avTransportLastChange;
    }

    private LastChange getRenderingControlLastChange() {
        return renderingControlLastChange;
    }

    synchronized TransportInfo getCurrentTransportInfo() {
        return currentTransportInfo;
    }

    synchronized PositionInfo getCurrentPositionInfo() {
        return currentPositionInfo;
    }

    synchronized MediaInfo getCurrentMediaInfo() {
        return currentMediaInfo;
    }

    synchronized void setURI(URI uri, String type, String name, String currentURIMetaData) {
        Log.i(TAG, "setURI " + uri);

        currentMediaInfo = new MediaInfo(uri.toString(), currentURIMetaData);
        currentPositionInfo = new PositionInfo(1, "", uri.toString());

        getAvTransportLastChange().setEventedValue(getInstanceId(),
                new AVTransportVariable.AVTransportURI(uri),
                new AVTransportVariable.CurrentTrackURI(uri));

        transportStateChanged(TransportState.STOPPED);

        DevicesActivity.setMediaListener(new GstMediaListener());
        // FIXME Add method to pass parameter to DevicesActivity
        //  include "type", "name", and "playURI"
    }

    synchronized void setVolume(double volume) {
        Log.i(TAG, "setVolume " + volume);
        storedVolume = getVolume();

        Intent intent = new Intent();
        intent.setAction(Action.DMR);
        intent.putExtra("helpAction", Action.SET_VOLUME);
        intent.putExtra("volume", volume);

        context.sendBroadcast(intent);

        ChannelMute switchedMute =
                (storedVolume == 0 && volume > 0) || (storedVolume > 0 && volume == 0)
                        ? new ChannelMute(Channel.Master, storedVolume > 0)
                        : null;

        getRenderingControlLastChange().setEventedValue(
                getInstanceId(),
                new RenderingControlVariable.Volume(
                        new ChannelVolume(Channel.Master, (int) (volume * 100))
                ),
                switchedMute != null
                        ? new RenderingControlVariable.Mute(switchedMute)
                        : null
        );
    }

    synchronized void setMute(boolean desiredMute) {
        if (desiredMute && getVolume() > 0) {
            setVolume(0);
        } else if (!desiredMute && getVolume() == 0) {
            setVolume(storedVolume);
        }
    }

    synchronized TransportAction[] getCurrentTransportActions() {
        TransportState state = currentTransportInfo.getCurrentTransportState();
        TransportAction[] actions;

        switch (state) {
            case STOPPED:
                actions = new TransportAction[]{
                        TransportAction.Play
                };
                break;
            case PLAYING:
                actions = new TransportAction[]{
                        TransportAction.Stop,
                        TransportAction.Pause,
                        TransportAction.Seek
                };
                break;
            case PAUSED_PLAYBACK:
                actions = new TransportAction[]{
                        TransportAction.Stop,
                        TransportAction.Pause,
                        TransportAction.Seek,
                        TransportAction.Play
                };
                break;
            default:
                actions = null;
        }
        return actions;
    }

    protected synchronized void transportStateChanged(TransportState newState) {
        TransportState currentTransportState = currentTransportInfo.getCurrentTransportState();
        currentTransportInfo = new TransportInfo(newState);

        getAvTransportLastChange().setEventedValue(
                getInstanceId(),
                new AVTransportVariable.TransportState(newState),
                new AVTransportVariable.CurrentTransportActions(getCurrentTransportActions())
        );
    }

    protected class GstMediaListener implements MediaListener {
        public void pause() {
            transportStateChanged(TransportState.PAUSED_PLAYBACK);
        }

        public void start() {
            transportStateChanged(TransportState.PLAYING);
        }

        public void stop() {
            transportStateChanged(TransportState.STOPPED);
        }

        public void endOfMedia() {
            transportStateChanged(TransportState.NO_MEDIA_PRESENT);
        }

        public void positionChanged(int position) {
            synchronized (MediaPlayer.this) {
                currentPositionInfo = new PositionInfo(1, currentMediaInfo.getMediaDuration(),
                        currentMediaInfo.getCurrentURI(), ModelUtil.toTimeString(position / 1000),
                        ModelUtil.toTimeString(position / 1000));
            }
        }

        public void durationChanged(int duration) {
            synchronized (MediaPlayer.this) {
                String newValue = ModelUtil.toTimeString(duration / 1000);
                currentMediaInfo = new MediaInfo(currentMediaInfo.getCurrentURI(), "",
                        new UnsignedIntegerFourBytes(1), newValue, StorageMedium.NETWORK);

                getAvTransportLastChange().setEventedValue(getInstanceId(),
                        new AVTransportVariable.CurrentTrackDuration(newValue),
                        new AVTransportVariable.CurrentMediaDuration(newValue));
            }
        }
    }

    double getVolume() {
        AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        double v = (double) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "getVolume " + v);
        return v;
    }

    void play() {
        Log.i(TAG, "play");
        sendBroadcastAction(Action.PLAY);
    }

    void pause() {
        Log.i(TAG, "pause");
        sendBroadcastAction(Action.PAUSE);
    }

    void stop() {
        Log.i(TAG, "stop");
        sendBroadcastAction(Action.STOP);
    }

    void seek(int position) {
        Log.i(TAG, "seek " + position);
        Intent intent = new Intent();
        intent.setAction(Action.DMR);
        intent.putExtra("helpAction", Action.SEEK);
        intent.putExtra("position", position);
        context.sendBroadcast(intent);
    }

    private void sendBroadcastAction(String action) {
        Intent intent = new Intent();
        intent.setAction(Action.DMR);
        intent.putExtra("helpAction", action);
        context.sendBroadcast(intent);
    }
}

