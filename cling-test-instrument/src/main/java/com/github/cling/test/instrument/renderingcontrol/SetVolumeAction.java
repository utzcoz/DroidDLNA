package com.github.cling.test.instrument.renderingcontrol;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;

public class SetVolumeAction extends ActionInvocation<RemoteService> {
    public SetVolumeAction(RemoteService service,
                           UnsignedIntegerFourBytes instanceId,
                           String channelName,
                           UnsignedIntegerTwoBytes desiredVolume) {
        super(service.getAction("SetVolume"));
        setInput("InstanceID", instanceId);
        setInput("Channel", channelName);
        setInput("DesiredVolume", desiredVolume);
    }
}
