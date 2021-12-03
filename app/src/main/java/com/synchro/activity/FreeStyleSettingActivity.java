package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.synchro.R;
import com.synchro.fragment.FreeStyleStatFragment;
import com.synchro.fragment.TrainFreeStyleFragment;
import com.synchro.utils.SharedPref;

public class FreeStyleSettingActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioButton rb_indicativeMode, rb_darkmode, rb_report_afterHit, rb_reportStopped, rb_wideDetectionZone, rb_narrowDetectionZone,rb_velocity_on,rb_velocity_off;
    private TextView tv_report;
    private RadioGroup rg_report, rg_mode, rg_detectionZone,rg_velocity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_style_setting);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initUi();
    }

    private void initUi() {
        rb_indicativeMode = findViewById(R.id.rb_indicativeMode);
        rb_darkmode = findViewById(R.id.rb_darkmode);
        tv_report = findViewById(R.id.tv_report);
        rg_report = findViewById(R.id.rg_report);
        rb_report_afterHit = findViewById(R.id.rb_report_afterHit);
        rb_reportStopped = findViewById(R.id.rb_reportStopped);
        rg_mode = findViewById(R.id.rg_mode);
        rg_velocity = findViewById(R.id.rg_velocity);
        rg_detectionZone = findViewById(R.id.rg_detectionZone);
        rb_wideDetectionZone = findViewById(R.id.rb_wideDetectionZone);
        rb_narrowDetectionZone = findViewById(R.id.rb_narrowDetectionZone);
        rb_velocity_on=findViewById(R.id.rb_velocity_on);
        rb_velocity_off=findViewById(R.id.rb_velocity_off);

        rg_report.setOnCheckedChangeListener(this);
        rg_mode.setOnCheckedChangeListener(this);
        rg_detectionZone.setOnCheckedChangeListener(this);
        rg_velocity.setOnCheckedChangeListener(this);

        String freestyleSetting = SharedPref.getValue(FreeStyleSettingActivity.this, SharedPref.FREE_STYLE_SETTING_PREF,"start_tarin_req_indicative");
        if (freestyleSetting.equals("start_tarin_req_indicative")){
            rb_indicativeMode.setChecked(true);
        }else {
            rb_darkmode.setChecked(true);
            if (freestyleSetting.equals("start_tarin_req_darkReportAfterEachHit")){
                rb_report_afterHit.setChecked(true);
            }else {
                rb_reportStopped.setChecked(true);
            }
        }

        if (SharedPref.getValue(FreeStyleSettingActivity.this, SharedPref.FREESTYLE_DETECTION_ZONE, getString(R.string.narrow_detection_zone)).equals(getString(R.string.wide_detection_zone))){
            rb_wideDetectionZone.setChecked(true);
        }else {
            rb_narrowDetectionZone.setChecked(true);
        }
        if (SharedPref.getValue(FreeStyleSettingActivity.this, SharedPref.FREE_STYLE_VELOCITY_ENABLE_DISABLE, "Off").equals("Off")){
            rb_velocity_off.setChecked(true);
        }else {
            rb_velocity_on.setChecked(true);
        }

        setReportUi();
    }

    private void setReportUi() {
        if (rb_darkmode.isChecked()) {
            tv_report.setVisibility(View.VISIBLE);
            rg_report.setVisibility(View.VISIBLE);
            if (rb_report_afterHit.isChecked()) {
                SharedPref.setValue(FreeStyleSettingActivity.this, SharedPref.FREE_STYLE_SETTING_PREF, "start_tarin_req_darkReportAfterEachHit");
            } else {
                SharedPref.setValue(FreeStyleSettingActivity.this, SharedPref.FREE_STYLE_SETTING_PREF, "start_tarin_req_darkReportWhenStopped");
            }
        } else {
            tv_report.setVisibility(View.GONE);
            rg_report.setVisibility(View.GONE);
            SharedPref.setValue(FreeStyleSettingActivity.this, SharedPref.FREE_STYLE_SETTING_PREF, "start_tarin_req_indicative");
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.rg_mode:
                setReportUi();
                break;

            case R.id.rg_report:
                setReportUi();
                break;

            case R.id.rg_detectionZone:
                if (rb_wideDetectionZone.isChecked()) {
                    SharedPref.setValue(FreeStyleSettingActivity.this, SharedPref.FREESTYLE_DETECTION_ZONE, getString(R.string.wide_detection_zone));
                } else {
                    SharedPref.setValue(FreeStyleSettingActivity.this, SharedPref.FREESTYLE_DETECTION_ZONE, getString(R.string.narrow_detection_zone));
                }
                break;
            case R.id.rg_velocity:
                if (rb_velocity_off.isChecked()) {
                    SharedPref.setValue(FreeStyleSettingActivity.this, SharedPref.FREE_STYLE_VELOCITY_ENABLE_DISABLE, "Off");
                    try {
                        TrainFreeStyleFragment.tv_target_velocity.setVisibility(View.GONE);

                    }
                    catch (NullPointerException e)
                    {

                    }
                } else {
                    SharedPref.setValue(FreeStyleSettingActivity.this, SharedPref.FREE_STYLE_VELOCITY_ENABLE_DISABLE, "On");
                    try {
                        TrainFreeStyleFragment.tv_target_velocity.setVisibility(View.VISIBLE);
                        TrainFreeStyleFragment.tv_target_velocity.setText("Target Velocity: "+SharedPref.getValue(this, SharedPref.VELOCITY,0.0+""));

                    }
                    catch (NullPointerException e)
                    {

                    }
                }
                break;
        }
    }
}