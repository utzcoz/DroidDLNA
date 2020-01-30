package com.github.dlna.dmr;

import android.content.Context;
import android.util.Log;

import com.github.dlna.Settings;
import com.github.dlna.util.UpnpUtil;
import com.github.dlna.util.Utils;

import org.fourthline.cling.binding.LocalServiceBinder;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DLNACaps;
import org.fourthline.cling.model.types.DLNADoc;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;

import java.io.IOException;
import java.util.Map;

public class ZxtMediaRenderer {

    private static final long LAST_CHANGE_FIRING_INTERVAL_MILLISECONDS = 500;

    private static final String TAG = "GstMediaRenderer";

    // These are shared between all "logical" player instances of a single service
    private final LastChange avTransportLastChange =
            new LastChange(new AVTransportLastChangeParser());
    private final LastChange renderingControlLastChange =
            new LastChange(new RenderingControlLastChangeParser());

    private final Map<UnsignedIntegerFourBytes, ZxtMediaPlayer> mediaPlayers;

    private final LastChangeAwareServiceManager<AVTransportService> avTransport;
    private final LastChangeAwareServiceManager<AudioRenderingControl> renderingControl;

    private final LocalDevice device;

    private Context mContext;

    public ZxtMediaRenderer(int numberOfPlayers, Context context) {
        mContext = context;

        // This is the backend which manages the actual player instances
        mediaPlayers = new ZxtMediaPlayers(
                numberOfPlayers,
                context,
                avTransportLastChange,
                renderingControlLastChange
        ) {
            // These overrides connect the player instances to the output/display
            @Override
            protected void onPlay(ZxtMediaPlayer player) {
            }

            @Override
            protected void onStop(ZxtMediaPlayer player) {
            }
        };

        // The connection manager doesn't have to do much, HTTP is stateless
        LocalServiceBinder binder = new AnnotationLocalServiceBinder();
        LocalService connectionManagerService = binder.read(ZxtConnectionManagerService.class);
        ServiceManager<ZxtConnectionManagerService> connectionManager =
                new DefaultServiceManager(connectionManagerService) {
                    @Override
                    protected Object createServiceInstance() throws Exception {
                        return new ZxtConnectionManagerService();
                    }
                };
        connectionManagerService.setManager(connectionManager);

        // The AVTransport just passes the calls on to the backend players
        LocalService<AVTransportService> avTransportService = binder.read(AVTransportService.class);
        avTransport =
                new LastChangeAwareServiceManager<AVTransportService>(
                        avTransportService,
                        new AVTransportLastChangeParser()
                ) {
                    @Override
                    protected AVTransportService createServiceInstance() {
                        return new AVTransportService(avTransportLastChange, mediaPlayers);
                    }
                };
        avTransportService.setManager(avTransport);

        // The Rendering Control just passes the calls on to the backend players
        LocalService<AudioRenderingControl> renderingControlService =
                binder.read(AudioRenderingControl.class);
        renderingControl =
                new LastChangeAwareServiceManager<AudioRenderingControl>(
                        renderingControlService,
                        new RenderingControlLastChangeParser()
                ) {
                    @Override
                    protected AudioRenderingControl createServiceInstance() {
                        return new AudioRenderingControl(renderingControlLastChange, mediaPlayers);
                    }
                };
        renderingControlService.setManager(renderingControl);

        try {
            UDN udn = UpnpUtil.uniqueSystemIdentifier("msidmr");

            device = new LocalDevice(
                    new DeviceIdentity(udn),
                    new UDADeviceType("MediaRenderer", 1),
                    new DeviceDetails(
                            Settings.getRenderName() + " (" + android.os.Build.MODEL + ")",
                            new ManufacturerDetails(Utils.MANUFACTURER),
                            new ModelDetails(
                                    Utils.DMR_NAME,
                                    Utils.DMR_DESC,
                                    "1",
                                    Utils.DMR_MODEL_URL
                            ),
                            new DLNADoc[]{
                                    new DLNADoc("DMR", DLNADoc.Version.V1_5)
                            },
                            new DLNACaps(new String[]{"av-upload", "image-upload", "audio-upload"})
                    ),
                    new Icon[]{createDefaultDeviceIcon()},
                    new LocalService[]{
                            avTransportService, renderingControlService, connectionManagerService
                    }
            );
            Log.i(TAG, "getType: " + device.getType().toString());
        } catch (ValidationException ex) {
            throw new RuntimeException(ex);
        }

        runLastChangePushThread();
    }

    // The backend player instances will fill the LastChange whenever something happens with
    // whatever event messages are appropriate. This loop will periodically flush these changes
    // to subscribers of the LastChange state variable of each service.
    private void runLastChangePushThread() {
        // TODO: We should only run this if we actually have event subscribers
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        // These operations will NOT block and wait for network responses
                        avTransport.fireLastChange();
                        renderingControl.fireLastChange();
                        Thread.sleep(LAST_CHANGE_FIRING_INTERVAL_MILLISECONDS);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "runLastChangePushThread ex", ex);
                }
            }
        }.start();
    }

    public LocalDevice getDevice() {
        return device;
    }
    
    private Icon createDefaultDeviceIcon() {
        try {
            return new Icon(
                    "image/png",
                    48,
                    48,
                    32,
                    "msi.png",
                    mContext.getResources().getAssets().open("ic_launcher.png")
            );
        } catch (IOException e) {
            Log.w(TAG, "createDefaultDeviceIcon IOException");
            return null;
        }
    }

}
