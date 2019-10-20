/*
 * Copyright (C) 2014 zxt
 * RenderService.java
 * Description:
 * Author: zxt
 * Date:  2014-1-23 上午10:30:58
 */

package com.github.dlna.dmr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RenderService extends Service {

    private boolean isopen = false;

    public void closeMediaRenderer() {
    }

    @Override
    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isopen = false;
        closeMediaRenderer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.isopen) {
            this.isopen = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
