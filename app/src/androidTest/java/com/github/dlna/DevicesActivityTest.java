package com.github.dlna;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class DevicesActivityTest {
    @Rule
    public ActivityScenarioRule<DevicesActivity> devicesActivityRule =
            new ActivityScenarioRule<>(DevicesActivity.class);

    @Test
    public void testDevicesActivityInitialization() {
        ActivityScenario<DevicesActivity> devicesActivity = devicesActivityRule.getScenario();
        assertEquals(Lifecycle.State.RESUMED, devicesActivity.getState());
    }
}
