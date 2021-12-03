package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.synchro.R;
import com.synchro.utils.SharedPref;

public class UnitsActivity extends AppCompatActivity {

    private RadioGroup rg_units;
    private RadioButton rb_metric,rb_standard;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units);
        mContext=this;
        initUI();
        registerListener();
    }
    private void initUI()
    {
        rg_units=findViewById(R.id.rg_units);
        rb_metric=findViewById(R.id.rb_metric);
        rb_standard=findViewById(R.id.rb_standard);

        if(SharedPref.getValue(mContext, SharedPref.SELECTED_UNIT, mContext.getResources().getString(R.string.metric)).equals( mContext.getResources().getString(R.string.metric)))
        {
            rb_metric.setChecked(true);
        }
        else
        {
            rb_standard.setChecked(true);

        }
    }
    private void registerListener()
    {
        rg_units.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rb_metric)
                {
                    SharedPref.setValue(mContext, SharedPref.SELECTED_UNIT, mContext.getResources().getString(R.string.metric));

                }
                else
                {
                    SharedPref.setValue(mContext, SharedPref.SELECTED_UNIT,  mContext.getResources().getString(R.string.standard));

                }
            }
        });
    }

}