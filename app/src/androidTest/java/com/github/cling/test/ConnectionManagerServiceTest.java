package com.github.cling.test;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.github.cling.test.instrument.ControlPointUpnpService;
import com.github.cling.test.instrument.TestHelper;
import com.github.cling.test.instrument.connectionmanager.GetCurrentConnectionIDsAction;
import com.github.cling.test.instrument.connectionmanager.GetCurrentConnectionInfoAction;
import com.github.cling.test.instrument.connectionmanager.GetProtocolInfoAction;

import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.support.model.ConnectionInfo;
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

    @Test
    public void testGetCurrentConnectionIdsSucceed() {
        GetCurrentConnectionIDsAction action = executeGetCurrentConnectionIDsAction(upnpService);
        assertEquals("0", action.getCurrentConnectionIDs());
    }

    @Test
    public void testGetRcsIDSucceed() {
        GetCurrentConnectionInfoAction action = executeGetCurrentConnectionInfoAction(upnpService);
        assertEquals(0, action.getRcsID());
    }

    @Test
    public void testGetAVTransportIDSucceed() {
        GetCurrentConnectionInfoAction action = executeGetCurrentConnectionInfoAction(upnpService);
        assertEquals(0, action.getAVTransportID());
    }

    @Test
    public void testGetPeerConnectionManagerSucceed() {
        GetCurrentConnectionInfoAction action = executeGetCurrentConnectionInfoAction(upnpService);
    }

    @Test
    public void testGetPeerConnectionIDSucceed() {
        GetCurrentConnectionInfoAction action = executeGetCurrentConnectionInfoAction(upnpService);
        assertEquals(-1, action.getPeerConnectionID());
    }

    @Test
    public void testGetDirectionSucceed() {
        GetCurrentConnectionInfoAction action = executeGetCurrentConnectionInfoAction(upnpService);
        assertEquals(ConnectionInfo.Direction.Input, action.getDirection());
    }

    @Test
    public void testGetStatusSucceed() {
        GetCurrentConnectionInfoAction action = executeGetCurrentConnectionInfoAction(upnpService);
        assertEquals(ConnectionInfo.Status.Unknown, action.getStatus());
    }

    private GetCurrentConnectionInfoAction executeGetCurrentConnectionInfoAction(
            ControlPointUpnpService upnpService) {
        GetCurrentConnectionInfoAction action =
                new GetCurrentConnectionInfoAction(getConnectionManagerService(), 0);
        TestHelper.executeAction(upnpService, action);
        return action;
    }

    private GetProtocolInfoAction executeGetProtocolInfoAction(
            ControlPointUpnpService upnpService) {
        GetProtocolInfoAction action = new GetProtocolInfoAction(getConnectionManagerService());
        TestHelper.executeAction(upnpService, action);
        return action;
    }

    private GetCurrentConnectionIDsAction executeGetCurrentConnectionIDsAction(
            ControlPointUpnpService upnpService) {
        GetCurrentConnectionIDsAction action =
                new GetCurrentConnectionIDsAction(getConnectionManagerService());
        TestHelper.executeAction(upnpService, action);
        return action;
    }

    private RemoteService getConnectionManagerService() {
        RemoteDevice remoteDevice = TestHelper.searchRemoteDevice(upnpService);
        assertNotNull(remoteDevice);
        ServiceId serviceId = new UDAServiceId("ConnectionManager");
        RemoteService connectionManagerService = remoteDevice.findService(serviceId);
        assertNotNull(connectionManagerService);
        return connectionManagerService;
    }
}
