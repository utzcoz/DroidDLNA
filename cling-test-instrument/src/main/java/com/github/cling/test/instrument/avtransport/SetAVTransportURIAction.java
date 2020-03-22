package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class SetAVTransportURIAction extends ActionInvocation<RemoteService> {
    public SetAVTransportURIAction(RemoteService service,
                                   UnsignedIntegerFourBytes instanceId,
                                   String currentURI,
                                   String currentURIMetaData) {
        super(service.getAction("SetAVTransportURI"));
        setInput("InstanceID", instanceId);
        setInput("CurrentURI", currentURI);
        setInput("CurrentURIMetaData", currentURIMetaData);
    }
}
