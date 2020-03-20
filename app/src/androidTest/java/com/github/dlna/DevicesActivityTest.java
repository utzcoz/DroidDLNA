package com.github.dlna;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class DevicesActivityTest {
    private static final String TAG = "DevicesActivityTest";
    private TestUpnpService upnpService;
    @Rule
    public ActivityScenarioRule<DevicesActivity> devicesActivityRule =
            new ActivityScenarioRule<>(DevicesActivity.class);

    @Before
    public void setUp() {
        Logger.getLogger("org.fourthline.cling").setLevel(Level.FINEST);
        upnpService = new TestUpnpService();
    }

    @After
    public void tearDown() {
        getScenario().close();
        upnpService.shutdown();
    }

    @Test
    public void testDevicesActivityInitialization() {
        assertEquals(Lifecycle.State.RESUMED, getScenario().getState());
    }

    @Test
    public void testUpnpServiceConnected() {
        DevicesActivity activity = getActivity();
        assertNotNull(activity.getUpnpService());
        assertTrue(activity.hasConnected());
    }

    @Test
    public void testInitTestUpnpServiceSucceed() {
        assertNotNull(upnpService);
    }

    @Test
    public void testTestUpnpServiceSearchDeviceSucceed() {
        RemoteDevice remoteDevice = TestHelper.searchRemoteDevice(upnpService);
        assertNotNull(remoteDevice);
        assertEquals(Utils.uniqueSystemIdentifier(), remoteDevice.getIdentity().getUdn());
        assertEquals(UDADeviceType.DEFAULT_NAMESPACE, remoteDevice.getType().getNamespace());
        assertEquals("MediaRenderer", remoteDevice.getType().getType());
        assertEquals(1, remoteDevice.getType().getVersion());
        DeviceDetails deviceDetails = remoteDevice.getDetails();
        assertEquals(
                Utils.getRenderName() + " (" + android.os.Build.MODEL + ")",
                deviceDetails.getFriendlyName()
        );
        assertEquals(Utils.MANUFACTURER, deviceDetails.getManufacturerDetails().getManufacturer());
        ModelDetails modelDetails = deviceDetails.getModelDetails();
        assertEquals(Utils.DMR_NAME, modelDetails.getModelName());
        assertEquals(Utils.DMR_DESC, modelDetails.getModelDescription());
        assertEquals("1", modelDetails.getModelNumber());
        assertEquals(Utils.DMR_MODEL_URL, modelDetails.getModelURI().toString());
        DLNADoc doc = deviceDetails.getDlnaDocs()[0];
        assertEquals("DMR", doc.getDevClass());
        assertEquals(DLNADoc.Version.V1_5.toString(), doc.getVersion());
    }

    @Test
    public void testGetConnectivityManagerSinkProtocolInfoSucceed() {
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
        String sinkProtocolInfo = action.getSinkProtocolInfo();
        assertNotNull(sinkProtocolInfo);
        String expectedSinkProtocolInfoList =
                Utils.generateSinkProtocolInfoList()
                        .stream()
                        .map(ProtocolInfo::toString)
                        .collect(Collectors.joining(","));
        assertEquals(expectedSinkProtocolInfoList, sinkProtocolInfo);
    }

    private ActivityScenario<DevicesActivity> getScenario() {
        return devicesActivityRule.getScenario();
    }

    private DevicesActivity getActivity() {
        final DevicesActivity[] activities = new DevicesActivity[1];
        getScenario().onActivity(activity -> activities[0] = activity);
        return activities[0];
    }
}
