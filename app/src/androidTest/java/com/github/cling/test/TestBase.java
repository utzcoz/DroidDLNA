package com.github.cling.test;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.github.cling.test.instrument.ControlPointUpnpService;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.cling.test.instrument.TestHelper.getScenario;

public class TestBase {
    protected ControlPointUpnpService upnpService;
    @Rule
    public ActivityScenarioRule<DevicesActivity> devicesActivityRule =
            new ActivityScenarioRule<>(DevicesActivity.class);

    @Before
    public void setUp() {
        Logger.getLogger("org.fourthline.cling").setLevel(Level.FINEST);
        upnpService = new ControlPointUpnpService();
    }

    @After
    public void tearDown() {
        getScenario(devicesActivityRule).close();
        upnpService.shutdown();
    }
}
