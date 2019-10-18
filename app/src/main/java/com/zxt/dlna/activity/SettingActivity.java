package com.zxt.dlna.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;

import com.zxt.dlna.R;
import com.zxt.dlna.util.PreferenceHead;

public class SettingActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        addTitleBar();
    }

    public static boolean getRenderOn(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getBoolean("dmr_status", true);
    }

    public static String getRenderName(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getString("player_name",
                context.getString(R.string.player_name_local));
    }

    public static boolean getDmsOn(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getBoolean("dms_status", true);
    }

    public static String getDeviceName(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getString("dms_name",
                context.getString(R.string.device_local));
    }

    public static int getSlideTime(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return Integer.valueOf(prefs.getString("image_slide_time", "5"));
    }

    private void addTitleBar() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        PreferenceHead ph = new PreferenceHead(this);
        ph.setOrder(0);
        preferenceScreen.addPreference(ph);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
