package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class SetNextAVTransportURIAction extends ActionInvocation<RemoteService> {
    public SetNextAVTransportURIAction(RemoteService service,
                                       UnsignedIntegerFourBytes instanceId,
                                       String nextURI,
                                       String nextURIMetaData) {
        super(service.getAction("SetNextAVTransportURI"));
        setInput("InstanceID", instanceId);
        setInput("NextURI", nextURI);
        setInput("NextURIMetaData", nextURIMetaData);
    }
}
