package com.github.cling.test.instrument;

import android.app.Activity;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestHelper {
    public static final int MAX_WAIT_MILLIS = 5000;

    private static final String TAG = "TestHelper";

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

    public static void waitState(Checker checker, long maxWaitTimeMillis) {
        long startTimeMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTimeMillis <= maxWaitTimeMillis) {
            if (checker.check()) {
                return;
            }
        }
    }

    public static <T extends Activity> ActivityScenario<T> getScenario(
            ActivityScenarioRule<T> activityRule) {
        return activityRule.getScenario();
    }

    public static <T extends ActionInvocation<RemoteService>> void executeAction(
            UpnpService upnpService, T action) {
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
                Log.e(
                        TAG,
                        "Failed to execute " + action
                                + ", " + defaultMsg
                                + ", operation " + operation
                );
            }
        });
        waitState(() -> result[0] != 0, MAX_WAIT_MILLIS);
        assertEquals(1, result[0]);
    }

    @FunctionalInterface
    private interface Checker {
        boolean check();
    }
}
