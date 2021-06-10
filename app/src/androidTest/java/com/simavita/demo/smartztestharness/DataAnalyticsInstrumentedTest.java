package com.simavita.demo.smartztestharness;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.simavita.DataInfo;
import com.simavita.sdk.dataanalytics.DataAnalytics;
import com.simavita.sdk.pod.ble.data.DataAcquisition;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DataAnalyticsInstrumentedTest {
    @Test
    public void processdata() {
        byte[] data = DataAnalytics.hexStringToByteArray("1EFFB0A0F2DE3C14209E2DAAEAE9BD17C098BED8FFDE1DC11E52D4265A09061809536D6172747A5F506F645F46343930434244303035354305FFCDAB0100");
        DataInfo previousDatainfo = DataAnalytics.getDefaultDataInfo();
        DataInfo info = DataAnalytics.smartzsdk.processdata(data,
                previousDatainfo);

    }
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.simavita.demo.smartztestharness", appContext.getPackageName());
    }
    @Test
    public  void test() {
        assertEquals(0, 0);
//        assertEquals(1, 0);
    }
}
