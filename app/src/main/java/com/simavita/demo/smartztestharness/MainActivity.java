package com.simavita.demo.smartztestharness;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.simavita.DataInfo;
import com.simavita.sdk.dataanalytics.DataAnalytics;
import com.simavita.sdk.pod.ble.data.DataAcquisition;
import com.simavita.sdk.pod.ble.data.demo.RuntimePermissionChecker;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    com.simavita.sdk.pod.ble.data.demo.RuntimePermissionChecker permissionChecker;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private AnalyticsPanel analyticsPanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(com.simavita.sdk.pod.ble.data.R.layout.sdk_pod_ble_data_activity_test);
        setContentView(R.layout.activity_main);
        analyticsPanel = findViewById(R.id.analyticsPanel);
        permissionChecker = new com.simavita.sdk.pod.ble.data.demo.RuntimePermissionChecker(this, savedInstanceState);


        permissionChecker.registerPermissionRequestCallback(REQUEST_LOCATION_PERMISSION, new RuntimePermissionChecker.PermissionRequestCallback() {
            @Override
            public void onPermissionRequestResult(int requestCode, String[] permissions, String[] denied) {
                if (denied == null) {
                    initDataAcquisition();
                }

            }
        });
        if (permissionChecker.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, "", REQUEST_LOCATION_PERMISSION)) {
            initDataAcquisition();
        }
//        initDataAcquisition();
    }
    public void initDataAcquisition() {
        if (!DataAcquisition.isBleOn()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
            return;
        }
        DataAcquisition.start();
        DataAcquisition.addCallback(new DataAcquisition.Callback(){
            @Override
            public void calculatePPM(DataAcquisition.DataHandler data) {
                Log.d("TestAct PPM", Double.toString(data.GetMinPPM())+ " | " + Double.toString(data.GetMaxPPM()));
            }

            @Override
            public void onLeScan(String data, BluetoothDevice device, int rssi, byte[] scanRecord) {
                Log.d("DataInfo", data.toString());
                DataInfo info = DataAnalytics.smartzsdk.processdata(scanRecord, DataAnalytics.getDefaultDataInfo());
                analyticsPanel.showDataInfo(info);
            }
        });
        Log.d("Tag", "initDataAcquisition");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                DataAcquisition.start();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        DataAcquisition.stop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void podBleDataTest(View view) {
        DataAcquisition.start();
//        startActivity(new Intent(this, PodTestActivity.class));
    }

}
