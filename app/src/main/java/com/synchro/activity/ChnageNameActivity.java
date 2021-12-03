package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.synchro.R;
import com.synchro.ble.BleCharacteristic;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.BleCallback;

import static com.synchro.activity.MainActivity.bleDeviceActor;

public class ChnageNameActivity extends AppCompatActivity implements BleCallback {
    private EditText et_newName;
    private TextView tv_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chnage_name);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initui();
        bleDeviceActor.setCallback(this);
    }

    private void initui() {
        et_newName = findViewById(R.id.et_newName);
        tv_change = findViewById(R.id.tv_change);

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_newName.getText().toString().trim();
                if (name.equals("")) {
                    Toast.makeText(ChnageNameActivity.this, getString(R.string.pls_enter_new_name), Toast.LENGTH_SHORT).show();
                } else {
                    AppMethods.showProgressDialog(ChnageNameActivity.this,getString(R.string.pls_wait));
                    String bleData = AppConstants.cmd_changename+name;
                    BleCharacteristic.WriteCharacteristic(ChnageNameActivity.this, AppMethods.convertStringToByte(bleData));
                }
            }
        });
    }

    @Override
    public void scanCallback(String deviceName, String macAddress, String isCoded) {

    }

    @Override
    public void connectionCallback(boolean isConnected, String status) {

    }

    @Override
    public void onCharacteristicChanged(byte[] data) {
      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              try {
                  AppMethods.hideProgressDialog(ChnageNameActivity.this);
                 // onBackPressed();
              }
              catch (NullPointerException e)
              {

              }

          }
      });
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