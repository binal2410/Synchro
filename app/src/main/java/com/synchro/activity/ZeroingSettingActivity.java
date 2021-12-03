package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.synchro.R;
import com.synchro.utils.SharedPref;

import static com.synchro.activity.GlobalApplication.context;

public class ZeroingSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_goalx, et_goaly, et_calix, et_caliy;
    private TextView tv_set;
    private RadioButton rb_indicativeMode, rb_darkmode, rb_wideDetectionZone, rb_narrowDetectionZone;
    private TextView tvGoalX,tvGoalY,tvClickToX,tvClickToY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zeroing_setting);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initUi();
    }

    private void initUi() {
        et_goalx = findViewById(R.id.et_goalx);
        et_goaly = findViewById(R.id.et_goaly);
        et_calix = findViewById(R.id.et_calix);
        et_caliy = findViewById(R.id.et_caliy);
        tv_set = findViewById(R.id.tv_set);
        rb_indicativeMode = findViewById(R.id.rb_indicativeMode);
        rb_darkmode = findViewById(R.id.rb_darkmode);
        rb_narrowDetectionZone = findViewById(R.id.rb_narrowDetectionZone);
        rb_wideDetectionZone = findViewById(R.id.rb_wideDetectionZone);
        rb_darkmode = findViewById(R.id.rb_darkmode);
        tvGoalX = findViewById(R.id.tvGoalX);
        tvGoalY= findViewById(R.id.tvGoalY);
        tvClickToX= findViewById(R.id.tvClickToX);
        tvClickToY= findViewById(R.id.tvClickToY);

        tv_set.setOnClickListener(this::onClick);

        et_goalx.setText(SharedPref.getValue(ZeroingSettingActivity.this, SharedPref.GOAL_X_PREF, "0"));
        et_goaly.setText(SharedPref.getValue(ZeroingSettingActivity.this, SharedPref.GOAL_Y_PREF, "0"));
        et_calix.setText(SharedPref.getValue(ZeroingSettingActivity.this, SharedPref.CALI_X_PREF, "1"));
        et_caliy.setText(SharedPref.getValue(ZeroingSettingActivity.this, SharedPref.CALI_y_PREF, "1"));
        if (SharedPref.getValue(ZeroingSettingActivity.this, SharedPref.ZEROING_INDICATIVE_MODE, 1)== 1){
            rb_indicativeMode.setChecked(true);
        }else {
            rb_darkmode.setChecked(true);
        }

        if (SharedPref.getValue(ZeroingSettingActivity.this, SharedPref.ZEROING_DETECTION_ZONE, getString(R.string.narrow_detection_zone)).equals(getString(R.string.wide_detection_zone))){
            rb_wideDetectionZone.setChecked(true);
        }else {
            rb_narrowDetectionZone.setChecked(true);
        }
        if (SharedPref.getValue(context, SharedPref.SELECTED_UNIT, context.getResources().getString(R.string.metric)).equals(context.getResources().getString(R.string.standard))) {
            tvGoalX.setText(context.getResources().getString(R.string.goal_x_inch));
            tvGoalY.setText(context.getResources().getString(R.string.goal_y_inch));
            tvClickToX.setText(context.getResources().getString(R.string.cali_x_inch));
            tvClickToY.setText(context.getResources().getString(R.string.cali_y_inch));
        }

        }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set:
                String goalX = et_goalx.getText().toString().trim();
                String goalY = et_goaly.getText().toString().trim();
                String caliX = et_calix.getText().toString().trim();
                String caliY = et_caliy.getText().toString().trim();

                if (goalX.equals("")) {
                    Toast.makeText(this, getString(R.string.pls_enter_golax), Toast.LENGTH_SHORT).show();
                } else if (goalY.equals("")) {
                    Toast.makeText(this, getString(R.string.pls_enter_golay), Toast.LENGTH_SHORT).show();
                } else if (caliX.equals("")) {
                    Toast.makeText(this, getString(R.string.pls_enter_calix), Toast.LENGTH_SHORT).show();
                } else if (caliY.equals("")) {
                    Toast.makeText(this, getString(R.string.pls_enter_caliy), Toast.LENGTH_SHORT).show();
                } else {
                    SharedPref.setValue(ZeroingSettingActivity.this, SharedPref.GOAL_X_PREF, goalX);
                    SharedPref.setValue(ZeroingSettingActivity.this, SharedPref.GOAL_Y_PREF, goalY);
                    SharedPref.setValue(ZeroingSettingActivity.this, SharedPref.CALI_X_PREF, caliX);
                    SharedPref.setValue(ZeroingSettingActivity.this, SharedPref.CALI_y_PREF, caliY);
                    if (rb_indicativeMode.isChecked()) {
                        SharedPref.setValue(ZeroingSettingActivity.this, SharedPref.ZEROING_INDICATIVE_MODE, 1);
                    }else {
                        SharedPref.setValue(ZeroingSettingActivity.this, SharedPref.ZEROING_INDICATIVE_MODE, 0);
                    }
                    if (rb_wideDetectionZone.isChecked()) {
                        SharedPref.setValue(ZeroingSettingActivity.this, SharedPref.ZEROING_DETECTION_ZONE, getString(R.string.wide_detection_zone));
                    } else {
                        SharedPref.setValue(ZeroingSettingActivity.this, SharedPref.ZEROING_DETECTION_ZONE, getString(R.string.narrow_detection_zone));
                    }
                    finish();
                }
                break;
        }
    }
}