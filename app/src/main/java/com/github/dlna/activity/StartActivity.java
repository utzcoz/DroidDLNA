
package com.github.dlna.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dlna.R;
import com.github.dlna.Settings;

import java.util.UUID;

public class StartActivity extends AppCompatActivity {
    private static final String SP_NAME_UUID = "sp_name_uuid";
    private static final String SP_KEY_UUID = "uuid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_lay);
        updateUUID();
        jumpToMain();
    }

    private void updateUUID() {
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

    private void jumpToMain() {
        Intent intent = new Intent(StartActivity.this, DevicesActivity.class);
        startActivity(intent);
        this.finish();
    }
}
