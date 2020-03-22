package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.RecordQualityMode;
import org.fourthline.cling.support.model.StorageMedium;

public class GetDeviceCapabilitiesAction extends ActionInvocation<RemoteService> {
    public GetDeviceCapabilitiesAction(RemoteService service,
                                       UnsignedIntegerFourBytes instanceId) {
        super(service.getAction("GetDeviceCapabilities"));
        setInput("InstanceID", instanceId);
    }

    public DeviceCapabilities getDeviceCapabilities() {
        return new DeviceCapabilities(
                StorageMedium.valueOfCommaSeparatedList(
                        (String) getOutput("PlayMedia").getValue()
                ),
                StorageMedium.valueOfCommaSeparatedList(
                        (String) getOutput("RecMedia").getValue()
                ),
                RecordQualityMode.valueOfCommaSeparatedList(
                        (String) getOutput("RecQualityModes").getValue()
                )
        );
    }
}
