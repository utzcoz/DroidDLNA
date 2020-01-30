package com.github.dlna.dmp;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.UDN;

public class DeviceItem {

    private UDN udn;

    private Device device;

    public DeviceItem(Device device) {
        this.udn = device.getIdentity().getUdn();
        this.device = device;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DeviceItem that = (DeviceItem) o;

        return udn.equals(that.udn);
    }

    @Override
    public int hashCode() {
        return udn.hashCode();
    }

    @Override
    public String toString() {
        String display;

        if (device.getDetails().getFriendlyName() != null)
            display = device.getDetails().getFriendlyName();
        else
            display = device.getDisplayString();

        // Display a little star while the device is being loaded (see
        // performance optimization earlier)
        return device.isFullyHydrated() ? display : display + " *";
    }
}
