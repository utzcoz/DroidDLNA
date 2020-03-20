package com.github.dlna;

import androidx.lifecycle.Lifecycle;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

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

    private DevicesActivity getActivity() {
        final DevicesActivity[] activities = new DevicesActivity[1];
        getScenario(devicesActivityRule).onActivity(activity -> activities[0] = activity);
        return activities[0];
    }
}
