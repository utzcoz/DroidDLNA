package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class SeekAction extends ActionInvocation<RemoteService> {
    public SeekAction(RemoteService service,
                      UnsignedIntegerFourBytes instanceId,
                      String unit,
                      String target) {
        super(service.getAction("Seek"));
        setInput("InstanceID", instanceId);
        setInput("Unit", unit);
        setInput("Target", target);
    }
}
