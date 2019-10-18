package com.zxt.dlna.util;

import android.os.Environment;

import com.zxt.dlna.util.DevMountInfo.DevInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DevMountInfo implements IDev {
    /**
     * ***
     */
    public final String HEAD = "dev_mount";

    /**
     * Partition
     */
    private final int NPATH = 2;

    private final int DEV_INTERNAL = 0;
    private final int DEV_EXTERNAL = 1;

    private ArrayList<String> cache = new ArrayList<String>();

    private static DevMountInfo dev;
    private DevInfo info;

    private final File VOLD_FSTAB = new File(Environment.getRootDirectory()
            .getAbsoluteFile()
            + File.separator
            + "etc"
            + File.separator
            + "vold.fstab");

    public static DevMountInfo getInstance() {
        if (null == dev)
            dev = new DevMountInfo();
        return dev;
    }

    private DevInfo getInfo(final int device) {
        // for(String str:cache)
        // System.out.println(str);

        if (null == info)
            info = new DevInfo();

        try {
            initVoldFstabToCache();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (device >= cache.size())
            return null;
        String[] sinfo = cache.get(device).split(" ");

        info.setPath(sinfo[NPATH]);

        return info;
    }

    /**
     * init the words into the cache array
     *
     * @throws IOException
     */
    private void initVoldFstabToCache() throws IOException {
        cache.clear();
        BufferedReader br = new BufferedReader(new FileReader(VOLD_FSTAB));
        String tmp;
        while ((tmp = br.readLine()) != null) {
            // the words startsWith "dev_mount" are the SD info
            if (tmp.startsWith(HEAD)) {
                cache.add(tmp);
            }
        }
        br.close();
        cache.trimToSize();
    }

    public class DevInfo {
        private String path;

        /**
         * SD mount path
         *
         * @return
         */
        public String getPath() {
            return path;
        }

        private void setPath(String path) {
            this.path = path;
        }
    }

    @Override
    public DevInfo getInternalInfo() {
        return getInfo(DEV_INTERNAL);
    }

    @Override
    public DevInfo getExternalInfo() {
        return getInfo(DEV_EXTERNAL);
    }
}

interface IDev {
    DevInfo getInternalInfo();

    DevInfo getExternalInfo();
}
