package com.github.dlna;

import android.app.Activity;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.fourthline.cling.model.meta.RemoteDevice;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.Assert.fail;

public class TestHelper {
    public static final int MAX_WAIT_MILLIS = 5000;

    private static final String TAG = "TesetHelper";

    public static RemoteDevice searchRemoteDevice(ControlPointUpnpService upnpService) {
        for (int i = 0; i < 10; i++) {
            Log.e(TAG, "Start to search upnp device, index " + i);
            upnpService.getControlPoint().search();
            waitState(
                    () -> upnpService.getRemoteDevices().size() > 0,
                    MAX_WAIT_MILLIS
            );
            List<RemoteDevice> remoteDevices = upnpService.getRemoteDevices();
            if (remoteDevices.size() > 0) {
                return remoteDevices.get(0);
            }
        }
        fail("Failed to search remote devices");
        return null;
    }

    public static void waitState(Supplier<Boolean> checkMethod, long maxWaitTimeMillis) {
        long startTimeMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTimeMillis <= maxWaitTimeMillis) {
            if (checkMethod.get()) {
                return;
            }
        }
    }

    public static <T extends Activity> ActivityScenario<T> getScenario(
            ActivityScenarioRule<T> activityRule) {
        return activityRule.getScenario();
    }
}
