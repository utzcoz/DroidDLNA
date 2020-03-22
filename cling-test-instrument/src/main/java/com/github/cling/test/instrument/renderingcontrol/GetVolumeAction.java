package com.github.cling.test.instrument.renderingcontrol;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;

public class GetVolumeAction extends ActionInvocation<RemoteService> {
    public GetVolumeAction(RemoteService service,
                           UnsignedIntegerFourBytes instanceId,
                           String channelName) {
        super(service.getAction("GetVolume"));
        setInput("InstanceID", instanceId);
        setInput("Channel", channelName);
    }

    public UnsignedIntegerTwoBytes getVolume() {
        return (UnsignedIntegerTwoBytes) getOutput("CurrentVolume").getValue();
    }
}
