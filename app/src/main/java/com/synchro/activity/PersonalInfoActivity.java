package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.synchro.R;
import com.synchro.model.PersonalInfo;
import com.synchro.model.TargetSwitchingModel;
import com.synchro.utils.SharedPref;

import java.util.Calendar;

import static com.synchro.activity.GlobalApplication.context;

public class PersonalInfoActivity extends AppCompatActivity {

    private Context mContext;
    private EditText et_fullname,et_id,et_unit,et_sub_unit,et_company,et_platoon;
    private TextView tv_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        mContext=this;
        initUI();
        registerListener();
    }
    private void initUI()
    {
        et_fullname=findViewById(R.id.et_fullname);
        et_id=findViewById(R.id.et_id);
        et_unit=findViewById(R.id.et_unit);
        et_sub_unit=findViewById(R.id.et_sub_unit);
        et_company=findViewById(R.id.et_company);
        et_platoon=findViewById(R.id.et_platoon);
        tv_save=findViewById(R.id.tv_save);

        setPersonalInfo();

    }

    private void setPersonalInfo()
    {
        PersonalInfo model = SharedPref.getPersonalInfo(mContext, SharedPref.PERSONAL_INFO,null);
        if(model!=null)
        {
            et_fullname.setText(model.getFullName());
            et_id.setText(model.getId());
            et_unit.setText(model.getUnit());
            et_sub_unit.setText(model.getSubUnit());
            et_company.setText(model.getCompany());
            et_platoon.setText(model.getPlatoon());
        }
    }

    private void registerListener() {
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid())
                {
                    PersonalInfo personalInfo = SharedPref.getPersonalInfo(context, SharedPref.PERSONAL_INFO, null);
                    if (personalInfo != null) {
                        personalInfo.setFullName(et_fullname.getText().toString().trim());
                        personalInfo.setId(et_id.getText().toString().trim());
                        personalInfo.setUnit(et_unit.getText().toString().trim());
                        personalInfo.setSubUnit(et_sub_unit.getText().toString().trim());
                        personalInfo.setCompany(et_company.getText().toString().trim());
                        personalInfo.setPlatoon(et_platoon.getText().toString().trim());
                    } else {
                        personalInfo=new PersonalInfo();
                        personalInfo.setFullName(et_fullname.getText().toString().trim());
                        personalInfo.setId(et_id.getText().toString().trim());
                        personalInfo.setUnit(et_unit.getText().toString().trim());
                        personalInfo.setSubUnit(et_sub_unit.getText().toString().trim());
                        personalInfo.setCompany(et_company.getText().toString().trim());
                        personalInfo.setPlatoon(et_platoon.getText().toString().trim());
                    }
                    SharedPref.setPersonalInfo(context, SharedPref.PERSONAL_INFO, personalInfo);
                    Toast.makeText(mContext,mContext.getResources().getString(R.string.personal_info_saved_successfully),Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private boolean isValid()
    {
        if(et_fullname.getText().toString().trim().isEmpty())
        {
            Toast.makeText(mContext, getString(R.string.pls_enter_full_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(et_id.getText().toString().trim().isEmpty())
        {
            Toast.makeText(mContext, getString(R.string.pls_enter_id), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(et_unit.getText().toString().trim().isEmpty())
        {
            Toast.makeText(mContext, getString(R.string.pls_enter_unit), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(et_sub_unit.getText().toString().trim().isEmpty())
        {
            Toast.makeText(mContext, getString(R.string.pls_enter_sub_unit), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(et_company.getText().toString().trim().isEmpty())
        {
            Toast.makeText(mContext, getString(R.string.pls_enter_company), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(et_platoon.getText().toString().trim().isEmpty())
        {
            Toast.makeText(mContext, getString(R.string.pls_enter_platoon), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}