package com.github.cling.test.instrument.renderingcontrol;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class ListPresetsAction extends ActionInvocation<RemoteService> {
    public ListPresetsAction(RemoteService service, UnsignedIntegerFourBytes instanceId) {
        super(service.getAction("ListPresets"));
        setInput("InstanceID", instanceId);
    }

    public String getCurrentPresentNameList() {
        return (String) getOutput("CurrentPresetNameList").getValue();
    }
}
