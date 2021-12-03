package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.synchro.BuildConfig;
import com.synchro.R;
import com.synchro.ble.BleCharacteristic;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.BleCallback;

import static com.synchro.activity.MainActivity.bleDeviceActor;

public class SoftwareVersionActivity extends AppCompatActivity implements BleCallback {
    private TextView tv_appVersion, tv_deviceSoftwareVersion, tv_deviceSerialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_version);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initUi();
        bleDeviceActor.setCallback(this);
        BleCharacteristic.WriteCharacteristic(SoftwareVersionActivity.this, AppMethods.convertStringToByte(AppConstants.cmd_softwareversion));
    }

    private void initUi() {
        tv_appVersion = findViewById(R.id.tv_appVersion);
        tv_deviceSoftwareVersion = findViewById(R.id.tv_deviceSoftwareVersion);
        tv_deviceSerialNumber = findViewById(R.id.tv_deviceSerialNumber);
        tv_appVersion.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public void scanCallback(String deviceName, String macAddress, String isCoded) {

    }

    @Override
    public void connectionCallback(boolean isConnected, String status) {

    }

    @Override
    public void onCharacteristicChanged(byte[] data) {
        String dataString = AppMethods.convertByteToString(data);
        if (dataString.toLowerCase().contains(AppConstants.cmd_softwareversion_reply)) {
            BleCharacteristic.WriteCharacteristic(SoftwareVersionActivity.this, AppMethods.convertStringToByte(AppConstants.cmd_serialnumber));
            String version = dataString.substring(dataString.indexOf("_") + 1, dataString.length());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_deviceSoftwareVersion.setText(version);
                }
            });
        }else if (dataString.toLowerCase().contains(AppConstants.cmd_serialnumber_reply)) {
            String number = dataString.substring(dataString.indexOf("_") + 1, dataString.length());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_deviceSerialNumber.setText(number);
                }
            });
        }
    }

    @Override
    public void connectClickCallback(boolean isConnectClick) {

    }

    @Override
    public void drawTrainDot(float x, float y) {

    }

    @Override
    public void bluetoothCallback(boolean isOn) {

    }
}