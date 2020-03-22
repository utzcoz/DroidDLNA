package com.github.cling.test;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.github.cling.test.instrument.ControlPointUpnpService;
import com.github.cling.test.instrument.TestHelper;
import com.github.cling.test.instrument.UpnpServiceFetcher;
import com.github.cling.test.instrument.avtransport.GetDeviceCapabilitiesAction;
import com.github.cling.test.instrument.avtransport.GetMediaInfoAction;
import com.github.cling.test.instrument.avtransport.GetPositionInfoAction;
import com.github.cling.test.instrument.avtransport.GetTransportInfoAction;
import com.github.cling.test.instrument.avtransport.GetTransportSettingsAction;
import com.github.cling.test.instrument.avtransport.PauseAction;
import com.github.cling.test.instrument.avtransport.PlayAction;
import com.github.cling.test.instrument.avtransport.SeekAction;
import com.github.cling.test.instrument.avtransport.SetAVTransportURIAction;
import com.github.cling.test.instrument.avtransport.SetNextAVTransportURIAction;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.RecordQualityMode;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportSettings;
import org.fourthline.cling.support.model.TransportState;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4ClassRunner.class)
public class AVTransportServiceTest extends TestBase {

    public static final String URI = "some-uri";
    public static final String URI_META_DATA = "some-meta-data";

    @After
    public void tearDown() {
        super.tearDown();
        ClingLocalRenderer.getLocalRender().seek(null, null);
    }

    @Test
    public void testGetAVTransportServiceSucceed() {
        assertNotNull(UpnpServiceFetcher.getAVTransportService(upnpService));
    }

    @Test
    public void testSetAVTransportURISucceed() {
        setAVTransportURI(URI, URI_META_DATA);
    }

    @Test
    public void testSetNextAVTransportURISucceed() {
        String uri = "next-some-uri";
        String uriMetaData = "next-some-meta-data";
        SetNextAVTransportURIAction action =
                new SetNextAVTransportURIAction(
                        UpnpServiceFetcher.getAVTransportService(upnpService),
                        Utils.getDefaultInstanceId(),
                        uri,
                        uriMetaData
                );
        TestHelper.executeAction(upnpService, action);
        assertEquals(uri, ClingLocalRenderer.getLocalRender().getNextPlayURI());
        assertEquals(uriMetaData, ClingLocalRenderer.getLocalRender().getNextURIMetaData());
    }

    @Test
    public void testGetMediaInfoSucceed() {
        setAVTransportURI(URI, URI_META_DATA);
        int durationSeconds = 10000;
        ClingLocalRenderer.getControlPoint().durationChanged(durationSeconds);
        GetMediaInfoAction action =
                new GetMediaInfoAction(
                        UpnpServiceFetcher.getAVTransportService(upnpService),
                        Utils.getDefaultInstanceId()
                );
        TestHelper.executeAction(upnpService, action);
        MediaInfo mediaInfo = action.getMediaInfo();
        assertNotNull(mediaInfo);
        assertNull(mediaInfo.getCurrentURI());
        assertNull(mediaInfo.getCurrentURIMetaData());
        assertEquals(Utils.getDefaultNumberOfTracks(), mediaInfo.getNumberOfTracks());
        assertEquals(
                ModelUtil.toTimeString(durationSeconds / 1000),
                mediaInfo.getMediaDuration()
        );
        assertEquals(StorageMedium.NETWORK, mediaInfo.getPlayMedium());
    }

    @Test
    public void testGetTransportInfoSucceed() {
        checkTransportState(upnpService, TransportState.NO_MEDIA_PRESENT);
        setAVTransportURI(URI, URI_META_DATA);
        checkTransportState(upnpService, TransportState.STOPPED);
        ClingLocalRenderer.getControlPoint().start();
        checkTransportState(upnpService, TransportState.PLAYING);
        ClingLocalRenderer.getControlPoint().pause();
        checkTransportState(upnpService, TransportState.PAUSED_PLAYBACK);
        ClingLocalRenderer.getControlPoint().endOfMedia();
        checkTransportState(upnpService, TransportState.NO_MEDIA_PRESENT);
    }

    @Test
    public void testGetPositionInfoSucceed() {
        setAVTransportURI(URI, URI_META_DATA);
        int position = 10000;
        ClingLocalRenderer.getControlPoint().positionChanged(position);
        GetPositionInfoAction action =
                new GetPositionInfoAction(
                        UpnpServiceFetcher.getAVTransportService(upnpService),
                        Utils.getDefaultInstanceId()
                );
        TestHelper.executeAction(upnpService, action);
        PositionInfo positionInfo = action.getPositionInfo();
        assertEquals(ModelUtil.toTimeString(position / 1000), positionInfo.getRelTime());
        assertEquals(positionInfo.getRelTime(), positionInfo.getAbsTime());
    }

    @Test
    public void testGetDeviceCapabilitiesSucceed() {
        GetDeviceCapabilitiesAction action =
                new GetDeviceCapabilitiesAction(
                        UpnpServiceFetcher.getAVTransportService(upnpService),
                        Utils.getDefaultInstanceId()
                );
        TestHelper.executeAction(upnpService, action);
        DeviceCapabilities capabilities = action.getDeviceCapabilities();
        StorageMedium[] playMedia = capabilities.getPlayMedia();
        assertNotNull(playMedia);
        assertEquals(1, playMedia.length);
        assertEquals(StorageMedium.NETWORK, playMedia[0]);
    }

    @Test
    public void testGetTransportSettingsSucceed() {
        GetTransportSettingsAction action =
                new GetTransportSettingsAction(
                        UpnpServiceFetcher.getAVTransportService(upnpService),
                        Utils.getDefaultInstanceId()
                );
        TestHelper.executeAction(upnpService, action);
        TransportSettings settings = action.getTransportSettings();
        assertEquals(PlayMode.NORMAL, settings.getPlayMode());
        assertEquals(RecordQualityMode.NOT_IMPLEMENTED, settings.getRecQualityMode());
    }

    @Test
    public void testPlayActionSucceed() {
        setAVTransportURI(URI, URI_META_DATA);
        PlayAction action = new PlayAction(
                UpnpServiceFetcher.getAVTransportService(upnpService),
                Utils.getDefaultInstanceId()
        );
        TestHelper.executeAction(upnpService, action);
        checkTransportState(upnpService, TransportState.PLAYING);
    }

    @Test
    public void testPauseActionSucceed() {
        testPlayActionSucceed();
        PauseAction action = new PauseAction(
                UpnpServiceFetcher.getAVTransportService(upnpService),
                Utils.getDefaultInstanceId()
        );
        TestHelper.executeAction(upnpService, action);
        checkTransportState(upnpService, TransportState.PAUSED_PLAYBACK);
    }

    @Test
    public void testSeekActionSucceed() {
        testPlayActionSucceed();
        String unit = "s";
        String target = "100";
        SeekAction action = new SeekAction(
                UpnpServiceFetcher.getAVTransportService(upnpService),
                Utils.getDefaultInstanceId(),
                unit,
                target
        );
        TestHelper.executeAction(upnpService, action);
        assertEquals(unit, ClingLocalRenderer.getLocalRender().getSeekUnit());
        assertEquals(target, ClingLocalRenderer.getLocalRender().getSeekTarget());
    }

    private void checkTransportState(ControlPointUpnpService upnpService,
                                     TransportState state) {
        GetTransportInfoAction action =
                new GetTransportInfoAction(
                        UpnpServiceFetcher.getAVTransportService(upnpService),
                        Utils.getDefaultInstanceId()
                );
        TestHelper.executeAction(upnpService, action);
        TransportInfo transportInfo = action.getTransportInfo();
        assertNotNull(transportInfo);
        assertEquals(state, transportInfo.getCurrentTransportState());
    }

    private void setAVTransportURI(String uri, String uriMetaData) {
        SetAVTransportURIAction action =
                new SetAVTransportURIAction(
                        UpnpServiceFetcher.getAVTransportService(upnpService),
                        Utils.getDefaultInstanceId(),
                        uri,
                        uriMetaData
                );
        TestHelper.executeAction(upnpService, action);
        assertEquals(uri, ClingLocalRenderer.getLocalRender().getPlayURI());
        assertEquals(uriMetaData, ClingLocalRenderer.getLocalRender().getURIMetaData());
    }
}
