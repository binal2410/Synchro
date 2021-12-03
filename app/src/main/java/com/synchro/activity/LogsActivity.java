package com.synchro.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.synchro.R;
import com.synchro.adapter.LogsAdapter;
import com.synchro.model.LogsModel;
import com.synchro.utils.SharedPref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class LogsActivity extends AppCompatActivity {
    private TextView tv_clear;
    private RecyclerView rv_logs;
    private ArrayList<LogsModel> logsArrayList = new ArrayList<>();
    private LogsAdapter logsAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initUi();
    }

    private void initUi() {
        rv_logs = findViewById(R.id.rv_logs);
        tv_clear = findViewById(R.id.tv_clear);
        logsArrayList = SharedPref.getArraylistLogs(LogsActivity.this, SharedPref.LOGS_PREF);
        Collections.sort(logsArrayList, new Comparator<LogsModel>() {
            @Override
            public int compare(LogsModel logsModel, LogsModel t1) {
                return new Date(logsModel.logTime).compareTo(new Date(t1.logTime));
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(LogsActivity.this);
        rv_logs.setLayoutManager(mLayoutManager);
        logsAdapter = new LogsAdapter(LogsActivity.this, logsArrayList);
        rv_logs.setAdapter(logsAdapter);

        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logsArrayList = new ArrayList<>();
                SharedPref.setArraylistLogs(LogsActivity.this, SharedPref.LOGS_PREF, logsArrayList);
                logsAdapter = new LogsAdapter(LogsActivity.this, logsArrayList);
                rv_logs.setAdapter(logsAdapter);
            }
        });
    }

}