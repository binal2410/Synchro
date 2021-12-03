package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.synchro.R;
import com.synchro.model.TargetSwitchingModel;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.CheckSelfPermission;
import com.synchro.utils.SharedPref;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import static android.os.Build.VERSION.SDK_INT;
import static com.synchro.activity.GlobalApplication.buildGoogleApiClient;
import static com.synchro.activity.GlobalApplication.context;
import static com.synchro.utils.AppConstants.storage_directoryname;

public class SplashActivity extends AppCompatActivity {
    private boolean isFirstLocationPermission = true;
    private boolean isFirstStoragePermission = true;
    private ArrayList<TargetSwitchingModel> targetsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tv_version = findViewById(R.id.tv_version);
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tv_version.setText("Version: "+pinfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        AppConstants.setTargetSwitchingSetting(SplashActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkselfPermission()) {
            buildGoogleApiClient();
            if (GlobalApplication.isCoded==0){
                AppMethods.writeDataTostrem(SplashActivity.this,AppConstants.uncoded);
            }else {
                AppMethods.writeDataTostrem(SplashActivity.this,AppConstants.coded);
            }
            if(SharedPref.getValue(context,SharedPref.SCORE_VALUE,"").equals(""))
            {
                readTargetListeFromStorage();

            }
            goToNextActivity();
        }
    }
    private boolean checkStoragePermission() {
        boolean isPermissionCheck = false;
        if (isFirstStoragePermission) {
            isFirstStoragePermission = false;
            if (SDK_INT >= Build.VERSION_CODES.R) {
                requestPermission();
                isPermissionCheck = Environment.isExternalStorageManager();
            } else {
                //below android 11
                return CheckSelfPermission.checkStoragePermission(this);
            }
        } else {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                requestPermission();
                isPermissionCheck = Environment.isExternalStorageManager();
            } else {
                //below android 11
                return CheckSelfPermission.checkStoragePermissionRetional(this);
            }
        }
        return isPermissionCheck;
    }
    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", this.getPackageName())));
                    startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            }
        }
    }

    private boolean checkselfPermission(){
        if (CheckSelfPermission.isBluetoothOn(SplashActivity.this)){
            if (CheckSelfPermission.isLocationOn(SplashActivity.this)){
               if (checkLocationPermission()){
                   return checkStoragePermission();
               }
            }
        }
        return false;
    }

    private boolean checkLocationPermission(){
        if (isFirstLocationPermission){
            isFirstLocationPermission = false;
            return CheckSelfPermission.checkLocationPermission(SplashActivity.this);
        }else {
            return CheckSelfPermission.checkLocationPermissionRetional(SplashActivity.this);
        }
    }
/*    private boolean checkStoragePermission(){
        if (isFirstStoragePermission){
            isFirstStoragePermission = false;
            return CheckSelfPermission.checkStoragePermission(SplashActivity.this);
        }else {
            return CheckSelfPermission.checkStoragePermissionRetional(SplashActivity.this);
        }
    }*/

    private void goToNextActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mIntent);
                finish();
            }
        },1000);
    }
    private void readTargetListeFromStorage() {
        File sdcard = new File(Environment.getExternalStoragePublicDirectory(storage_directoryname).getPath());
        if (sdcard.exists()) {
            File file = new File(sdcard + "/" + "Target_list.txt");
            String myData = "";
            try {
                FileInputStream fis = new FileInputStream(file);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    myData = myData + strLine + "\n";
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("filedata==>", myData);
            addDateToList(myData);
        }
    }

    private void addDateToList(String value) {
        String[] rowdata = value.split("\n");
        if (rowdata != null && rowdata.length > 0) {
            for (int i = 0; i < rowdata.length; i++) {
                String[] columndata = rowdata[i].trim().split("\t");
                if (columndata != null && columndata.length >= 5) {
                    String imageNAme = columndata[0].trim();
                    String realX = columndata[1].trim();
                    String realY = columndata[2].trim();
                    String ofcetX = columndata[3].trim();
                    String ofcetY = columndata[4].trim();
                    String aspectRation = columndata[5].trim();
                    targetsList.add(new TargetSwitchingModel(imageNAme, realX, realY, ofcetX, ofcetY,aspectRation));
                }
            }
        }

        Collections.reverse(targetsList);
        targetsList.get(0).isSelected = true;
        SharedPref.setSelectedTarget(context, SharedPref.SELECTED_TARGET_SWITCHING_PREF, targetsList.get(0));

        loadScoreFile(targetsList.get(0).targetName);

    }
    private void loadScoreFile(String target)
    {
        if(target.equals("Target_1.png"))
        {
            File sdcard = new File(Environment.getExternalStoragePublicDirectory(storage_directoryname).getPath());
            if (sdcard.exists()) {
                File file = new File(sdcard + "/" + "Target_1_Score.txt");
                String myData = "";
                try {
                    FileInputStream fis = new FileInputStream(file);
                    DataInputStream in = new DataInputStream(fis);
                    BufferedReader br =
                            new BufferedReader(new InputStreamReader(in));
                    String strLine;
                    while ((strLine = br.readLine()) != null) {
                        myData = myData + strLine + "\n";
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("filedata==>", myData);
                SharedPref.setValue(context,SharedPref.SCORE_VALUE,myData);
            }
        }
        if(target.equals("Target_2.png"))
        {
            File sdcard = new File(Environment.getExternalStoragePublicDirectory(storage_directoryname).getPath());
            if (sdcard.exists()) {
                File file = new File(sdcard + "/" + "Target_2_Score.txt");
                String myData = "";
                try {
                    FileInputStream fis = new FileInputStream(file);
                    DataInputStream in = new DataInputStream(fis);
                    BufferedReader br =
                            new BufferedReader(new InputStreamReader(in));
                    String strLine;
                    while ((strLine = br.readLine()) != null) {
                        myData = myData + strLine + "\n";
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("filedata==>", myData);
                SharedPref.setValue(context,SharedPref.SCORE_VALUE,myData);
            }
        }
        else
        {
            File sdcard = new File(Environment.getExternalStoragePublicDirectory(storage_directoryname).getPath());
            if (sdcard.exists()) {
                File file = new File(sdcard + "/" + "Target_1_Score.txt");
                String myData = "";
                try {
                    FileInputStream fis = new FileInputStream(file);
                    DataInputStream in = new DataInputStream(fis);
                    BufferedReader br =
                            new BufferedReader(new InputStreamReader(in));
                    String strLine;
                    while ((strLine = br.readLine()) != null) {
                        myData = myData + strLine + "\n";
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("filedata==>", myData);
                SharedPref.setValue(context,SharedPref.SCORE_VALUE,myData);
            }
        }
    }

}