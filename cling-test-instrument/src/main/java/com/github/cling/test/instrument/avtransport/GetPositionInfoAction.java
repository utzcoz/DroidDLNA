package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.PositionInfo;

public class GetPositionInfoAction extends ActionInvocation<RemoteService> {
    public GetPositionInfoAction(RemoteService service,
                                 UnsignedIntegerFourBytes instanceId) {
        super(service.getAction("GetPositionInfo"));
        setInput("InstanceID", instanceId);
    }

    public PositionInfo getPositionInfo() {
        return new PositionInfo(
                ((UnsignedIntegerFourBytes) getOutput("Track").getValue()).getValue(),
                (String) getOutput("TrackDuration").getValue(),
                (String) getOutput("TrackMetaData").getValue(),
                (String) getOutput("TrackURI").getValue(),
                (String) getOutput("RelTime").getValue(),
                (String) getOutput("AbsTime").getValue(),
                (Integer) getOutput("RelCount").getValue(),
                (Integer) getOutput("AbsCount").getValue()
        );
    }
}
