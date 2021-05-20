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
        DataInfo info = DataAnalytics.smartzsdk.processdata(new byte[]{0x01, 0x02, 0x03, 0x04});
        Log.d("datainfo", info.toString());
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(info.toString());
    }
}