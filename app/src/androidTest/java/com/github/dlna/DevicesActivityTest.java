package com.github.dlna;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.UDADeviceType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4ClassRunner.class)
public class DevicesActivityTest {
    private static final String TAG = "DevicesActivityTest";
    private TestUpnpService upnpService;
    @Rule
    public ActivityScenarioRule<DevicesActivity> devicesActivityRule =
            new ActivityScenarioRule<>(DevicesActivity.class);

    private static final int MAX_SEARCH_WAIT_SECONDS = 5;

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
        RemoteDevice remoteDevice = searchRemoteDevice();
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
    public void testSendSetURIActionSucceed() {

    }

    private RemoteDevice searchRemoteDevice() {
        for (int i = 0; i < 10; i++) {
            Log.e(TAG, "Start to search upnp device, index " + i);
            upnpService.getControlPoint().search();
            long startTimeMillis = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTimeMillis <= MAX_SEARCH_WAIT_SECONDS * 1000) {
                if (upnpService.getRemoteDevices().size() > 0) {
                    break;
                }
            }
            List<RemoteDevice> remoteDevices = upnpService.getRemoteDevices();
            if (remoteDevices.size() > 0) {
                return remoteDevices.get(0);
            }
        }
        fail("Failed to search remote devices");
        return null;
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
