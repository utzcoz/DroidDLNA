package com.github.cling.test.instrument.connectionmanager;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;

public class GetCurrentConnectionIDsAction extends ActionInvocation<RemoteService> {
    public GetCurrentConnectionIDsAction(RemoteService service) {
        super(service.getAction("GetCurrentConnectionIDs"));
    }

    public String getCurrentConnectionIDs() {
        return (String) getOutput("ConnectionIDs").getValue();
    }
}
