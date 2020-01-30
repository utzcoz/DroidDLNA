package com.github.dlna.dmr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.github.dlna.dmp.GPlayer;
import com.github.dlna.util.Action;

public class RenderPlayerService extends Service {

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        if (intent == null) {
            return result;
        }
        String type = intent.getStringExtra("type");
        if (type == null) {
            return result;
        }
        switch (type) {
            case "audio":
            case "video": {
                Intent playerIntent = new Intent(this, GPlayer.class);
                playerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                playerIntent.putExtra("name", intent.getStringExtra("name"));
                playerIntent.putExtra("playURI", intent.getStringExtra("playURI"));
                startActivity(playerIntent);
                break;
            }
            default: {
                Intent playerIntent = new Intent(Action.DMR);
                playerIntent.putExtra("playpath", intent.getStringExtra("playURI"));
                sendBroadcast(playerIntent);
                break;
            }
        }

        return result;
    }
}
