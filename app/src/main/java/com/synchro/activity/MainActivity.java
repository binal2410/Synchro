package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bugfender.sdk.Bugfender;
import com.synchro.R;
import com.synchro.ble.BleDeviceActor;
import com.synchro.fragment.ConnectDeviceFragment;
import com.synchro.fragment.FreeStyleStatFragment;
import com.synchro.fragment.SettingFragment;
import com.synchro.fragment.TrainFreeStyleFragment;
import com.synchro.fragment.TrainReactionFragment;
import com.synchro.fragment.ZeroingFragment;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.CheckSelfPermission;
import com.synchro.utils.SharedPref;

import static com.synchro.ble.BleDeviceActor.reconnectDevice;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static BleDeviceActor bleDeviceActor = null;
    private TextView tv_footer_connect, tv_footer_tarin, tv_footer_setting;
    private RelativeLayout rl_main;
    public static boolean isStartTraining = false;
    public static boolean isStartZeroing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppMethods.setLocale(MainActivity.this);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initUi();
        addDefaultFragment(new ConnectDeviceFragment());
        bleDeviceActor = new BleDeviceActor(MainActivity.this);
        Bugfender.d("Test", "Hello world!");
        registerReceiver();
    }

    private void initUi() {
        tv_footer_connect = findViewById(R.id.tv_footer_connect);
        tv_footer_tarin = findViewById(R.id.tv_footer_tarin);
        tv_footer_setting = findViewById(R.id.tv_footer_setting);
        rl_main = findViewById(R.id.rl_main);

        tv_footer_connect.setOnClickListener(this::onClick);
        tv_footer_tarin.setOnClickListener(this::onClick);
        tv_footer_setting.setOnClickListener(this::onClick);

        setClickedFooter(1, true);
        setClickedFooter(2, false);
        setClickedFooter(3, false);
    }

    private void addDefaultFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public void addReplacedFragment(Fragment fragment, boolean isAddToback, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("from", tag);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        if (isAddToback) {
            if (tag.equals("")) {
                fragmentTransaction.addToBackStack(null);
            } else {
                fragmentTransaction.addToBackStack("FreeStyleStatFragment");
            }
        }
        fragmentTransaction.commit();
    }

    private boolean chechIsReactionRunning() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (f instanceof TrainReactionFragment) {
            return ((TrainReactionFragment) f).isReactionRunning();
        }
        return false;
    }


    @Override
    public void onClick(View view) {
        if (chechIsReactionRunning()) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_footer_connect:
                if (tv_footer_connect.getTag().toString().equals("0")) {
                    String currentTraining = GlobalApplication.currentTraining;
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
                    if (f instanceof FreeStyleStatFragment) {
                        FragmentManager fm = getSupportFragmentManager();
                        fm.popBackStack();
//                        fm.popBackStack("FreeStyleStatFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    onBackPressed();
                    setClickedFooter(1, true);
                    setClickedFooter(2, false);
                    setClickedFooter(3, false);
                    GlobalApplication.currentTraining = currentTraining;
                }
                break;

            case R.id.tv_footer_tarin:
                if (BleDeviceActor.isConnected) {
                    if (SharedPref.getSelectedTarget(MainActivity.this, SharedPref.SELECTED_TARGET_SWITCHING_PREF,null)==null){
                        AppMethods.setAlertDialog(MainActivity.this, getString(R.string.select_target_alert), "");
                        return;
                    }
                    if (tv_footer_tarin.getTag().toString().equals("0")) {
                        Fragment fragment = new TrainFreeStyleFragment();
                        if (GlobalApplication.currentTraining.equals("TrainReactionFragment")) {
                            fragment = new TrainReactionFragment();
                        } else if (GlobalApplication.currentTraining.equals("ZeroingFragment")) {
                            fragment = new ZeroingFragment();
                        }
                        if (tv_footer_connect.getTag().toString().equals("1")) {
                            addReplacedFragment(fragment, true, "");
                        } else {
                            addReplacedFragment(fragment, false, "");
                        }
                        setClickedFooter(1, false);
                        setClickedFooter(2, true);
                        setClickedFooter(3, false);
                    }
                } else {
                    AppMethods.setAlertDialog(MainActivity.this, getString(R.string.pls_connect_device), "");
                }
                break;

            case R.id.tv_footer_setting:
                if (tv_footer_setting.getTag().toString().equals("0")) {
                    String currentTraining = GlobalApplication.currentTraining;
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
                    if (f instanceof FreeStyleStatFragment) {
                        FragmentManager fm = getSupportFragmentManager();
                        fm.popBackStack();
                    }
                    if (tv_footer_connect.getTag().toString().equals("1")) {
                        addReplacedFragment(new SettingFragment(), true, "");
                    } else {
                        addReplacedFragment(new SettingFragment(), false, "");
                    }
                    setClickedFooter(1, false);
                    setClickedFooter(2, false);
                    setClickedFooter(3, true);
                    GlobalApplication.currentTraining = currentTraining;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (f instanceof TrainFreeStyleFragment || f instanceof  ZeroingFragment) {
            setClickedFooter(1, false);
            setClickedFooter(2, true);
            setClickedFooter(3, false);
        } else {
            setClickedFooter(1, true);
            setClickedFooter(2, false);
            setClickedFooter(3, false);
        }

    }

    private void setClickedFooter(int position, boolean isSelected) {
        if (isSelected) {
            switch (position) {
                case 1:
                    tv_footer_connect.setTextColor(getColor(R.color.connect_btn_color));
                    tv_footer_connect.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.connect_active), null, null);
                    tv_footer_connect.setTag("1");
                    break;

                case 2:
                    tv_footer_tarin.setTextColor(getColor(R.color.connect_btn_color));
                    tv_footer_tarin.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.train_active), null, null);
                    tv_footer_tarin.setTag("1");
                    break;

                case 3:
                    tv_footer_setting.setTextColor(getColor(R.color.connect_btn_color));
                    tv_footer_setting.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.log_active), null, null);
                    tv_footer_setting.setTag("1");
                    break;
            }
        } else {
            switch (position) {
                case 1:
                    tv_footer_connect.setTextColor(getColor(R.color.footer_disable_color));
                    tv_footer_connect.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.connect_deactive), null, null);
                    tv_footer_connect.setTag("0");
                    break;

                case 2:
                    tv_footer_tarin.setTextColor(getColor(R.color.footer_disable_color));
                    tv_footer_tarin.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.train_deactive), null, null);
                    tv_footer_tarin.setTag("0");
                    break;

                case 3:
                    tv_footer_setting.setTextColor(getColor(R.color.footer_disable_color));
                    tv_footer_setting.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.log_deactive), null, null);
                    tv_footer_setting.setTag("0");
                    break;
            }
        }
    }

    private void registerReceiver(){
        unregiasterReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        registerReceiver(bluetoothReceiver, intentFilter);
    }

    private void unregiasterReceiver(){
        try {
            if (bluetoothReceiver!=null){
                unregisterReceiver(bluetoothReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int state;

            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    if (state == BluetoothAdapter.STATE_OFF) {
                        try {
                            if (BleDeviceActor.bleCallback!=null){
                                BleDeviceActor.bleCallback.bluetoothCallback(false);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        CheckSelfPermission.isBluetoothOn(MainActivity.this);
                    }else if (state == BluetoothAdapter.STATE_ON){
                        Log.d(AppConstants.TAG, "Bluetooth state on");
                        reconnectDevice();
                    }
                    break;


                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregiasterReceiver();
        super.onDestroy();
    }
}