package com.github.cling.test;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.AVTransportErrorCode;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.avtransport.AbstractAVTransportService;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportSettings;

import java.util.Map;

public class AVTransportService extends AbstractAVTransportService {
    final private Map<UnsignedIntegerFourBytes, MediaPlayer> players;

    AVTransportService(LastChange lastChange,
                       Map<UnsignedIntegerFourBytes, MediaPlayer> players) {
        super(lastChange);
        this.players = players;
    }

    private Map<UnsignedIntegerFourBytes, MediaPlayer> getPlayers() {
        return players;
    }

    private MediaPlayer getInstance(UnsignedIntegerFourBytes instanceId)
            throws AVTransportException {
        MediaPlayer player = getPlayers().get(instanceId);
        if (player == null) {
            throw new AVTransportException(AVTransportErrorCode.INVALID_INSTANCE_ID);
        }
        return player;
    }

    @Override
    public void setAVTransportURI(UnsignedIntegerFourBytes instanceId,
                                  String currentURI,
                                  String currentURIMetaData) throws AVTransportException {
        getInstance(instanceId).setURI(currentURI, currentURIMetaData);
    }

    @Override
    public MediaInfo getMediaInfo(UnsignedIntegerFourBytes instanceId)
            throws AVTransportException {
        return getInstance(instanceId).getCurrentMediaInfo();
    }

    @Override
    public TransportInfo getTransportInfo(UnsignedIntegerFourBytes instanceId)
            throws AVTransportException {
        return getInstance(instanceId).getCurrentTransportInfo();
    }

    @Override
    public PositionInfo getPositionInfo(UnsignedIntegerFourBytes instanceId)
            throws AVTransportException {
        return getInstance(instanceId).getCurrentPositionInfo();
    }

    @Override
    public DeviceCapabilities getDeviceCapabilities(UnsignedIntegerFourBytes instanceId) {
        return new DeviceCapabilities(new StorageMedium[]{StorageMedium.NETWORK});
    }

    @Override
    public TransportSettings getTransportSettings(UnsignedIntegerFourBytes instanceId) {
        return new TransportSettings(PlayMode.NORMAL);
    }

    @Override
    public void stop(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        getInstance(instanceId).stop();
    }

    @Override
    public void play(UnsignedIntegerFourBytes instanceId, String speed)
            throws AVTransportException {
        getInstance(instanceId).play();
    }

    @Override
    public void pause(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        getInstance(instanceId).pause();
    }

    @Override
    public void record(UnsignedIntegerFourBytes instanceId) {
        // Do nothing
    }

    @Override
    public void seek(UnsignedIntegerFourBytes instanceId, String unit, String target)
            throws AVTransportException {
        getInstance(instanceId).seek(unit, target);
    }

    @Override
    public void next(UnsignedIntegerFourBytes instanceId) {
        // Do nothing
    }

    @Override
    public void previous(UnsignedIntegerFourBytes instanceId) {
        // Do nothing
    }

    @Override
    public void setNextAVTransportURI(UnsignedIntegerFourBytes instanceId,
                                      String nextURI,
                                      String nextURIMetaData) throws AVTransportException {
        getInstance(instanceId).setNextAVTransportURI(nextURI, nextURIMetaData);
    }

    @Override
    public void setPlayMode(UnsignedIntegerFourBytes instanceId, String newPlayMode) {
        // Do nothing
    }

    @Override
    public void setRecordQualityMode(UnsignedIntegerFourBytes instanceId,
                                     String newRecordQualityMode) {
        // Do nothing
    }

    @Override
    protected TransportAction[] getCurrentTransportActions(UnsignedIntegerFourBytes instanceId)
            throws Exception {
        return getInstance(instanceId).getCurrentTransportActions();
    }

    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        UnsignedIntegerFourBytes[] ids = new UnsignedIntegerFourBytes[getPlayers().size()];
        int i = 0;
        for (UnsignedIntegerFourBytes id : getPlayers().keySet()) {
            ids[i] = id;
            i++;
        }
        return ids;
    }
}
