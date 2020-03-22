package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class PlayAction extends ActionInvocation<RemoteService> {
    public PlayAction(RemoteService service, UnsignedIntegerFourBytes instanceId) {
        super(service.getAction("Play"));
        setInput("InstanceID", instanceId);
    }
}
