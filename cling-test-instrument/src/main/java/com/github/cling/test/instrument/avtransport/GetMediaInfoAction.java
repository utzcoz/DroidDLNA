package com.github.cling.test.instrument.avtransport;

import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.RecordMediumWriteStatus;
import org.fourthline.cling.support.model.StorageMedium;

import java.util.Map;

public class GetMediaInfoAction extends ActionInvocation<RemoteService> {
    public GetMediaInfoAction(RemoteService service,
                              UnsignedIntegerFourBytes instanceId) {
        super(service.getAction("GetMediaInfo"));
        setInput("InstanceID", instanceId);
    }

    public MediaInfo getMediaInfo() {
        Map<String, ActionArgumentValue<RemoteService>> args = getOutputMap();
        return new MediaInfo(
                (String) getOutput("CurrentURI").getValue(),
                (String) getOutput("CurrentURIMetaData").getValue(),
                (String) getOutput("NextURI").getValue(),
                (String) getOutput("NextURIMetaData").getValue(),
                (UnsignedIntegerFourBytes) getOutput("NrTracks").getValue(),
                (String) getOutput("MediaDuration").getValue(),
                StorageMedium.valueOrVendorSpecificOf(
                        (String) getOutput("PlayMedium").getValue()
                ),
                StorageMedium.valueOrVendorSpecificOf(
                        (String) getOutput("RecordMedium").getValue()
                ),
                RecordMediumWriteStatus.valueOrUnknownOf(
                        (String) getOutput("WriteStatus").getValue()
                )
        );
    }
}
