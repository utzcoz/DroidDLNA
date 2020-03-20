package com.github.dlna;

import androidx.test.platform.app.InstrumentationRegistry;

import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.android.AndroidRouter;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.transport.Router;

import java.util.ArrayList;
import java.util.List;

public class TestUpnpService extends UpnpServiceImpl {
    private TestRegistryListener registryListener = new TestRegistryListener();

    public TestUpnpService() {
        super(new AndroidUpnpServiceConfiguration());
        getRegistry().addListener(registryListener);
    }

    @Override
    protected Router createRouter(ProtocolFactory protocolFactory, Registry registry) {
        return
                new AndroidRouter(
                        getConfiguration(),
                        protocolFactory,
                        InstrumentationRegistry.getInstrumentation().getTargetContext()
                );
    }

    public List<RemoteDevice> getRemoteDevices() {
        return new ArrayList<>(registryListener.remoteDevices);
    }

    public List<LocalDevice> getLocalDevices() {
        return new ArrayList<>(registryListener.localDevices);
    }

    @Override
    public synchronized void shutdown() {
        ((AndroidRouter) getRouter()).unregisterBroadcastReceiver();
        super.shutdown();
    }

    private static final class TestRegistryListener implements RegistryListener {
        private List<RemoteDevice> remoteDevices = new ArrayList<>();
        private List<LocalDevice> localDevices = new ArrayList<>();

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            // Do nothing
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry,
                                                RemoteDevice device,
                                                Exception ex) {
            remoteDevices.remove(device);
        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            remoteDevices.add(device);
        }

        @Override
        public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
            remoteDevices.remove(device);
            remoteDevices.add(device);
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            remoteDevices.remove(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            localDevices.add(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            localDevices.remove(device);
        }

        @Override
        public void beforeShutdown(Registry registry) {
            // Do nothing
        }

        @Override
        public void afterShutdown() {
            // Do nothing
        }
    }
}
