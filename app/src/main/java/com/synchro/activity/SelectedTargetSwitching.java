package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.synchro.R;
import com.synchro.model.TargetSwitchingModel;
import com.synchro.utils.AppConstants;
import com.synchro.utils.SharedPref;

public class SelectedTargetSwitching extends AppCompatActivity implements View.OnClickListener {
    private EditText et_width, et_height, et_hOffcet, et_vOffcet;
    private TextView tv_title, tv_set;
    private TargetSwitchingModel targetSwitchingModel = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_target_switching);

        initui();
    }

    private void initui() {
        et_width = findViewById(R.id.et_width);
        et_height = findViewById(R.id.et_height);
        et_hOffcet = findViewById(R.id.et_hOffcet);
        et_vOffcet = findViewById(R.id.et_vOffcet);
        tv_title = findViewById(R.id.tv_title);
        tv_set = findViewById(R.id.tv_set);

        tv_set.setOnClickListener(this::onClick);

        if (getIntent().hasExtra("SelectedTarget")) {
            targetSwitchingModel = (TargetSwitchingModel) getIntent().getSerializableExtra("SelectedTarget");
            tv_title.setText(targetSwitchingModel.targetName);
            et_width.setText(targetSwitchingModel.widthInRealLife);
            et_height.setText(targetSwitchingModel.heightInRealLife);
            et_hOffcet.setText(targetSwitchingModel.horizontalOffcet);
            et_vOffcet.setText(targetSwitchingModel.verticalOffcet);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_set:
                String width = et_width.getText().toString().trim();
                String height = et_height.getText().toString().trim();
                String vOffcet = et_vOffcet.getText().toString().trim();
                String hOffcet = et_hOffcet.getText().toString().trim();
                if (width.equals("")) {
                    Toast.makeText(this, getString(R.string.pls_enter_width_reallife), Toast.LENGTH_SHORT).show();
                } else if (height.equals("")) {
                    Toast.makeText(this, getString(R.string.pls_enter_height_reallife), Toast.LENGTH_SHORT).show();
                } else if (hOffcet.equals("")) {
                    Toast.makeText(this, getString(R.string.pls_enter_horizontal_offcet), Toast.LENGTH_SHORT).show();
                } else if (vOffcet.equals("")) {
                    Toast.makeText(this, getString(R.string.pls_enter_vertical_offcet), Toast.LENGTH_SHORT).show();
                } else {
                    targetSwitchingModel.widthInRealLife = width;
                    targetSwitchingModel.heightInRealLife = height;
                    targetSwitchingModel.verticalOffcet = vOffcet;
                    targetSwitchingModel.horizontalOffcet = hOffcet;

                    SharedPref.setSelectedTarget(SelectedTargetSwitching.this, SharedPref.SELECTED_TARGET_SWITCHING_PREF, targetSwitchingModel);
                    AppConstants.setTargetSwitchingSetting(SelectedTargetSwitching.this);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }
}