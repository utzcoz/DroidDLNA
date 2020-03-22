package com.github.cling.test;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.github.cling.test.instrument.ControlPointUpnpService;
import com.github.cling.test.instrument.connectionmanager.GetProtocolInfoAction;
import com.github.cling.test.instrument.TestHelper;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ConnectionManagerServiceTest extends TestBase {
    @Test
    public void testGetConnectivityManagerSinkProtocolInfoSucceed() {
        GetProtocolInfoAction action = executeGetProtocolInfoAction(upnpService);
        String expectedSinkProtocolInfoList =
                Utils.generateSinkProtocolInfoList()
                        .stream()
                        .map(ProtocolInfo::toString)
                        .collect(Collectors.joining(","));
        assertEquals(expectedSinkProtocolInfoList, action.getSinkProtocolInfo());
    }

    @Test
    public void testGetConnectivityManagerSourceProtocolInfoSucceed() {
        GetProtocolInfoAction action = executeGetProtocolInfoAction(upnpService);
        assertNull(action.getSourceProtocolInfo());
    }

    private GetProtocolInfoAction executeGetProtocolInfoAction(ControlPointUpnpService upnpService) {
        RemoteDevice remoteDevice = TestHelper.searchRemoteDevice(upnpService);
        assertNotNull(remoteDevice);
        ServiceId serviceId = new UDAServiceId("ConnectionManager");
        RemoteService connectionManagerService = remoteDevice.findService(serviceId);
        assertNotNull(connectionManagerService);
        GetProtocolInfoAction action = new GetProtocolInfoAction(connectionManagerService);
        int[] result = new int[1];
        upnpService.getControlPoint().execute(new ActionCallback(action) {
            @Override
            public void success(ActionInvocation invocation) {
                result[0] = 1;
            }

            @Override
            public void failure(ActionInvocation invocation,
                                UpnpResponse operation,
                                String defaultMsg) {
                result[0] = -1;
            }
        });
        TestHelper.waitState(() -> result[0] != 0, TestHelper.MAX_WAIT_MILLIS);
        assertEquals(1, result[0]);
        return action;
    }
}
