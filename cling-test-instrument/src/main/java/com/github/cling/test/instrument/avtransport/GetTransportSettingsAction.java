package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.RecordQualityMode;
import org.fourthline.cling.support.model.TransportSettings;

public class GetTransportSettingsAction extends ActionInvocation<RemoteService> {
    public GetTransportSettingsAction(RemoteService service, UnsignedIntegerFourBytes instanceId) {
        super(service.getAction("GetTransportSettings"));
        setInput("InstanceID", instanceId);
    }

    public TransportSettings getTransportSettings() {
        return new TransportSettings(
                PlayMode.valueOf((String) getOutput("PlayMode").getValue()),
                RecordQualityMode.valueOrExceptionOf(
                        (String) getOutput("RecQualityMode").getValue()
                )
        );
    }
}
