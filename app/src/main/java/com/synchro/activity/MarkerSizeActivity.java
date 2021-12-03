package com.synchro.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.synchro.R;
import com.synchro.utils.SharedPref;

public class MarkerSizeActivity extends AppCompatActivity {

    private RadioGroup rg_marker_size;
    private RadioButton rb_twelve, rb_ten, rb_eight, rb_six, rb_four,rb_two;
    private RadioGroup rg_marker_color;
    private RadioButton rb_red, rb_yellow, rb_blue, rb_green, rb_pink;
    private RadioGroup rg_marker_shape;
    private RadioButton rb_circle, rb_rectangle, rb_pentagon, rb_oval, rb_star;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_size);
        mContext = this;
        initUI();
        registerListener();
    }

    private void initUI() {
        rg_marker_size = findViewById(R.id.rg_marker_size);
        rb_twelve = findViewById(R.id.rb_twelve);
        rb_ten = findViewById(R.id.rb_ten);
        rb_eight = findViewById(R.id.rb_eight);
        rb_six = findViewById(R.id.rb_six);
        rb_four = findViewById(R.id.rb_four);
        rb_two = findViewById(R.id.rb_two);

        if (SharedPref.getValue(mContext, SharedPref.MARKER_SIZE, 4) == 12) {
            rb_twelve.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_SIZE, 4) == 10) {
            rb_ten.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_SIZE, 4) == 8) {
            rb_eight.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_SIZE, 4) == 6) {
            rb_six.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_SIZE, 4) == 4) {
            rb_four.setChecked(true);
        }
        if (SharedPref.getValue(mContext, SharedPref.MARKER_SIZE, 4) == 2) {
            rb_two.setChecked(true);
        }
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
        rg_marker_size.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_twelve) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_SIZE, 12);

                }
                if (checkedId == R.id.rb_ten) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_SIZE, 10);

                }
                if (checkedId == R.id.rb_eight) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_SIZE, 8);

                }
                if (checkedId == R.id.rb_six) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_SIZE, 6);

                }
                if (checkedId == R.id.rb_four) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_SIZE, 4);

                }
                if (checkedId == R.id.rb_two) {
                    SharedPref.setValue(mContext, SharedPref.MARKER_SIZE, 2);

                }
            }
        });
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