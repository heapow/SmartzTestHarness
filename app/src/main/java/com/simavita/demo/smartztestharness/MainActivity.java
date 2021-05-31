package com.simavita.demo.smartztestharness;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.simavita.DataInfo;
import com.simavita.sdk.dataanalytics.DataAnalytics;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataInfo info = DataAnalytics.smartzsdk.processdata("1EFFB0A0F2DE3C14209E2DAAEAE9BD17C098BED8FFDE1DC11E52D4265A09061809536D6172747A5F506F645F46343930434244303035354305FFCDAB0100".getBytes(),
                new DataInfo(0,0,0,0,0,0,0,
                        0,0,0,0,
                        0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0));

        Log.d("datainfo", info.toString());
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(info.toString());
    }
}