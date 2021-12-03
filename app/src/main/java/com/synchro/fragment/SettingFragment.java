package com.synchro.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
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
import com.synchro.activity.ChnageNameActivity;
import com.synchro.activity.FreeStyleSettingActivity;
import com.synchro.activity.IndoorOutdoorActivity;
import com.synchro.activity.LogsActivity;
import com.synchro.activity.MainActivity;
import com.synchro.activity.MarkerColorActivity;
import com.synchro.activity.MarkerShapeActivity;
import com.synchro.activity.MarkerSizeActivity;
import com.synchro.activity.PersonalInfoActivity;
import com.synchro.activity.ReactionSettingActivity;
import com.synchro.activity.SoftwareVersionActivity;
import com.synchro.activity.TargetSwitchingActivity;
import com.synchro.activity.UnitsActivity;
import com.synchro.activity.ZeroingSettingActivity;
import com.synchro.adapter.LogsAdapter;
import com.synchro.ble.BleDeviceActor;
import com.synchro.model.LogsModel;
import com.synchro.utils.AppMethods;
import com.synchro.utils.CheckSelfPermission;
import com.synchro.utils.SharedPref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.os.Build.VERSION.SDK_INT;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private TextView tv_logs, tv_softwareversion, tv_changeName, tv_freeStyleSetting,
            tv_reactionSetting,tv_zeroingSetting, tv_targetSwitching,tv_units,tv_indoor_outdoor,tv_personal_info,
            tv_marker_menu,tv_marker_color,tv_marker_shape;
    private boolean isFirstStoragePermission = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
    }

    private void initUi(View view) {
        tv_logs = view.findViewById(R.id.tv_logs);
        tv_softwareversion = view.findViewById(R.id.tv_softwareversion);
        tv_changeName = view.findViewById(R.id.tv_changeName);
        tv_freeStyleSetting = view.findViewById(R.id.tv_freeStyleSetting);
        tv_reactionSetting = view.findViewById(R.id.tv_reactionSetting);
        tv_zeroingSetting = view.findViewById(R.id.tv_zeroingSetting);
        tv_targetSwitching = view.findViewById(R.id.tv_targetSwitching);
        tv_units = view.findViewById(R.id.tv_units);
        tv_indoor_outdoor = view.findViewById(R.id.tv_indoor_outdoor);
        tv_personal_info = view.findViewById(R.id.tv_personal_info);
        tv_marker_menu = view.findViewById(R.id.tv_marker_menu);
        tv_marker_color = view.findViewById(R.id.tv_marker_color);
        tv_marker_shape = view.findViewById(R.id.tv_marker_shape);

        tv_logs.setOnClickListener(this::onClick);
        tv_softwareversion.setOnClickListener(this::onClick);
        tv_changeName.setOnClickListener(this::onClick);
        tv_freeStyleSetting.setOnClickListener(this::onClick);
        tv_reactionSetting.setOnClickListener(this::onClick);
        tv_reactionSetting.setOnClickListener(this::onClick);
        tv_zeroingSetting.setOnClickListener(this::onClick);
        tv_targetSwitching.setOnClickListener(this::onClick);
        tv_units.setOnClickListener(this::onClick);
        tv_indoor_outdoor.setOnClickListener(this::onClick);
        tv_personal_info.setOnClickListener(this::onClick);
        tv_marker_menu.setOnClickListener(this::onClick);
        tv_marker_color.setOnClickListener(this::onClick);
        tv_marker_shape.setOnClickListener(this::onClick);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_logs:
                Intent logIntent = new Intent(getActivity(), LogsActivity.class);
                startActivity(logIntent);
                break;

            case R.id.tv_softwareversion:
                if (BleDeviceActor.isConnected) {
                    Intent versionIntent = new Intent(getActivity(), SoftwareVersionActivity.class);
                    startActivity(versionIntent);
                }else {
                    AppMethods.setAlertDialog(getActivity(),getString(R.string.pls_connect_device),"");
                }
                break;

            case R.id.tv_changeName:
                if (BleDeviceActor.isConnected) {
                    Intent nameIntent = new Intent(getActivity(), ChnageNameActivity.class);
                    startActivity(nameIntent);
                }else {
                    AppMethods.setAlertDialog(getActivity(),getString(R.string.pls_connect_device),"");
                }
                break;

            case R.id.tv_freeStyleSetting:
                Intent styleIntent = new Intent(getActivity(), FreeStyleSettingActivity.class);
                startActivity(styleIntent);
                break;

            case R.id.tv_reactionSetting:
                Intent reactionIntent = new Intent(getActivity(), ReactionSettingActivity.class);
                startActivity(reactionIntent);
                break;

            case R.id.tv_zeroingSetting:
                Intent zeroingIntent = new Intent(getActivity(), ZeroingSettingActivity.class);
                startActivity(zeroingIntent);
                break;

            case R.id.tv_targetSwitching:
                if (checkStoragePermission()) {
                    Intent targetSwitching = new Intent(getActivity(), TargetSwitchingActivity.class);
                    startActivity(targetSwitching);
                }
                break;
            case R.id.tv_units:
                Intent unitsIntent = new Intent(getActivity(), UnitsActivity.class);
                startActivity(unitsIntent);
                break;
            case R.id.tv_indoor_outdoor:
                Intent indoorOutdoorIntent = new Intent(getActivity(), IndoorOutdoorActivity.class);
                startActivity(indoorOutdoorIntent);
                break;
            case R.id.tv_personal_info:
                Intent personalInfoIntent = new Intent(getActivity(), PersonalInfoActivity.class);
                startActivity(personalInfoIntent);
                break;
            case R.id.tv_marker_menu:
                Intent markerSizeIntent = new Intent(getActivity(), MarkerSizeActivity.class);
                startActivity(markerSizeIntent);
                break;
            case R.id.tv_marker_color:
                Intent markerColorIntent = new Intent(getActivity(), MarkerColorActivity.class);
                startActivity(markerColorIntent);
                break;
            case R.id.tv_marker_shape:
                Intent markerShapeIntent = new Intent(getActivity(), MarkerShapeActivity.class);
                startActivity(markerShapeIntent);
                break;
        }
    }


    private boolean checkStoragePermission() {
        boolean isPermissionCheck = false;
        if (isFirstStoragePermission) {
            isFirstStoragePermission = false;
            if (SDK_INT >= Build.VERSION_CODES.R) {
                requestPermission();
                isPermissionCheck = Environment.isExternalStorageManager();
            } else {
                //below android 11
                return CheckSelfPermission.checkStoragePermission(getActivity());
            }
        } else {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                requestPermission();
                isPermissionCheck = Environment.isExternalStorageManager();
            } else {
                //below android 11
                return CheckSelfPermission.checkStoragePermissionRetional(getActivity());
            }
        }
        return isPermissionCheck;
    }

    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getActivity().getPackageName())));
                    startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            }
        }
    }
}
