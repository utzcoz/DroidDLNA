package com.github.cling.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;

public class DevicesActivity extends AppCompatActivity {
    private static final String TAG = "DevicesActivity";
    private AndroidUpnpService upnpService;
    private boolean hasConnected = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            MediaRenderer mediaRenderer = new MediaRenderer(DevicesActivity.this);
            upnpService.getRegistry().addDevice(mediaRenderer.getDevice());
            Log.i(TAG, "Add upnp device " + mediaRenderer.getDevice());

            // Refresh device list
            upnpService.getControlPoint().search();
            hasConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
            hasConnected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.devices);

        getApplicationContext()
                .bindService(
                        new Intent(this, AndroidUpnpServiceImpl.class),
                        serviceConnection,
                        Context.BIND_AUTO_CREATE
                );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().unbindService(serviceConnection);
    }

    public boolean hasConnected() {
        return hasConnected;
    }

    public AndroidUpnpService getUpnpService() {
        return upnpService;
    }
}
