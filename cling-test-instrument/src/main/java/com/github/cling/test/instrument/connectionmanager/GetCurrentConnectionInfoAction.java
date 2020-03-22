package com.github.cling.test.instrument.connectionmanager;

import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.support.model.ConnectionInfo;

public class GetCurrentConnectionInfoAction extends ActionInvocation<RemoteService> {
    public GetCurrentConnectionInfoAction(RemoteService service, int connectionId) {
        super(service.getAction("GetCurrentConnectionInfo"));
        setInput("ConnectionID", connectionId);
    }

    public int getRcsID() {
        return (int) getOutput("RcsID").getValue();
    }

    public int getAVTransportID() {
        return (Integer) getOutput("AVTransportID").getValue();
    }

    public ServiceReference getPeerConnectionManager() {
        return new ServiceReference(getOutput("PeerConnectionManager").toString());
    }

    public int getPeerConnectionID() {
        return (Integer) getOutput("PeerConnectionID").getValue();
    }

    public ConnectionInfo.Direction getDirection() {
        return ConnectionInfo.Direction.valueOf(getOutput("Direction").toString());
    }

    public ConnectionInfo.Status getStatus() {
        return ConnectionInfo.Status.valueOf(getOutput("Status").toString());
    }
}
