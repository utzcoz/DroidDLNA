/*
 * Copyright (C) 2010 Teleal GmbH, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.dlna.dmp;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.UDN;

/**
 * Wraps a <tt>Device</tt> for display with icon and label. Equality is
 * implemented with UDN comparison.
 */
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
