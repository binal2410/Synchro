package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.synchro.R;
import com.synchro.utils.SharedPref;

public class MarkerColorActivity extends AppCompatActivity {

    private RadioGroup rg_marker_color;
    private RadioButton rb_red, rb_yellow, rb_blue, rb_green, rb_pink;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_color);
        mContext = this;
        initUI();
        registerListener();
    }

    private void initUI() {
        rg_marker_color = findViewById(R.id.rg_marker_color);
        rb_red = findViewById(R.id.rb_red);
        rb_yellow = findViewById(R.id.rb_yellow);
        rb_blue = findViewById(R.id.rb_blue);
        rb_green = findViewById(R.id.rb_green);
        rb_pink = findViewById(R.id.rb_pink);

        if (SharedPref.getValue(mContext, SharedPref.MARKER_COLOR, mContext.getResources().getString(R.string.red)).equals(mContext.getResources().getString(R.string.red))) {
            rb_red.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_COLOR, mContext.getResources().getString(R.string.red)).equals(mContext.getResources().getString(R.string.yellow))) {
            rb_yellow.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_COLOR, mContext.getResources().getString(R.string.red)).equals(mContext.getResources().getString(R.string.blue))) {
            rb_blue.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_COLOR, mContext.getResources().getString(R.string.red)).equals(mContext.getResources().getString(R.string.green))) {
            rb_green.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_COLOR, mContext.getResources().getString(R.string.red)).equals(mContext.getResources().getString(R.string.pink))) {
            rb_pink.setChecked(true);
        }

    }

    private void registerListener() {
        rg_marker_color.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_red) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_COLOR,mContext.getResources().getString(R.string.red));
                }
                if (checkedId == R.id.rb_yellow) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_COLOR,mContext.getResources().getString(R.string.yellow));

                }
                if (checkedId == R.id.rb_blue) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_COLOR,mContext.getResources().getString(R.string.blue));

                }
                if (checkedId == R.id.rb_green) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_COLOR,mContext.getResources().getString(R.string.green));

                }
                if (checkedId == R.id.rb_pink) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_COLOR,mContext.getResources().getString(R.string.pink));

                }
            }
        });
    }
}