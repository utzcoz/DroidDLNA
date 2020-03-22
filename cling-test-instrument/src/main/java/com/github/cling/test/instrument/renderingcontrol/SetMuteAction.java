package com.github.cling.test.instrument.renderingcontrol;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class SetMuteAction extends ActionInvocation<RemoteService> {
    public SetMuteAction(RemoteService service,
                         UnsignedIntegerFourBytes instanceId,
                         String channelName,
                         boolean desiredMute) {
        super(service.getAction("SetMute"));
        setInput("InstanceID", instanceId);
        setInput("Channel", channelName);
        setInput("DesiredMute", desiredMute);
    }
}
