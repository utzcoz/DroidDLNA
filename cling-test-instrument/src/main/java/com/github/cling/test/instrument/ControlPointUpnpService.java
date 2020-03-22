package com.github.cling.test.instrument;

import android.util.Log;

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
import org.fourthline.cling.transport.impl.AsyncServletStreamServerConfigurationImpl;
import org.fourthline.cling.transport.impl.AsyncServletStreamServerImpl;
import org.fourthline.cling.transport.impl.jetty.JettyServletContainer;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;
import org.fourthline.cling.transport.spi.StreamServer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ControlPointUpnpService extends UpnpServiceImpl {
    private TestRegistryListener registryListener = new TestRegistryListener();

    public ControlPointUpnpService() {
        super(new TestUpnpServiceConfiguration());
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

    private static final class TestUpnpServiceConfiguration
            extends AndroidUpnpServiceConfiguration {
        private static final String TAG = "UpnpServiceConfig";

        @Override
        public StreamServer createStreamServer(NetworkAddressFactory networkAddressFactory) {
            JettyServletContainer container = null;
            try {
                Constructor<?> constructor = JettyServletContainer.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                container = (JettyServletContainer) constructor.newInstance();
                Log.i(TAG, "Jetty container instance " + container);
            } catch (NoSuchMethodException
                    | IllegalAccessException
                    | InstantiationException
                    | InvocationTargetException e) {
                Log.e(TAG, "Failed to initialize jetty container", e);
            }
            return new AsyncServletStreamServerImpl(
                    new AsyncServletStreamServerConfigurationImpl(
                            container == null ? JettyServletContainer.INSTANCE : container,
                            networkAddressFactory.getStreamListenPort()
                    )
            );
        }
    }

    private static final class TestRegistryListener implements RegistryListener {
        private List<RemoteDevice> remoteDevices = new ArrayList<>();
        private List<LocalDevice> localDevices = new ArrayList<>();
        private boolean hasShutdown = false;

        public boolean hasShutdown() {
            return hasShutdown;
        }

        @Override
        public synchronized void remoteDeviceDiscoveryStarted(Registry registry,
                                                              RemoteDevice device) {
            // Do nothing
        }

        @Override
        public synchronized void remoteDeviceDiscoveryFailed(Registry registry,
                                                             RemoteDevice device,
                                                             Exception ex) {
            remoteDevices.remove(device);
        }

        @Override
        public synchronized void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            remoteDevices.add(device);
        }

        @Override
        public synchronized void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
            remoteDevices.remove(device);
            remoteDevices.add(device);
        }

        @Override
        public synchronized void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            remoteDevices.remove(device);
        }

        @Override
        public synchronized void localDeviceAdded(Registry registry, LocalDevice device) {
            localDevices.add(device);
        }

        @Override
        public synchronized void localDeviceRemoved(Registry registry, LocalDevice device) {
            localDevices.remove(device);
        }

        @Override
        public synchronized void beforeShutdown(Registry registry) {
            // Do nothing
        }

        @Override
        public synchronized void afterShutdown() {
            hasShutdown = true;
        }
    }
}
