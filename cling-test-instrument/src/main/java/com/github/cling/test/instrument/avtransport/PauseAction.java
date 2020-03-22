package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class PauseAction extends ActionInvocation<RemoteService> {
    public PauseAction(RemoteService service, UnsignedIntegerFourBytes instanceId) {
        super(service.getAction("Pause"));
        setInput("InstanceID", instanceId);
    }
}
