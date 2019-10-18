
package com.zxt.dlna.util;

import android.os.Build;
import android.util.Log;

import com.zxt.dlna.Settings;

import org.fourthline.cling.model.types.UDN;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.UUID;

public class UpnpUtil {

    private static final String TAG = "UpnpUtil";

    public static UDN uniqueSystemIdentifier(String salt) {
        StringBuilder systemSalt = new StringBuilder();
        systemSalt.append(Settings.getUUID());
        systemSalt.append(Build.MODEL);
        systemSalt.append(Build.MANUFACTURER);
        Log.i(TAG, "uniqueSystemIdentifier " + systemSalt.toString());

        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(systemSalt.toString().getBytes());
            return new UDN(new UUID(new BigInteger(-1, hash).longValue(), salt.hashCode()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
