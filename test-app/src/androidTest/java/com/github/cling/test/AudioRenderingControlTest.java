package com.github.cling.test;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.github.cling.test.instrument.TestHelper;
import com.github.cling.test.instrument.UpnpServiceFetcher;
import com.github.cling.test.instrument.renderingcontrol.GetMuteAction;
import com.github.cling.test.instrument.renderingcontrol.GetVolumeAction;
import com.github.cling.test.instrument.renderingcontrol.ListPresetsAction;
import com.github.cling.test.instrument.renderingcontrol.SetMuteAction;
import com.github.cling.test.instrument.renderingcontrol.SetVolumeAction;

import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.model.PresetName;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class AudioRenderingControlTest extends TestBase {
    @After
    public void tearDown() {
        super.tearDown();
        ClingLocalRenderer.getLocalRender().setVolume(0);
    }

    @Test
    public void testGetAudioRenderingControlSucceed() {
        assertNotNull(UpnpServiceFetcher.getAudioRenderingControl(upnpService));
    }

    @Test
    public void testListPresentsSucceed() {
        ListPresetsAction action =
                new ListPresetsAction(
                        UpnpServiceFetcher.getAudioRenderingControl(upnpService),
                        Utils.getDefaultInstanceId()
                );
        TestHelper.executeAction(upnpService, action);
        assertEquals(PresetName.FactoryDefaults.toString(), action.getCurrentPresentNameList());
    }

    @Test
    public void testGetMuteSucceed() {
        GetMuteAction action =
                new GetMuteAction(
                        UpnpServiceFetcher.getAudioRenderingControl(upnpService),
                        Utils.getDefaultInstanceId(),
                        Utils.getChannels()[0].name()
                );
        ClingLocalRenderer.getLocalRender().setVolume(100);
        TestHelper.executeAction(upnpService, action);
        assertFalse(action.getMute());
        ClingLocalRenderer.getLocalRender().setVolume(0);
        TestHelper.executeAction(upnpService, action);
        assertTrue(action.getMute());
    }

    @Test
    public void testSetMuteSucceed() {
        SetMuteAction action =
                new SetMuteAction(
                        UpnpServiceFetcher.getAudioRenderingControl(upnpService),
                        Utils.getDefaultInstanceId(),
                        Utils.getChannels()[0].name(),
                        true
                );
        ClingLocalRenderer.getLocalRender().setVolume(100);
        TestHelper.executeAction(upnpService, action);
        assertEquals(0, ClingLocalRenderer.getLocalRender().getVolume());
        action =
                new SetMuteAction(
                        UpnpServiceFetcher.getAudioRenderingControl(upnpService),
                        Utils.getDefaultInstanceId(),
                        Utils.getChannels()[0].name(),
                        false
                );
        TestHelper.executeAction(upnpService, action);
        assertEquals(100, ClingLocalRenderer.getLocalRender().getVolume());
    }

    @Test
    public void testGetVolumeSucceed() {
        GetVolumeAction action =
                new GetVolumeAction(
                        UpnpServiceFetcher.getAudioRenderingControl(upnpService),
                        Utils.getDefaultInstanceId(),
                        Utils.getChannels()[0].name()
                );
        TestHelper.executeAction(upnpService, action);
        assertEquals(new UnsignedIntegerTwoBytes(0), action.getVolume());
        ClingLocalRenderer.getLocalRender().setVolume(100);
        TestHelper.executeAction(upnpService, action);
        assertEquals(new UnsignedIntegerTwoBytes(100), action.getVolume());
    }

    @Test
    public void testSetVolumeSucceed() {
        SetVolumeAction action =
                new SetVolumeAction(
                        UpnpServiceFetcher.getAudioRenderingControl(upnpService),
                        Utils.getDefaultInstanceId(),
                        Utils.getChannels()[0].name(),
                        new UnsignedIntegerTwoBytes(100)
                );
        TestHelper.executeAction(upnpService, action);
        assertEquals(100, ClingLocalRenderer.getLocalRender().getVolume());
        action =
                new SetVolumeAction(
                        UpnpServiceFetcher.getAudioRenderingControl(upnpService),
                        Utils.getDefaultInstanceId(),
                        Utils.getChannels()[0].name(),
                        new UnsignedIntegerTwoBytes(50)
                );
        TestHelper.executeAction(upnpService, action);
        assertEquals(50, ClingLocalRenderer.getLocalRender().getVolume());
    }
}
