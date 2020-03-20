package com.github.dlna;

import androidx.lifecycle.Lifecycle;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.UDADeviceType;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.github.dlna.TestHelper.getScenario;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class DevicesActivityTest extends TestBase {
    @Test
    public void testDevicesActivityInitialization() {
        assertEquals(Lifecycle.State.RESUMED, getScenario(devicesActivityRule).getState());
    }

    @Test
    public void testUpnpServiceConnected() {
        DevicesActivity activity = getActivity();
        assertNotNull(activity.getUpnpService());
        assertTrue(activity.hasConnected());
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

    private DevicesActivity getActivity() {
        final DevicesActivity[] activities = new DevicesActivity[1];
        getScenario(devicesActivityRule).onActivity(activity -> activities[0] = activity);
        return activities[0];
    }
}
