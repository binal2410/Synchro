package com.synchro.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.BarEntry;
import com.synchro.R;
import com.synchro.activity.GlobalApplication;
import com.synchro.ble.CalculatePosition;
import com.synchro.model.DotsModel;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.BleCallback;
import com.synchro.utils.DrawableDotImageView;
import com.synchro.utils.DrawableDotImageViewState;
import com.synchro.utils.SharedPref;

import java.util.ArrayList;

import static com.synchro.activity.GlobalApplication.context;
import static com.synchro.activity.GlobalApplication.speedOfSound;
import static com.synchro.activity.MainActivity.bleDeviceActor;
import static com.synchro.utils.AppConstants.Image_pixels_X;
import static com.synchro.utils.AppConstants.Image_pixels_Y;

public class FreeStyleStatFragment extends Fragment implements BleCallback {
    private DrawableDotImageViewState iv_draw_dots;
    private ArrayList<DotsModel> arrayList = new ArrayList<>();
    private LinearLayout ll_history;
    private TextView tv_no_hits, tv_avg_hit, tv_maxDistance, tv_goal_positon, tv_distBwAvgToGoal;
    private ImageView iv_back;
    private String from = "";
    private TextView tv_title;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            from = bundle.getString("from");
        }
        View view = inflater.inflate(R.layout.fragment_free_style_stat, container, false);
        if (!from.equals("FreeStyleStatFragment")) {
             view = inflater.inflate(R.layout.fragment_free_style_stat_zeroing, container, false);
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        initUi(view);
        return view;
    }

    private void initUi(View view) {
        iv_draw_dots = view.findViewById(R.id.iv_draw_dots);
        ll_history = view.findViewById(R.id.ll_history);
        tv_no_hits = view.findViewById(R.id.tv_no_hits);
        tv_avg_hit = view.findViewById(R.id.tv_avg_hit);
        tv_maxDistance = view.findViewById(R.id.tv_maxDistance);
        iv_back = view.findViewById(R.id.iv_back);
        tv_title = view.findViewById(R.id.tv_title);

        AppMethods.setSelectedSwitchingImage(iv_draw_dots, getActivity());

        if (!from.equals("FreeStyleStatFragment")) {
            tv_goal_positon = view.findViewById(R.id.tv_goal_positon);
            tv_distBwAvgToGoal = view.findViewById(R.id.tv_distBwAvgToGoal);
        }

        ViewTreeObserver vto = iv_draw_dots.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                iv_draw_dots.getViewTreeObserver().removeOnPreDrawListener(this);
//                iv_draw_dots.getLayoutParams().height = (iv_draw_dots.getMeasuredWidth() * 1121) / 1586;
                Image_pixels_X = iv_draw_dots.getMeasuredWidth();
                AppConstants.Image_pixels_Y = iv_draw_dots.getMeasuredHeight();
                iv_draw_dots.requestLayout();
                Log.d("canvas==>","StateFragment");

                DrawableDotImageView.setCanvasWidthHeight(iv_draw_dots.getMeasuredWidth(),iv_draw_dots.getMeasuredHeight());
                for (int i = 0; i < arrayList.size(); i++) {
                    iv_draw_dots.addDots(arrayList.get(i).x, arrayList.get(i).y, i);
                }
                getLongDistancePoint();

                return true;
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bleDeviceActor.setCallback(this);
        if (from.equals("FreeStyleStatFragment")) {
            arrayList = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_PREF);
        }else {
            arrayList = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_ZEROING_PREF);
            tv_title.setText(getString(R.string.zeroing_stats));
        }
        if (arrayList!=null && arrayList.size()>0){
            ll_history.setVisibility(View.VISIBLE);
        }else {
            ll_history.setVisibility(View.GONE);
        }
    }

    private double maxDistance = -1;
    private DotsModel max_dotsModel1;
    private DotsModel max_dotsModel2;
    private DotsModel averagePosition;

    private void getLongDistancePoint() {
        maxDistance = -1;
        for (int i = 0; i < arrayList.size(); i++) {
            DotsModel dotsModel1 = arrayList.get(i);
            for (int j = 0; j < arrayList.size(); j++) {
                DotsModel dotsModel2 = arrayList.get(j);
                if (maxDistance == -1) {
                    maxDistance = getDistanceBetwwenTwoPoints(dotsModel1, dotsModel2);
                    max_dotsModel1 = dotsModel1;
                    max_dotsModel2 = dotsModel2;
                } else {
                    double newDist = getDistanceBetwwenTwoPoints(dotsModel1, dotsModel2);
                    if (maxDistance < newDist) {
                        maxDistance = newDist;
                        max_dotsModel1 = dotsModel1;
                        max_dotsModel2 = dotsModel2;
                    }
                }
            }
        }
        averagePosition = new DotsModel(AppMethods.getAverageOfX(arrayList), AppMethods.getAverageOfY(arrayList));
        tv_no_hits.setText(arrayList.size()+"");
        if (SharedPref.getValue(context, SharedPref.SELECTED_UNIT, context.getResources().getString(R.string.metric)).equals(context.getResources().getString(R.string.standard))) {
            tv_avg_hit.setText("("+String.format("%.2f", (averagePosition.x*100)*0.3937)+","+String.format("%.2f",(averagePosition.y*100))+") inch");
            tv_maxDistance.setText("("+String.format("%.2f", ((maxDistance)*100)*0.3937)+") inch");
        }
        else
        {
            tv_avg_hit.setText("("+String.format("%.2f", (averagePosition.x*100))+","+String.format("%.2f",(averagePosition.y*100))+") cm");
            tv_maxDistance.setText("("+String.format("%.2f", (maxDistance)*100)+") cm");

        }
        if (!from.equals("FreeStyleStatFragment")) {
            String goalx = SharedPref.getValue(getActivity(), SharedPref.GOAL_X_PREF, "0");
            String goaly = SharedPref.getValue(getActivity(), SharedPref.GOAL_Y_PREF, "0");
            DotsModel goal = new DotsModel(Float.parseFloat(goalx), Float.parseFloat(goaly));
            iv_draw_dots.drawLineMAxDistance(max_dotsModel1, max_dotsModel2, averagePosition, goal);
            if (SharedPref.getValue(context, SharedPref.SELECTED_UNIT, context.getResources().getString(R.string.metric)).equals(context.getResources().getString(R.string.standard)))
            {
                tv_goal_positon.setText("("+goalx+","+goaly+") inch");
                tv_distBwAvgToGoal.setText("("+String.format("%.2f", (getDistanceBetwwenTwoPoints(new DotsModel(Float.parseFloat(goalx), Float.parseFloat(goaly)), averagePosition))*100*0.3937)+") inch");
            }
            else
            {
                tv_goal_positon.setText("("+goalx+","+goaly+") cm");
                tv_distBwAvgToGoal.setText("("+String.format("%.2f", (getDistanceBetwwenTwoPoints(new DotsModel(Float.parseFloat(goalx), Float.parseFloat(goaly)), averagePosition))*100)+") cm");
            }
        }else {
            iv_draw_dots.drawLineMAxDistance(max_dotsModel1, max_dotsModel2, averagePosition, null);
        }
    }

    @Override
    public void scanCallback(String deviceName, String macAddress, String isCoded) {

    }

    @Override
    public void connectionCallback(boolean isConnected, String status) {

    }

    @Override
    public void onCharacteristicChanged(byte[] data) {
        String responseString = AppMethods.convertByteToString(data).toLowerCase();
        if (responseString.startsWith(AppConstants.masurement_data) /*&& GlobalApplication.isStartAccepted*/) {
//            AppMethods.writeDataTostrem(getActivity(), "On characteristic change called in train screen");
            String[] Mdata = responseString.split("_");
            double[] mDataDouble = new double[8];
            for (int i = 1; i < Mdata.length; i++) {
                mDataDouble[i - 1] = Double.parseDouble(Mdata[i].trim());
            }
            mDataDouble[0] = 0;
            try {
                double cValue = speedOfSound;
                new CalculatePosition(this, mDataDouble, cValue, getActivity());
            } catch (Exception e) {
                new CalculatePosition(this, mDataDouble, 340, getActivity());
            }

        }
    }

    @Override
    public void connectClickCallback(boolean isConnectClick) {

    }

    @Override
    public void drawTrainDot(float x, float y) {

        addDots(x,y);
    }

    @Override
    public void bluetoothCallback(boolean isOn) {

    }

    private void addDots(float x, float y) {
        if (from.equals("FreeStyleStatFragment")) {
            ArrayList<DotsModel> arrayList1 = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_PREF);
            arrayList1.add(new DotsModel(x, y));
            SharedPref.setDotsList(getActivity(), SharedPref.DOTS_PREF, arrayList1);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    arrayList.add(new DotsModel(x, y));
                    iv_draw_dots.addDots(x, y, arrayList.size() - 1);
                    AppMethods.writeDataTostrem(getActivity(), "stats add dot to ui: " + x + "," + y);
                    getLongDistancePoint();
                }
            });
        }else {
            ArrayList<DotsModel> arrayList1 = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_ZEROING_PREF);
            arrayList1.add(new DotsModel(x, y));
            SharedPref.setDotsList(getActivity(), SharedPref.DOTS_ZEROING_PREF, arrayList1);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    arrayList.add(new DotsModel(x, y));
                    iv_draw_dots.addDots(x, y, arrayList.size() - 1);
                    AppMethods.writeDataTostrem(getActivity(), "stats add dot to ui: " + x + "," + y);
                    getLongDistancePoint();
                }
            });
        }
    }

    public double getDistanceBetwwenTwoPoints(DotsModel dotsModel1, DotsModel dotsModel2) {
        double Dist = Math.pow((Math.pow((dotsModel1.x - dotsModel2.x), 2) + Math.pow((dotsModel1.y - dotsModel2.y), 2)), 0.5);
        return Dist;
    }
}
