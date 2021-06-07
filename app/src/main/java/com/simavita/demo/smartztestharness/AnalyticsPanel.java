package com.simavita.demo.smartztestharness;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.simavita.DataInfo;

public class AnalyticsPanel extends LinearLayout {

    TextView mCompanyId;
    TextView mConnectionStatus; // conn
    TextView mPosition; // pos
    TextView mFall;  // Fall Detection
    TextView mHardwareVersion; // HWVersion
    TextView mFirmwareVersion;
    TextView mSensor1Value; // s1
    TextView mSensor2Value; // s2
    TextView mRefineValue;
    TextView mAggerateValue;
    TextView mTickCounter; // Tick
    TextView mLastFallTickCounter;  // fallTick
    TextView mLastConnectedTickCounter;  // lastTick
    TextView mLastPositionTickCounter;    // posTick
    TextView mAx;  // ax
    TextView mAy;  // ay
    TextView mAz;  // az
    TextView mPitchAngle;  // PitchAngle
    TextView mVoltage; // vol
    TextView mBatteryPercentage; //batt
    TextView mBatteryStatus; //stat
    TextView mTemperature; // temp
    TextView mRawTemperature;
    TextView mTimeInPad; // padT
    TextView mTimeInPosition; //posT
    TextView mTimeInFall;

    // IntrpAx
    // IntrpAy
    // IntrpAz
    // PodVersion
    // Urine
    // Hydration
    // PPM

    public AnalyticsPanel(Context context) {
        super(context);
        initLayout(context);
    }

    public AnalyticsPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public AnalyticsPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    public AnalyticsPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initLayout(context);
    }
    private void initLayout(Context context) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.view_analytics_panel, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(layout, params);
         mConnectionStatus = layout.findViewById(R.id.mConnectionStatus); // conn
         mPosition = layout.findViewById(R.id.mPosition); // pos
         mFall = layout.findViewById(R.id.mFall);  // Fall Detection
         mHardwareVersion = layout.findViewById(R.id.mHardwareVersion); // HWVersion
         mSensor1Value = layout.findViewById(R.id.mSensor1Value); // s1
         mSensor2Value = layout.findViewById(R.id.mSensor2Value); // s2
         mTickCounter = layout.findViewById(R.id.mTickCounter); // Tick
         mLastFallTickCounter = layout.findViewById(R.id.mLastFallTickCounter);  // fallTick
         mLastConnectedTickCounter = layout.findViewById(R.id.mLastConnectedTickCounter);  // lastTick
         mLastPositionTickCounter = layout.findViewById(R.id.mLastPositionTickCounter);    // posTick
         mAx = layout.findViewById(R.id.mAx);  // ax
         mAy = layout.findViewById(R.id.mAy);  // ay
         mAz = layout.findViewById(R.id.mAz);  // az
         mPitchAngle = layout.findViewById(R.id.mPitchAngle);  // PitchAngle
         mVoltage = layout.findViewById(R.id.mVoltage); // vol
         mBatteryPercentage = layout.findViewById(R.id.mBatteryPercentage); //batt
         mBatteryStatus = layout.findViewById(R.id.mBatteryStatus); //stat
         mTemperature = layout.findViewById(R.id.mTemperature); // temp
         mTimeInPad = layout.findViewById(R.id.mTimeInPad); // padT
         mTimeInPosition = layout.findViewById(R.id.mTimeInPosition); //posT
    }
    public void showDataInfo(DataInfo info) {
        if (info == null) return;
        mConnectionStatus.setText(String.valueOf(info.getConnectionStatus()));
        mPosition.setText(String.valueOf(info.getPosition()));
        mFall.setText(String.valueOf(info.getFall()));
        mHardwareVersion.setText(String.valueOf(info.getHardwareVersion()));
        mSensor1Value.setText(String.valueOf(info.getSensor1Value()));
        mSensor2Value.setText(String.valueOf(info.getSensor2Value()));
        mTickCounter.setText(String.valueOf(info.getTickCounter()));
        mLastFallTickCounter.setText(String.valueOf(info.getLastFallTickCounter()));
        mLastConnectedTickCounter.setText(String.valueOf(info.getLastConnectedTickCounter()));
        mLastPositionTickCounter.setText(String.valueOf(info.getLastPositionTickCounter()));
        mAx.setText(String.valueOf(info.getAx()));
        mAy.setText(String.valueOf(info.getAy()));
        mAz.setText(String.valueOf(info.getAz()));
        mPitchAngle.setText(String.valueOf(info.getPitchAngle()));
        mVoltage.setText(String.valueOf(info.getVoltage()));
        mBatteryPercentage.setText(String.valueOf(info.getBatteryPercentage()));
        mBatteryStatus.setText(String.valueOf(info.getBatteryStatus()));
        mTemperature.setText(String.valueOf(info.getTemperature()));
        mTimeInPad.setText(String.valueOf(info.getTimeInPad()));
        mTimeInPosition.setText(String.valueOf(info.getTimeInPosition()));

    }
}
