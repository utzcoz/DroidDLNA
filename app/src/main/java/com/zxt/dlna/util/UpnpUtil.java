
package com.zxt.dlna.util;

import android.util.Log;

import com.zxt.dlna.application.BaseApplication;

import org.fourthline.cling.model.types.UDN;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

public class UpnpUtil {

    private static final String TAG = "UpnpUtil";

    public static UDN uniqueSystemIdentifier(String salt) {
        StringBuilder systemSalt = new StringBuilder();
        Log.d(TAG, "host:" + BaseApplication.getHostName() + " ip:" + BaseApplication.getHostAddress());
        if (null != BaseApplication.getHostName()
                && null != BaseApplication.getHostAddress()) {
            systemSalt.append(BaseApplication.getHostName()).append(
                    BaseApplication.getHostAddress());
        }
        systemSalt.append(android.os.Build.MODEL);
        systemSalt.append(android.os.Build.MANUFACTURER);

        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(systemSalt.toString().getBytes());
            return new UDN(new UUID(new BigInteger(-1, hash).longValue(), salt.hashCode()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
