package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.synchro.R;
import com.synchro.utils.SharedPref;

public class MarkerShapeActivity extends AppCompatActivity {

    private RadioGroup rg_marker_shape;
    private RadioButton rb_circle, rb_rectangle, rb_pentagon, rb_oval, rb_star;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_shape);
        mContext = this;
        initUI();
        registerListener();
    }
    private void initUI() {
        rg_marker_shape = findViewById(R.id.rg_marker_shape);
        rb_circle = findViewById(R.id.rb_circle);
        rb_rectangle = findViewById(R.id.rb_rectangle);
        rb_pentagon = findViewById(R.id.rb_pentagon);
        rb_oval = findViewById(R.id.rb_oval);
        rb_star = findViewById(R.id.rb_star);

        if (SharedPref.getValue(mContext, SharedPref.MARKER_SHAPE, mContext.getResources().getString(R.string.circle)).equals(mContext.getResources().getString(R.string.circle))) {
            rb_circle.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_SHAPE, mContext.getResources().getString(R.string.circle)).equals(mContext.getResources().getString(R.string.rectangle))) {
            rb_rectangle.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_SHAPE, mContext.getResources().getString(R.string.circle)).equals(mContext.getResources().getString(R.string.pentagon))) {
            rb_pentagon.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_SHAPE, mContext.getResources().getString(R.string.circle)).equals(mContext.getResources().getString(R.string.oval))) {
            rb_oval.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_SHAPE, mContext.getResources().getString(R.string.circle)).equals(mContext.getResources().getString(R.string.star))) {
            rb_star.setChecked(true);
        }

    }

    private void registerListener() {
        rg_marker_shape.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_circle) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_SHAPE,mContext.getResources().getString(R.string.circle));
                }
                if (checkedId == R.id.rb_rectangle) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_SHAPE,mContext.getResources().getString(R.string.rectangle));

                }
                if (checkedId == R.id.rb_pentagon) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_SHAPE,mContext.getResources().getString(R.string.pentagon));

                }
                if (checkedId == R.id.rb_oval) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_SHAPE,mContext.getResources().getString(R.string.oval));

                }
                if (checkedId == R.id.rb_star) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_SHAPE,mContext.getResources().getString(R.string.star));

                }
            }
        });
    }
}