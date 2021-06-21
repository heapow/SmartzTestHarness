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
import com.example.simavita.PreviousDataInfo;
import com.example.simavita.VolumeInfo;
import com.example.simavita.VolumeStatusInfo;
import com.simavita.sdk.dataanalytics.DataAnalytics;
import com.simavita.sdk.guprofile.UserApi;
import com.simavita.sdk.guprofile.bean.Group;
import com.simavita.sdk.guprofile.bean.Patient;
import com.simavita.sdk.pod.ble.data.DataAcquisition;
import com.simavita.sdk.pod.ble.data.demo.RuntimePermissionChecker;
import com.simiavita.sdk.mpcatalog.ConfigurationApi;
import com.simiavita.sdk.mpcatalog.bean.Configuration;
import com.simiavita.sdk.mpcatalog.bean.Manufacturer;
import com.simiavita.sdk.mpcatalog.bean.Product;
import com.simiavita.sdk.mpcatalog.bean.Threshold;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    com.simavita.sdk.pod.ble.data.demo.RuntimePermissionChecker permissionChecker;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private AnalyticsPanel analyticsPanel;
//    private final String TEST_POD = "Smartz_Pod_F490CBD0056C";
    private final String TEST_POD = "Smartz_Pod_F490CBD000A5";
    private  DataInfo previousInfo = DataAnalytics.getDefaultDataInfo();
    private VolumeStatusInfo previousVolumeStatusInfo = null;
    private List<Threshold> thresholds = null;
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
        prepareThreshold();
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
                double ppm = data.GetPPM(TEST_POD);
//                double ppm2 = data.GetPPM("44:44:44:44");
                double minPPM = data.GetMinPPM();
                double maxPPM = data.GetMaxPPM();
                analyticsPanel.showPPM(ppm, minPPM, maxPPM);
            }

            @Override
            public void onLeScan(String data, BluetoothDevice device, int rssi, byte[] scanRecord) {
                Log.d("BLE Device", device.getName());
                if (!TEST_POD.equals(device.getName())) return;
                DataInfo info = DataAnalytics.smartzsdk.processdata(scanRecord, previousInfo);

                analyticsPanel.showDataInfo(info);
                try{
                    algorithm(previousInfo, info);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                if (info != null) previousInfo = info;
            }
        });
        Log.d("Tag", "initDataAcquisition");
    }

    private void algorithm(DataInfo previous, DataInfo current) {
        if (current == null || thresholds == null) return;
        List<Threshold> locThresholds = new ArrayList<>();
        for(Threshold thre : thresholds) {
            if(thre.getFwVersion().equals(current.getFirmwareVersion())
                    && thre.getHwVersion().equals(current.getHardwareVersion())) {
                locThresholds.add(thre);
            } else {
                locThresholds.add(thre);
            }
        }

        // Step 6: Filter out the threshold for a position that the pod is
        // in. (Position can be obtained from the DataInfo)
        Threshold threshold = null;
        for(Threshold thre : locThresholds) {
            if (current.getPosition() == thre.getPosition()) {
                threshold = thre;
            }
        }
        if (threshold == null)  threshold = locThresholds.get(0);
        VolumeInfo volumeInfo = new VolumeInfo(threshold.getConstantA()
                ,threshold.getConstantB()
                ,threshold.getConstantC()
                ,threshold.getConstantD()
                ,threshold.getYellowThreshold()
                ,threshold.getRedThreshold()
                ,threshold.getMaxCapacity()
                ,50
                ,25);

        // Parameter 2: sensorValue
        int sensorValue = current.getAggerateValue();

        // Parameter 3: PreviousDataInfo
        int preVolum = 0;
        int prePadStatus = 0;
        if (previousVolumeStatusInfo != null) {
            preVolum = previousVolumeStatusInfo.getActualVolume();
            prePadStatus = previousVolumeStatusInfo.getPodStatus();
        }
        PreviousDataInfo previousDataInfo = DataAnalytics.getPreviousDataInfo(previous,
                preVolum, prePadStatus, current);
        VolumeStatusInfo volumeStatusInfo = DataAnalytics.smartzsdk.getvolume(volumeInfo
                , sensorValue,
                previousDataInfo);
        if (volumeStatusInfo != null) previousVolumeStatusInfo = volumeStatusInfo;
        if (volumeStatusInfo == null) return;
        Log.d("volumeStatusInfo", volumeStatusInfo.toString());
        analyticsPanel.showVolumeInfo(volumeStatusInfo);
    }

    private void prepareThreshold() {
        new Thread(){
            public void run() {
                try {
                    // Step 1: Receive user information from GetAllPatients api.
                    List<Group> groups = UserApi.getAllPatients("eyJhbGciOiJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9yZSNobWFjLXNoYTI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6ImQzMzljNDNlLTAzNTEtNGJhZi05YTNlLTdhOWM4YjUyYzljZSIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWUiOiJ0ZXN0c3UxIiwiaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvd3MvMjAwNS8wNS9pZGVudGl0eS9jbGFpbXMvZW1haWxhZGRyZXNzIjoidEB0LmNvbSIsImV4cCI6MTY1NTI2NDY4NywiaXNzIjoic2ltYXZpdGEuY29tIiwiYXVkIjoiYXVkaWVuY2UifQ.icTGzCtDCs8Qdbe9i_oYck4PE91EwvGkxixK6OkPtKk");

                    // Step 2: For now, choose any one of the patients of any group.
                    if(groups == null || groups.size() <= 0) return;
                    Group group = groups.get(0);
                    if (group == null) return;;
                    List<Patient> patients = group.getPatients();
                    if(patients == null || patients.size() <=0) return;
                    Patient patient = patients.get(0);
                    if(patient == null) return;

                    // Step 3: Get Manufacturers information from GetConfiguration api.
                    Configuration conf = ConfigurationApi.getConfigurationLatestRelease();

                    // Step 4: Filter out the specific product  through the product id in patient details.
                    List<Product> products = new ArrayList<>();
                    for(Manufacturer man : conf.getManufacturers()) {
                        for(Product pro : man.getProducts()) {
                            if (patient.getDayProductId() == pro.getId() || patient.getNightProductId() == pro.getId()) {
                                products.add(pro);
                            }
                        }
                    }
                    if (products.size() <= 0) return;
                    Product product = products.get(0);
                    thresholds = new ArrayList<>();
                    // Step 5: Get product thresholds of that specific product
                    // (Filter with a firmware version and hardware version which is available from
                    // DataInfo class. The result  is a list of thresholds for different positions).

                    for(Threshold thre : product.getProductThresholds()) {
                        thresholds.add(thre);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
    public void demos() {
        DataAcquisition acquisition = DataAcquisition.getInstance();
        boolean bleStatus = DataAcquisition.isBleOn();
        double ppm1 = DataAcquisition.getInstance().GetPPM("Smartz_Pod_44444444");
        double ppm2 = DataAcquisition.getInstance().GetPPM("44:44:44:44");
        double minPPM = DataAcquisition.getInstance().GetMinPPM();
        double maxPPM = DataAcquisition.getInstance().GetMaxPPM();
    }

}
