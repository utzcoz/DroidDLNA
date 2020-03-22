package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class SetPlayModeAction extends ActionInvocation<RemoteService> {
    public SetPlayModeAction(RemoteService service,
                             UnsignedIntegerFourBytes instanceId,
                             String newPlayMode) {
        super(service.getAction("SetPlayMode"));
        setInput("InstanceID", instanceId);
        setInput("NewPlayMode", newPlayMode);
    }
}
