package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.synchro.R;
import com.synchro.utils.SharedPref;

public class IndoorOutdoorActivity extends AppCompatActivity {

    private RadioGroup rg_indoor_outdoor;
    private RadioButton rb_indoor,rb_outdoor;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_outdoor);
        mContext=this;
        initUI();
        registerListener();
    }
    private void initUI()
    {
        rg_indoor_outdoor=findViewById(R.id.rg_indoor_outdoor);
        rb_indoor=findViewById(R.id.rb_indoor);
        rb_outdoor=findViewById(R.id.rb_outdoor);

        if(SharedPref.getValue(mContext, SharedPref.INDOOR_OUTDOOR, mContext.getResources().getString(R.string.outdoor)).equals( mContext.getResources().getString(R.string.outdoor)))
        {
            rb_outdoor.setChecked(true);

        }
        else
        {
            rb_indoor.setChecked(true);

        }
    }
    private void registerListener()
    {
        rg_indoor_outdoor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rb_indoor)
                {
                    SharedPref.setValue(mContext, SharedPref.INDOOR_OUTDOOR, mContext.getResources().getString(R.string.indoor));

                }
                else
                {
                    SharedPref.setValue(mContext, SharedPref.INDOOR_OUTDOOR,  mContext.getResources().getString(R.string.outdoor));

                }
            }
        });
    }
}