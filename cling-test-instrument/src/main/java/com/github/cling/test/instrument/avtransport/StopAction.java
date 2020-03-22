package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class StopAction extends ActionInvocation<RemoteService> {
    public StopAction(RemoteService service, UnsignedIntegerFourBytes instanceId) {
        super(service.getAction("Stop"));
        setInput("InstanceID", instanceId);
    }
}
