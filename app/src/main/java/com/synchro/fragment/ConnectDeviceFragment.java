package com.synchro.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synchro.R;
import com.synchro.activity.GlobalApplication;
import com.synchro.activity.MainActivity;
import com.synchro.adapter.ConnectDeviceAdapter;
import com.synchro.ble.BleCharacteristic;
import com.synchro.model.ConnectDeviceModel;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.BleCallback;
import com.synchro.utils.CheckSelfPermission;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.synchro.activity.MainActivity.bleDeviceActor;

public class ConnectDeviceFragment extends Fragment implements View.OnClickListener, BleCallback {
    private TextView tv_scan;
    private RecyclerView rv_devices;
    private ConnectDeviceAdapter connectDeviceAdapter = null;
    private ArrayList<ConnectDeviceModel> deviceModelArrayList = new ArrayList<>();
    public static int connectPosition = 0;
    private Timer timer;
    private TimerTask timerTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_device, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        bleDeviceActor.setCallback(this);
        initUi(view);
    }

    private void initUi(View view) {
        tv_scan = view.findViewById(R.id.tv_scan);
        rv_devices = view.findViewById(R.id.rv_devices);

        tv_scan.setOnClickListener(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_devices.setLayoutManager(mLayoutManager);
        updateList();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
        checkConnectionState();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_scan:
                if (tv_scan.getText().toString().trim().equals(getString(R.string.scan))) {
                    if (CheckSelfPermission.isBluetoothOn(getActivity())) {
                        startScanUi();
                    }
                } else {
                    stopScanUi();
                }
                break;
        }
    }

    private void checkConnectionState(){
        if (deviceModelArrayList != null && deviceModelArrayList.size() > connectPosition) {
            ConnectDeviceModel connectedModel = deviceModelArrayList.get(connectPosition);
            if (!AppMethods.isDeviceConnected(getActivity(), connectedModel.macAddress)) {
                deviceModelArrayList.remove(connectPosition);
                updateList();
            }
        }

        if (GlobalApplication.connectedModel==null){
            return;
        }

        if (deviceModelArrayList.size()==0 && AppMethods.isDeviceConnected(getActivity(), GlobalApplication.connectedModel.macAddress)) {
            connectPosition = 0;
            deviceModelArrayList.add(GlobalApplication.connectedModel);
            updateList();
        }
    }

    private void startScanUi() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_scan.setText(getString(R.string.stop_scan));
            }
        });

        ConnectDeviceModel connectedModel = null;
        if (deviceModelArrayList != null && deviceModelArrayList.size() > connectPosition) {
            connectedModel = deviceModelArrayList.get(connectPosition);
        }
        deviceModelArrayList = new ArrayList<>();
        if (connectedModel != null && connectedModel.isConnected) {
            deviceModelArrayList.add(connectedModel);
            connectPosition = 0;
        }
        updateList();
        bleDeviceActor.startScan();
    }

    private void stopScanUi() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_scan.setText(getString(R.string.scan));
            }
        });
        bleDeviceActor.stopScan();
    }


    @Override
    public void scanCallback(String deviceName, String macAddress, String isCoded) {
        if (!isContainScanArrayList(macAddress, isCoded)) {
            deviceName = isContainDeviceName(deviceName);
            deviceModelArrayList.add(new ConnectDeviceModel(deviceName, macAddress, isCoded, false, false));
            updateList();
        }
    }


    private String lastConnectedDeviveName = "";

    @Override
    public void connectionCallback(boolean isConnected, String status) {
        checkConnectionState();
        if (isConnected) {
            BleCharacteristic.WriteCharacteristic(getActivity(), AppMethods.convertStringToByte(AppConstants.cmd_batteryLevel));
            MainActivity.isStartTraining = false;
            ConnectDeviceModel connectedModel = deviceModelArrayList.get(connectPosition);
            connectedModel.isConnected = true;
            lastConnectedDeviveName = connectedModel.deviceName;
            AppMethods.writeDataTostrem(getActivity(), "Connected to " + connectedModel.deviceName + ", Coded=" + connectedModel.isCoded);
            deviceModelArrayList.remove(connectPosition);
            ArrayList<ConnectDeviceModel> deviceModelArrayListNew = new ArrayList<>();
            deviceModelArrayListNew.add(connectedModel);
            GlobalApplication.connectedModel = connectedModel;
            for (ConnectDeviceModel model : deviceModelArrayList) {
                deviceModelArrayListNew.add(model);
            }
            deviceModelArrayList = deviceModelArrayListNew;
            updateList();
            connectPosition = 0;
            AppMethods.hideProgressDialog(getActivity());
        } else {
            Log.d(AppConstants.TAG, "from disconnect click: " + isdisconnectClicked);

            if (connectPosition == 0) {
                if (!isdisconnectClicked) {
                    if (!status.equals("")) {
                        AppMethods.setAlertDialog(getActivity(), status, "");
                    } else {
                        AppMethods.setAlertDialog(getActivity(), getString(R.string.device_disconnected), "");
                    }
                    stopScanUi();
                } else {
                    isdisconnectClicked = false;
                }
            }
            if (!status.equals("")) {
//                AppMethods.writeDataTostrem(getActivity(), status);
            } else {
                AppMethods.writeDataTostrem(getActivity(), "Disconnected from " + lastConnectedDeviveName);
            }
            AppMethods.hideProgressDialog(getActivity());
        }
    }

    @Override
    public void onCharacteristicChanged(byte[] data) {
        String dataString = AppMethods.convertByteToString(data).toLowerCase();
        if (dataString.contains(AppConstants.cmd_batteryLevel_reply)) {
            String[] dataarray = dataString.split("_");
            if (dataarray.length >= 2) {
                String batterylevel = dataarray[1];
                if (batterylevel.startsWith("-")) {
                    batterylevel = "0";
                }
                int batterylevelint = (int) Float.parseFloat(batterylevel);
                deviceModelArrayList.get(0).battery_level = batterylevelint + "";
                deviceModelArrayList.get(0).battery_level_time = System.currentTimeMillis();
                updateList();
            }
        } else if (dataString.contains(AppConstants.Accepted_Blink_res)) {
            deviceModelArrayList.get(connectPosition).isBlink = true;
            updateList();
        }
    }

    private boolean isdisconnectClicked = false;

    @Override
    public void connectClickCallback(boolean isConnectClick) {
        if (isConnectClick) {
            isdisconnectClicked = false;
            stopScanUi();
        } else {
            isdisconnectClicked = true;
            startScanUi();
        }
    }

    @Override
    public void drawTrainDot(float x, float y) {

    }

    @Override
    public void bluetoothCallback(boolean isOn) {
        stopScanUi();
        deviceModelArrayList = new ArrayList<>();
        updateList();
    }

    private void updateList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectDeviceAdapter = new ConnectDeviceAdapter(getActivity(), deviceModelArrayList, ConnectDeviceFragment.this);
                rv_devices.setAdapter(connectDeviceAdapter);
            }
        });
    }

    private boolean isContainScanArrayList(String macAddress, String isCoded) {
        for (ConnectDeviceModel model : deviceModelArrayList) {
            if (model.macAddress.toUpperCase().equals(macAddress.toUpperCase())) {
                if (!model.isCoded.equals(isCoded)) {
                    model.isCoded = isCoded;
                    updateList();
                }
                return true;
            }
        }
        return false;
    }

    private void startTimer() {
        stoptimertask();
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 10 * 60 * 1000);
    }

    private void stoptimertask() {
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {

        }
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                if (deviceModelArrayList.size() > 0 && deviceModelArrayList.get(0).isConnected) {
                    if ((System.currentTimeMillis() - deviceModelArrayList.get(0).battery_level_time) > 10 * 60 * 1000) {
                        BleCharacteristic.WriteCharacteristic(getActivity(), AppMethods.convertStringToByte(AppConstants.cmd_batteryLevel));
                    }
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        stoptimertask();
    }

    private synchronized String isContainDeviceName(String deviceName) {
        String updated = deviceName;
        for (ConnectDeviceModel model : deviceModelArrayList) {
            String name = model.deviceName;
            int count = 2;
            if (name.contains("#")) {
                String[] nameArray = name.split("#");
                if (nameArray.length > 1) {
                    name = nameArray[0].trim();
                    count = Integer.parseInt(nameArray[1].trim()) + 1;
                }

            }
            if (name.toUpperCase().equals(deviceName.toUpperCase())) {
                updated = name + " #" + count;
            }
        }
        return updated;
    }
}
