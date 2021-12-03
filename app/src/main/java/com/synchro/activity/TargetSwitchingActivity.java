package com.synchro.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synchro.R;
import com.synchro.adapter.TargetSwitchingAdapter;
import com.synchro.model.TargetSwitchingModel;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.SharedPref;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import static com.synchro.utils.AppConstants.storage_directoryname;

public class TargetSwitchingActivity extends AppCompatActivity implements TargetSwitchingAdapter.TargetSwitchItemClickListener {
    private RecyclerView rv_targetList;
    private TargetSwitchingAdapter targetSwitchingAdapter = null;
    private ArrayList<TargetSwitchingModel> targetsList = new ArrayList<>();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_switching);
        mContext = this;
        initUi();
        readTargetListeFromStorage();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initUi() {
        rv_targetList = findViewById(R.id.rv_targetList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(TargetSwitchingActivity.this);
        rv_targetList.setLayoutManager(mLayoutManager);
        targetSwitchingAdapter = new TargetSwitchingAdapter(TargetSwitchingActivity.this, targetsList, this);
        rv_targetList.setAdapter(targetSwitchingAdapter);
    }

    public void setOnItemSelectListener(int position) {
        Intent mIntent = new Intent(TargetSwitchingActivity.this, SelectedTargetSwitching.class);
        mIntent.putExtra("SelectedTarget", targetsList.get(position));
        startActivityForResult(mIntent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
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
                    targetsList.add(new TargetSwitchingModel(imageNAme, realX, realY, ofcetX, ofcetY, aspectRation));
                }
            }
        }

        Collections.reverse(targetsList);
        setSelectedTarget();
        targetSwitchingAdapter = new TargetSwitchingAdapter(TargetSwitchingActivity.this, targetsList, this);
        rv_targetList.setAdapter(targetSwitchingAdapter);
    }

    private void setSelectedTarget() {
        TargetSwitchingModel targetSwitchingModel = SharedPref.getSelectedTarget(TargetSwitchingActivity.this, SharedPref.SELECTED_TARGET_SWITCHING_PREF, null);
        if (targetSwitchingModel == null) {
            return;
        }

        for (int i = 0; i < targetsList.size(); i++) {
            if (targetsList.get(i).targetName.equals(targetSwitchingModel.targetName)) {
                targetsList.get(i).isSelected = true;
                return;
            }
        }
    }

    @Override
    public void onTargetSelected(int position) {
        for (int i = 0; i < targetsList.size(); i++) {
            targetsList.get(i).isSelected = false;
        }
        targetsList.get(position).isSelected = true;
        SharedPref.setSelectedTarget(mContext, SharedPref.SELECTED_TARGET_SWITCHING_PREF, targetsList.get(position));
        AppConstants.setTargetSwitchingSetting(mContext);
        AppMethods.writeDataTostrem(mContext, "Selected target: " + targetsList.get(position).targetName);
        targetSwitchingAdapter.notifyDataSetChanged();

        loadScoreFile(targetsList.get(position).targetName);
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
                    addDataToScoreArray(myData);
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
                    addDataToScoreArray(myData);
                }
            }
    }

    private void addDataToScoreArray(String value)
    {
        SharedPref.setValue(mContext,SharedPref.SCORE_VALUE,value);

        /*String[] rowdata = value.split("\n");
        ArrayList<Integer>arrayList1=new ArrayList<>();
        ArrayList<Integer>arrayList2=new ArrayList<>();
        ArrayList<Integer>arrayList3=new ArrayList<>();
        ArrayList<Integer>arrayList4=new ArrayList<>();
        ArrayList<Integer>arrayList5=new ArrayList<>();
        ArrayList<Integer>arrayList6=new ArrayList<>();
        ArrayList<Integer>arrayList7=new ArrayList<>();
        if (rowdata != null && rowdata.length > 0) {

            for (int i = 0; i < rowdata.length; i++) {
                String[] columndata = rowdata[i].trim().split("\t");
                Log.e("rowData",rowdata[i]);

              //  Log.e("rowData",rowdata[i]);
                if (columndata != null && columndata.length >= 5) {
                    arrayList1.add(Integer.parseInt(columndata[0]));
                    arrayList2.add(Integer.parseInt(columndata[1]));
                    arrayList3.add(Integer.parseInt(columndata[2]));
                    arrayList4.add(Integer.parseInt(columndata[3]));
                    arrayList5.add(Integer.parseInt(columndata[4]));
                    arrayList6.add(Integer.parseInt(columndata[5]));
                    arrayList7.add(Integer.parseInt(columndata[6]));
                }
            }
        }*/
    }
}