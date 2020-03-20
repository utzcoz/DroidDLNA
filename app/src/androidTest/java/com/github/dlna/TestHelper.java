package com.github.dlna;

import android.util.Log;

import org.fourthline.cling.model.meta.RemoteDevice;

import java.util.List;

import static org.junit.Assert.fail;

public class TestHelper {
    public static final int MAX_SEARCH_WAIT_SECONDS = 5;

    private static final String TAG = "TesetHelper";

    static RemoteDevice searchRemoteDevice(TestUpnpService upnpService) {
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
}
