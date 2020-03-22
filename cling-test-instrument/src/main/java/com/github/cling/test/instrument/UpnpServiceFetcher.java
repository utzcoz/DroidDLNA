package com.github.cling.test.instrument;

import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDAServiceId;

import static org.junit.Assert.assertNotNull;

public class UpnpServiceFetcher {
    public static RemoteService getService(ControlPointUpnpService upnpService,
                                           String serviceName) {
        RemoteDevice remoteDevice = TestHelper.searchRemoteDevice(upnpService);
        assertNotNull(remoteDevice);
        ServiceId serviceId = new UDAServiceId(serviceName);
        RemoteService connectionManagerService = remoteDevice.findService(serviceId);
        assertNotNull(connectionManagerService);
        return connectionManagerService;
    }

    public static RemoteService getConnectionManagerService(ControlPointUpnpService upnpService) {
        return getService(upnpService, "ConnectionManager");
    }

    public static RemoteService getAudioRenderingControl(
            ControlPointUpnpService upnpService) {
        return getService(upnpService, "RenderingControl");
    }

    public static RemoteService getAVTransportService(ControlPointUpnpService upnpService) {
        return getService(upnpService, "AVTransport");
    }
}
