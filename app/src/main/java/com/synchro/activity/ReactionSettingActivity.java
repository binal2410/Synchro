package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.synchro.R;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.SharedPref;

import java.util.Timer;
import java.util.TimerTask;

public class ReactionSettingActivity extends AppCompatActivity {
    private Spinner spinner_preparationTime, spinner_noOfRounds, spinner_noOfHits;
    private EditText et_time_penalty;
    private RadioGroup rg_detectionZone;
    private RadioButton rb_wideDetectionZone, rb_narrowDetectionZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_setting);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initUi();
    }

    private void initUi() {
        spinner_preparationTime = findViewById(R.id.spinner_preparationTime);
        spinner_noOfRounds = findViewById(R.id.spinner_noOfRounds);
        spinner_noOfHits = findViewById(R.id.spinner_noOfHits);
        et_time_penalty = findViewById(R.id.et_time_penalty);
        rg_detectionZone = findViewById(R.id.rg_detectionZone);
        rb_wideDetectionZone = findViewById(R.id.rb_wideDetectionZone);
        rb_narrowDetectionZone = findViewById(R.id.rb_narrowDetectionZone);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.preparation_time, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner_preparationTime.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.no_of_rounds, R.layout.spinner_item);
        adapter1.setDropDownViewResource(R.layout.spinner_item);
        spinner_noOfRounds.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.no_of_hits, R.layout.spinner_item);
        adapter2.setDropDownViewResource(R.layout.spinner_item);
        spinner_noOfHits.setAdapter(adapter2);

        int preparationTime = SharedPref.getValue(ReactionSettingActivity.this, SharedPref.PREPARATION_TIME_PREF, AppConstants.preparation_time);
        int noOFRound = SharedPref.getValue(ReactionSettingActivity.this, SharedPref.NO_OF_ROUND_PREF, AppConstants.no_of_round);
        int noOfHits = SharedPref.getValue(ReactionSettingActivity.this, SharedPref.NO_OF_HIT_PREF, AppConstants.no_of_hit);
        et_time_penalty.setText(SharedPref.getValue(ReactionSettingActivity.this, SharedPref.TIME_PENALTY_PREF, "1"));
        String[] preparationTimeArray = getResources().getStringArray(R.array.preparation_time);
        for (int i = 0; i < preparationTimeArray.length; i++) {
            if (preparationTimeArray[i].equals(preparationTime + "")) {
                spinner_preparationTime.setSelection(i);
                continue;
            }
        }

        String[] noOFRoundArray = getResources().getStringArray(R.array.no_of_rounds);
        for (int i = 0; i < noOFRoundArray.length; i++) {
            if (noOFRoundArray[i].equals(noOFRound + "")) {
                spinner_noOfRounds.setSelection(i);
                continue;
            }
        }

        String[] noOfHitsArray = getResources().getStringArray(R.array.no_of_hits);
        for (int i = 0; i < noOfHitsArray.length; i++) {
            if (noOfHitsArray[i].equals(noOfHits + "")) {
                spinner_noOfHits.setSelection(i);
                continue;
            }
        }

        if (SharedPref.getValue(ReactionSettingActivity.this, SharedPref.RECTION_DETECTION_ZONE, getString(R.string.narrow_detection_zone)).equals(getString(R.string.wide_detection_zone))){
            rb_wideDetectionZone.setChecked(true);
        }else {
            rb_narrowDetectionZone.setChecked(true);
        }

        spinner_preparationTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPref.setValue(ReactionSettingActivity.this, SharedPref.PREPARATION_TIME_PREF, Integer.parseInt(spinner_preparationTime.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_noOfRounds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPref.setValue(ReactionSettingActivity.this, SharedPref.NO_OF_ROUND_PREF, Integer.parseInt(spinner_noOfRounds.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_noOfHits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPref.setValue(ReactionSettingActivity.this, SharedPref.NO_OF_HIT_PREF, Integer.parseInt(spinner_noOfHits.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        et_time_penalty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                stopTimer();
                starttimer();
            }
        });

        rg_detectionZone.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rb_wideDetectionZone.isChecked()) {
                    SharedPref.setValue(ReactionSettingActivity.this, SharedPref.RECTION_DETECTION_ZONE, getString(R.string.wide_detection_zone));
                } else {
                    SharedPref.setValue(ReactionSettingActivity.this, SharedPref.RECTION_DETECTION_ZONE, getString(R.string.narrow_detection_zone));
                }
            }
        });
    }

    private Timer timer;
    private void starttimer() {
        timer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                validateEditText();
            }
        };
        timer.scheduleAtFixedRate(t, 1000, 1000);
    }

    private void stopTimer() {
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateEditText() {
        stopTimer();
        String s = et_time_penalty.getText().toString().trim();
        if (s.equals("")) {
            SharedPref.setValue(ReactionSettingActivity.this, SharedPref.TIME_PENALTY_PREF, "1");
        } else {
            double value = Double.parseDouble(s.trim());
            if (value < 0 || value > 5) {
                AppMethods.setAlertDialog(ReactionSettingActivity.this, getString(R.string.time_penalty_validation), null);
                return false;
            } else {
                SharedPref.setValue(ReactionSettingActivity.this, SharedPref.TIME_PENALTY_PREF, value + "");
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (validateEditText()) {
            super.onBackPressed();
        }
    }
}