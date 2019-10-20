package com.github.dlna.application;

import android.app.Application;
import android.content.Context;

import com.github.dlna.Settings;
import com.github.dlna.dmp.DeviceItem;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.fourthline.cling.android.AndroidUpnpService;

import java.util.UUID;

public class BaseApplication extends Application {
    private static final String SP_NAME_UUID = "sp_name_uuid";
    private static final String SP_KEY_UUID = "uuid";

    public static DeviceItem deviceItem;

    public static DeviceItem dmrDeviceItem;

    public static boolean isLocalDmr = true;

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

        initImageLoader(getApplicationContext());
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them, 
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .enableLogging() // Not necessary in common
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
