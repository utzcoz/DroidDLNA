package com.github.cling.test;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.renderingcontrol.AbstractAudioRenderingControl;
import org.fourthline.cling.support.renderingcontrol.RenderingControlErrorCode;
import org.fourthline.cling.support.renderingcontrol.RenderingControlException;

import java.util.Map;

public class AudioRenderingControl extends AbstractAudioRenderingControl {
    final private Map<UnsignedIntegerFourBytes, MediaPlayer> players;

    AudioRenderingControl(LastChange lastChange,
                          Map<UnsignedIntegerFourBytes, MediaPlayer> players) {
        super(lastChange);
        this.players = players;
    }

    private Map<UnsignedIntegerFourBytes, MediaPlayer> getPlayers() {
        return players;
    }

    private MediaPlayer getInstance(UnsignedIntegerFourBytes instanceId)
            throws RenderingControlException {
        MediaPlayer player = getPlayers().get(instanceId);
        if (player == null) {
            throw new RenderingControlException(RenderingControlErrorCode.INVALID_INSTANCE_ID);
        }
        return player;
    }

    @Override
    public boolean getMute(UnsignedIntegerFourBytes instanceId, String channelName)
            throws RenderingControlException {
        return getInstance(instanceId).getVolume() == 0;
    }

    @Override
    public void setMute(UnsignedIntegerFourBytes instanceId,
                        String channelName,
                        boolean desiredMute) throws RenderingControlException {
        getInstance(instanceId).setMute(desiredMute);
    }

    @Override
    public UnsignedIntegerTwoBytes getVolume(UnsignedIntegerFourBytes instanceId,
                                             String channelName) throws RenderingControlException {
        int vol = getInstance(instanceId).getVolume();
        return new UnsignedIntegerTwoBytes(vol);
    }

    @Override
    public void setVolume(UnsignedIntegerFourBytes instanceId,
                          String channelName,
                          UnsignedIntegerTwoBytes desiredVolume) throws RenderingControlException {
        Long desiredValue = desiredVolume.getValue();
        int vol = desiredValue == null ? 0 : desiredValue.intValue();
        getInstance(instanceId).setVolume(vol);
    }

    @Override
    protected Channel[] getCurrentChannels() {
        return Utils.getChannels();
    }

    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        UnsignedIntegerFourBytes[] ids = new UnsignedIntegerFourBytes[1];
        ids[0] = Utils.getDefaultInstanceId();
        return ids;
    }
}