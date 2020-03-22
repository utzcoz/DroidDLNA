package com.github.cling.test.instrument.renderingcontrol;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class GetMuteAction extends ActionInvocation<RemoteService> {
    public GetMuteAction(RemoteService service,
                         UnsignedIntegerFourBytes instanceId,
                         String channelName) {
        super(service.getAction("GetMute"));
        setInput("InstanceID", instanceId);
        setInput("Channel", channelName);
    }

    public boolean getMute() {
        return (Boolean) getOutput("CurrentMute").getValue();
    }
}
