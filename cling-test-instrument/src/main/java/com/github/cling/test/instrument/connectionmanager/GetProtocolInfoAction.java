package com.github.cling.test.instrument.connectionmanager;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;

public class GetProtocolInfoAction extends ActionInvocation<RemoteService> {
    public GetProtocolInfoAction(RemoteService service) {
        super(service.getAction("GetProtocolInfo"));
    }

    public String getSinkProtocolInfo() {
        return (String) getOutput("Sink").getValue();
    }

    public String getSourceProtocolInfo() {
        return (String) getOutput("Source").getValue();
    }
}
