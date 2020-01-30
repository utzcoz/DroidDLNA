package com.github.dlna;

import android.app.Application;
import android.content.Context;

import org.fourthline.cling.android.AndroidUpnpService;

import java.util.UUID;

public class BaseApplication extends Application {
    private static final String SP_NAME_UUID = "sp_name_uuid";
    private static final String SP_KEY_UUID = "uuid";

    public static AndroidUpnpService upnpService;

    @Override
    public void onCreate() {
        super.onCreate();
        String uuid =
                getApplicationContext()
                        .getSharedPreferences(SP_NAME_UUID, Context.MODE_PRIVATE)
                        .getString(SP_KEY_UUID, UUID.randomUUID().toString());
        Settings.setUUID(uuid);
        getApplicationContext()
                .getSharedPreferences(SP_NAME_UUID, Context.MODE_PRIVATE)
                .edit()
                .putString(SP_KEY_UUID, uuid).apply();
    }
}
