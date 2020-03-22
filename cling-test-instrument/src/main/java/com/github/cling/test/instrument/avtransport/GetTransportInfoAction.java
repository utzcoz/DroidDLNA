package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import org.fourthline.cling.support.model.TransportStatus;

public class GetTransportInfoAction extends ActionInvocation<RemoteService> {
    public GetTransportInfoAction(RemoteService service,
                                  UnsignedIntegerFourBytes instanceId) {
        super(service.getAction("GetTransportInfo"));
        setInput("InstanceID", instanceId);
    }

    public TransportInfo getTransportInfo() {
        return new TransportInfo(
                TransportState.valueOrCustomOf(
                        (String) getOutput("CurrentTransportState").getValue()
                ),
                TransportStatus.valueOrCustomOf(
                        (String) getOutput("CurrentTransportStatus").getValue()
                ),
                (String) getOutput("CurrentSpeed").getValue()
        );
    }
}
