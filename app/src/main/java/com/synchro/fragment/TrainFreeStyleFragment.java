package com.synchro.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.synchro.R;
import com.synchro.activity.FreeStyleSettingActivity;
import com.synchro.activity.GlobalApplication;
import com.synchro.activity.MainActivity;
import com.synchro.adapter.BonsaiScoreAdapter;
import com.synchro.ble.BleCharacteristic;
import com.synchro.ble.BleDeviceActor;
import com.synchro.ble.CalculateBonsai;
import com.synchro.ble.CalculatePosition;
import com.synchro.ble.CalculateSpeedOfSound;
import com.synchro.model.DotsModel;
import com.synchro.model.PersonalInfo;
import com.synchro.model.ScoreModel;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.BleCallback;
import com.synchro.utils.DrawableDotImageView;
import com.synchro.utils.DrawableDotImageViewState;
import com.synchro.utils.OnSwipeTouchListener;
import com.synchro.utils.SharedPref;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.synchro.activity.GlobalApplication.context;
import static com.synchro.activity.GlobalApplication.country;
import static com.synchro.activity.GlobalApplication.deg;
import static com.synchro.activity.GlobalApplication.getMetrologyData;
import static com.synchro.activity.GlobalApplication.humidity;
import static com.synchro.activity.GlobalApplication.last_metrology_time;
import static com.synchro.activity.GlobalApplication.mLocation;
import static com.synchro.activity.GlobalApplication.pressure;
import static com.synchro.activity.GlobalApplication.speed;
import static com.synchro.activity.GlobalApplication.speedOfSound;
import static com.synchro.activity.GlobalApplication.sunrise;
import static com.synchro.activity.GlobalApplication.sunset;
import static com.synchro.activity.GlobalApplication.temp;
import static com.synchro.activity.MainActivity.bleDeviceActor;
import static com.synchro.activity.MainActivity.isStartTraining;
import static com.synchro.utils.AppConstants.Image_pixels_X;
import static com.synchro.utils.AppConstants.Image_pixels_Y;
import static com.synchro.utils.AppMethods.pDialog;

public class TrainFreeStyleFragment extends Fragment implements BleCallback, View.OnClickListener {
    public static TextView tv_target_velocity;
    private static TextView tv_start_stop_training, tv_stop_training;
    private static DrawableDotImageView iv_draw_dots;
    private static DrawableDotImageViewState iv_draw_dots_state;
    private static LinearLayout ll_restart_continue;
    private static BonsaiScoreAdapter bonsaiScoreAdapter = null;
    private static RecyclerView rv_score;
    private static Context tarinContext;
    private static boolean isContinue = false;
    private static ImageView imgSettings, imgReport;
    private TextView tv_restart;
    private TextView tv_continue;
    private LinearLayout ll_main;
    private RelativeLayout rl_canvas, rl_canvas_state;
    private Spinner spiiner_trainingType;
    private String[] trainingType = {"Freestyle", "Reaction", "Zeroing", "Tactical", "Challenge"};
    private DrawableDotImageView imgState, imgScore;
    private ImageView imgRow;
    private double maxDistance = -1;
    private DotsModel max_dotsModel1;
    private DotsModel max_dotsModel2;
    private DotsModel averagePosition;
    private ProgressBar progressBar;

    private static void startTraining() {
        tv_start_stop_training.setVisibility(View.GONE);
        tv_stop_training.setVisibility(View.VISIBLE);
        imgReport.setVisibility(View.GONE);
        // tv_start_stop_training.setText(tarinContext.getString(R.string.stop_tarining));
        // tv_start_stop_training.setBackground(tarinContext.getResources().getDrawable(R.drawable.back_disconnect_btn));
        // tv_start_stop_training.setTextColor(tarinContext.getResources().getColor(R.color.connect_btn_color));
        BleCharacteristic.WriteCharacteristic(tarinContext, AppMethods.convertStringToByte(getFreeStyleCommandForWrite()));
        MainActivity.isStartTraining = true;
        ll_restart_continue.setVisibility(View.GONE);
        if (!isContinue) {
            SharedPref.setDotsList(tarinContext, SharedPref.DOTS_PREF, new ArrayList<DotsModel>());

            iv_draw_dots.removeDots();
            bonsaiScoreAdapter = new BonsaiScoreAdapter(tarinContext, getDefaultScore());
            rv_score.setAdapter(bonsaiScoreAdapter);
        }
    }

    private static String getFreeStyleCommandForWrite() {
        String freestyleSetting = SharedPref.getValue(tarinContext, SharedPref.FREE_STYLE_SETTING_PREF, "start_tarin_req_indicative");
        if (freestyleSetting.equals("start_tarin_req_indicative")) {
            return AppConstants.getStart_tarin_req_indicative(SharedPref.FREESTYLE_DETECTION_ZONE, tarinContext);
        } else if (freestyleSetting.equals("start_tarin_req_darkReportAfterEachHit")) {
            return AppConstants.getstart_tarin_req_darkReportAfterEachHit(SharedPref.FREESTYLE_DETECTION_ZONE, tarinContext);
        } else {
            return AppConstants.getstart_tarin_req_darkReportWhenStopped(SharedPref.FREESTYLE_DETECTION_ZONE, tarinContext);
        }
    }

    private static ArrayList<ScoreModel> getDefaultScore() {
        ArrayList<ScoreModel> scoreTableList = new ArrayList<>();
        ScoreModel model = new ScoreModel("No of Hits", "Total Score", "Avg Score");
        scoreTableList.add(model);
        ScoreModel model1 = new ScoreModel("0", "0", "0");
        scoreTableList.add(model1);
        return scoreTableList;
    }

    public static void calculateSpeedOFSound(Context context) {
        if (pDialog != null && pDialog.isShowing()) {
            GlobalApplication.speedOfSound = CalculateSpeedOfSound.CalculateSpeedOfSound(temp, humidity, pressure);
            AppMethods.writeDataTostrem(tarinContext, "Metrology data:" + "\ntemp:" + temp + ", pressure:" + pressure + ", humidity" + humidity + ", speed:"
                    + speed + ", deg:" + deg + ", country:" + country + ", sunrise:" + AppMethods.getDateFormateFormLong(sunrise) + ", sunset:" + AppMethods.getDateFormateFormLong(sunset) + ", name:" + GlobalApplication.name);
            AppMethods.writeDataTostrem(tarinContext, "Speed of sound:" + String.format("%.3f", speedOfSound));
            AppMethods.hideProgressDialog(tarinContext);
            ((Activity) tarinContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startTraining();
                }
            });
        } else {
            GlobalApplication.speedOfSound = CalculateSpeedOfSound.CalculateSpeedOfSound(temp, humidity, pressure);
            AppMethods.writeDataTostrem(context, "Metrology data:" + "\ntemp:" + temp + ", pressure:" + pressure + ", humidity" + humidity + ", speed:"
                    + speed + ", deg:" + deg + ", country:" + country + ", sunrise:" + AppMethods.getDateFormateFormLong(sunrise) + ", sunset:" + AppMethods.getDateFormateFormLong(sunset) + ", name:" + GlobalApplication.name);
            AppMethods.writeDataTostrem(context, "Speed of sound:" + String.format("%.3f", speedOfSound));
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train_freestyle, container, false);
        GlobalApplication.currentTraining = "TrainFreeStyleFragment";
        tarinContext = getActivity();
        initUi(view);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        bleDeviceActor.setCallback(this);
        iv_draw_dots.removeDots();

        ArrayList<DotsModel> arrayList = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_PREF);
        if (Image_pixels_X != 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                iv_draw_dots.addDots(arrayList.get(i).x, arrayList.get(i).y);
            }
        }
        calculateScore();
        if (isStartTraining) {
            ll_restart_continue.setVisibility(View.GONE);
            tv_start_stop_training.setVisibility(View.GONE);
            tv_stop_training.setVisibility(View.VISIBLE);
            imgReport.setVisibility(View.GONE);
        } else {
            if (arrayList == null || arrayList.size() == 0) {
                ll_restart_continue.setVisibility(View.GONE);
                tv_start_stop_training.setVisibility(View.VISIBLE);
                tv_stop_training.setVisibility(View.GONE);
                imgReport.setVisibility(View.GONE);
            } else {
                ll_restart_continue.setVisibility(View.VISIBLE);
                tv_start_stop_training.setVisibility(View.GONE);
                tv_stop_training.setVisibility(View.GONE);
                imgReport.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initUi(View view) {
        progressBar=view.findViewById(R.id.progressBar);
        iv_draw_dots = view.findViewById(R.id.iv_draw_dots);
        iv_draw_dots_state = view.findViewById(R.id.iv_draw_dots_state);
        tv_start_stop_training = view.findViewById(R.id.tv_start_stop_training);
        tv_stop_training = view.findViewById(R.id.tv_stop_training);
        rv_score = view.findViewById(R.id.rv_score);
        spiiner_trainingType = view.findViewById(R.id.spiiner_trainingType);
        ll_restart_continue = view.findViewById(R.id.ll_restart_continue);
        tv_restart = view.findViewById(R.id.tv_restart);
        tv_continue = view.findViewById(R.id.tv_continue);
        ll_main = view.findViewById(R.id.ll_main);
//        rl_canvas = view.findViewById(R.id.rl_canvas);
        //   rl_canvas_state = view.findViewById(R.id.rl_canvas_state);
        imgSettings = view.findViewById(R.id.imgSettings);
        imgReport = view.findViewById(R.id.imgReport);
        tv_target_velocity = view.findViewById(R.id.tv_target_velocity);

        tv_start_stop_training.setOnClickListener(this::onClick);
        tv_restart.setOnClickListener(this::onClick);
        tv_continue.setOnClickListener(this::onClick);
        imgReport.setOnClickListener(this::onClick);
        AppMethods.setSelectedSwitchingImage(iv_draw_dots, getActivity());
        AppMethods.setSelectedSwitchingImage(iv_draw_dots_state, getActivity());

        //  ViewTreeObserver vto1 = iv_draw_dots_state.getViewTreeObserver();
      /*  vto1.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                iv_draw_dots_state.getViewTreeObserver().removeOnPreDrawListener(this);
//                iv_draw_dots.getLayoutParams().height = (iv_draw_dots.getMeasuredWidth() * 1121) / 1586;
                Image_pixels_X = iv_draw_dots_state.getMeasuredWidth();
                AppConstants.Image_pixels_Y = iv_draw_dots_state.getMeasuredHeight();
                iv_draw_dots_state.requestLayout();
                return true;
            }
        });*/


        if (isStartTraining) {
            tv_start_stop_training.setVisibility(View.GONE);
            //  tv_start_stop_training.setText(getString(R.string.stop_tarining));
            //   tv_start_stop_training.setBackground(getResources().getDrawable(R.drawable.back_disconnect_btn));
            // tv_start_stop_training.setTextColor(getResources().getColor(R.color.connect_btn_color));
            GlobalApplication.isStartAccepted = true;
            ll_restart_continue.setVisibility(View.GONE);
            // tv_start_stop_training.setVisibility(View.VISIBLE);
            tv_stop_training.setVisibility(View.VISIBLE);
            imgReport.setVisibility(View.GONE);
        } else {
            tv_start_stop_training.setVisibility(View.VISIBLE);
            tv_start_stop_training.setText(getString(R.string.start_tarining));
            tv_start_stop_training.setBackground(getResources().getDrawable(R.drawable.back_connect_btn));
            tv_start_stop_training.setTextColor(getResources().getColor(R.color.white));
            tv_stop_training.setVisibility(View.GONE);
            imgReport.setVisibility(View.VISIBLE);
        }

        ViewTreeObserver vto = iv_draw_dots.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                iv_draw_dots.getViewTreeObserver().removeOnPreDrawListener(this);
//                iv_draw_dots.getLayoutParams().height = (iv_draw_dots.getMeasuredWidth() * 1121) / 1586;

                Image_pixels_X = iv_draw_dots.getMeasuredWidth();
                Image_pixels_Y = iv_draw_dots.getMeasuredHeight();

                iv_draw_dots.requestLayout();
                DrawableDotImageView.setCanvasWidthHeight(iv_draw_dots.getMeasuredWidth(),iv_draw_dots.getMeasuredHeight());
                iv_draw_dots.removeDots();
                ArrayList<DotsModel> arrayList = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_PREF);

                for (int i = 0; i < arrayList.size(); i++) {
                    iv_draw_dots.addDots(arrayList.get(i).x, arrayList.get(i).y);
                }

                return true;
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_score.setLayoutManager(mLayoutManager);

        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, trainingType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiiner_trainingType.setAdapter(aa);

        spiiner_trainingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1 && tv_start_stop_training.getVisibility() == View.VISIBLE) {
                    ((MainActivity) getActivity()).addReplacedFragment(new TrainReactionFragment(), false, "TrainReactionFragment");
                } else if (position == 2 && tv_start_stop_training.getVisibility() == View.VISIBLE) {
                    ((MainActivity) getActivity()).addReplacedFragment(new ZeroingFragment(), false, "ZeroingFragment");
                } else if (position == 1 && ll_restart_continue.getVisibility() == View.VISIBLE) {
                    ((MainActivity) getActivity()).addReplacedFragment(new TrainReactionFragment(), false, "TrainReactionFragment");
                } else if (position == 2 && ll_restart_continue.getVisibility() == View.VISIBLE) {
                    ((MainActivity) getActivity()).addReplacedFragment(new ZeroingFragment(), false, "ZeroingFragment");
                } else {
                    spiiner_trainingType.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ll_main.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeLeft() {
                ((MainActivity) getActivity()).addReplacedFragment(new FreeStyleStatFragment(), true, "FreeStyleStatFragment");
            }
        });

        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent styleIntent = new Intent(getActivity(), FreeStyleSettingActivity.class);
                startActivity(styleIntent);
            }
        });
        if (SharedPref.getValue(getActivity(), SharedPref.FREE_STYLE_VELOCITY_ENABLE_DISABLE, "Off").equals("Off")) {
            tv_target_velocity.setVisibility(View.GONE);
        } else {
            tv_target_velocity.setVisibility(View.VISIBLE);
            tv_target_velocity.setText("Target Velocity: " + SharedPref.getValue(getActivity(), SharedPref.VELOCITY, 0.0 + ""));

        }
        tv_stop_training.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startStopTraining();
                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start_stop_training:
                isContinue = false;
                //  SharedPref.setValue(getActivity(), SharedPref.VELOCITY,0.0+"");
                //tv_target_velocity.setText("Target Velocity: 0.0");
                startStopTraining();
                break;

            case R.id.tv_restart:
                isContinue = false;
                SharedPref.setValue(getActivity(), SharedPref.VELOCITY, 0.0 + "");
                tv_target_velocity.setText("Target Velocity: 0.0");
                //  startStopTraining();
                restartContinueTraining();
                break;

            case R.id.tv_continue:
                isContinue = true;
                // startStopTraining();
                restartContinueTraining();
                break;
            case R.id.imgReport:
               /* getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        generateFreeStyleReport();

                    }
                });*/
                break;
        }
    }

    private void generateFreeStyleReport() {
       // AppMethods.showProgressDialog(getActivity(), getString(R.string.pls_wait));
        iv_draw_dots.buildDrawingCache();
        Bitmap bmap = iv_draw_dots.getDrawingCache();
        View viewFreeStyle = getLayoutInflater().inflate(R.layout.layout_free_style_report, null);
        FrameLayout rlMain = viewFreeStyle.findViewById(R.id.rlMain);
        imgRow = viewFreeStyle.findViewById(R.id.imgRow);
        imgState = viewFreeStyle.findViewById(R.id.imgState);
        imgScore = viewFreeStyle.findViewById(R.id.imgScore);
        AppMethods.setSelectedSwitchingImage(imgScore, getActivity());

        imgRow.setImageBitmap(bmap);

        ArrayList<DotsModel> arrayList = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_PREF);
        ArrayList<Integer> scorelist = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            //  int bosaiScore = CalculateBonsai.bonsai_per_hit(arrayList.get(i).x, arrayList.get(i).y);
            int bosaiScore = CalculateBonsai.bonsai_per_hit_new(context, arrayList.get(i).x, arrayList.get(i).y);
            scorelist.add(bosaiScore);
        }
        int totlascore = BonsaiScoreAdapter.getTotelscore(scorelist);
        double avg = BonsaiScoreAdapter.getAvg(totlascore, scorelist.size());

        for (int i = 0; i < arrayList.size(); i++) {
            iv_draw_dots_state.addDots(arrayList.get(i).x, arrayList.get(i).y, i);
        }
        getLongDistancePoint();
        // iv_draw_dots_state.setDrawingCacheEnabled(true);
        // iv_draw_dots_state.buildDrawingCache();
        //  imgState.setImageBitmap(iv_draw_dots_state.getDrawingCache());
        rlMain.setDrawingCacheEnabled(true);
        Bitmap bm = createBitmapFromView(rlMain);
        //  Bitmap bm = rlMain.getDrawingCache();
/*
        File sdcard = new File(Environment.getExternalStoragePublicDirectory(storage_directoryname).getPath());
        File file = null;
        if (sdcard.exists()) {
            file = new File(sdcard + "/" + "FreeStyleReport_" + System.currentTimeMillis() + ".png");
            try (FileOutputStream out = new FileOutputStream(file)) {
                bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }*/
        String result = "failed";
        try {
            result = generatePDF(context);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (!result.equals("failed")) {
            Toast.makeText(context, "Report saved successfully", Toast.LENGTH_LONG).show();
        }
    }

    public String generatePDF(Context context) throws MalformedURLException {


        Paragraph p1 = new Paragraph("TRAINING REPORT");
        p1.setTextAlignment(TextAlignment.CENTER);
        PersonalInfo model = SharedPref.getPersonalInfo(context, SharedPref.PERSONAL_INFO, null);
        Paragraph p2 = new Paragraph();
        if (model != null) {
            p2 = new Paragraph("\n" + model.getFullName() + "\n" + model.getId() + "\n" + model.getDateFreeStyle() + "\n" + model.getYearFreeStyle() + "\n" + country
                    + "\n" + "Unit: " + model.getUnit() + "\n" + "Sub unit: " + model.getSubUnit() + "\n" + "Company: " + model.getCompany() + "\n"
                    + "Platoon: " + model.getPlatoon());
        }


        iv_draw_dots.buildDrawingCache();
        Bitmap bmap = iv_draw_dots.getDrawingCache();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        ImageData imageData = ImageDataFactory.create(stream.toByteArray());
        Image image = new Image(imageData);
        image.setWidth(200);
        image.setHeight(150);
        Table table1 = new Table(2);
        //  table1.setHeight(300);
        Cell cellOne = new Cell();
        cellOne.setWidth(350);
        cellOne.add(p2);
        cellOne.setBorder(Border.NO_BORDER);
        Cell cellTwo = new Cell();
        cellTwo.setMarginLeft(50);
        cellTwo.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        cellTwo.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cellTwo.setWidth(300);
        cellTwo.add(image);
        cellTwo.setBorder(Border.NO_BORDER);
        table1.addCell(cellOne);
        table1.addCell(cellTwo);

        ByteArrayOutputStream streamState = new ByteArrayOutputStream();
      //  iv_draw_dots_state.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv_draw_dots_state.buildDrawingCache();
        Bitmap bitmapState = iv_draw_dots_state.getDrawingCache();
        bitmapState.compress(Bitmap.CompressFormat.PNG, 100, streamState);
        ImageData imageDataState = ImageDataFactory.create(streamState.toByteArray());
        Image imageState = new Image(imageDataState);
        imageState.setWidth(200);
        imageState.setHeight(170);

        imgScore.buildDrawingCache();
        Bitmap bitmapScoreImage = imgScore.getDrawingCache();
        ByteArrayOutputStream streamScore = new ByteArrayOutputStream();
        bitmapScoreImage.compress(Bitmap.CompressFormat.PNG, 100, streamScore);
        ImageData imageDataScore = ImageDataFactory.create(streamScore.toByteArray());
        Image imageScore = new Image(imageDataScore);
        imageScore.setWidth(340);
        imageScore.setHeight(160);

        Table tableState = new Table(2);
        Cell cellState = new Cell();
        cellState.setMarginTop(25);
        cellState.setWidth(50);
        cellState.add(imageState);
        cellState.setBorder(Border.NO_BORDER);
        Cell cellScoreImage = new Cell();
        cellScoreImage.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        cellScoreImage.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cellState.setMarginTop(25);
        cellScoreImage.setWidth(300);
        cellScoreImage.add(imageScore);
        cellScoreImage.setBorder(Border.NO_BORDER);
       // cellScoreImage.setMarginRight(10);
        cellScoreImage.setMarginLeft(25);
        tableState.addCell(cellState);
        tableState.addCell(cellScoreImage);

        ArrayList<DotsModel> arrayList = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_PREF);
        ArrayList<Integer> scorelist = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            //  int bosaiScore = CalculateBonsai.bonsai_per_hit(arrayList.get(i).x, arrayList.get(i).y);
            int bosaiScore = CalculateBonsai.bonsai_per_hit_new(context, arrayList.get(i).x, arrayList.get(i).y);
            scorelist.add(bosaiScore);
        }
        int totlascore = BonsaiScoreAdapter.getTotelscore(scorelist);
        double avg = BonsaiScoreAdapter.getAvg(totlascore, scorelist.size());


        Paragraph p3 = new Paragraph(arrayList.size() + " detected hits" + "\n" + "AVG=(" + new DecimalFormat("##.##").format(averagePosition.x * 100) + "," + new DecimalFormat("##.##").format(averagePosition.y * 100) + ") cm" + "\n" +
                "Max dist=" + String.format("%.2f", (maxDistance) * 100) + " cm");

        Table tableStatsInfo = new Table(2);
        tableStatsInfo.setMarginTop(25);
        tableStatsInfo.setBorder(Border.NO_BORDER);
        tableStatsInfo.setBorderBottom(Border.NO_BORDER);
        Cell cellStatsInfo = new Cell();
        cellStatsInfo.setWidth(350);
        cellStatsInfo.add(p3);
        cellStatsInfo.setBorder(Border.NO_BORDER);
        cellStatsInfo.setVerticalAlignment(VerticalAlignment.MIDDLE);

        Table tableScore = new Table(3);
        tableScore.addCell(new Cell().add("#")).setTextAlignment(TextAlignment.CENTER);
        tableScore.addCell(new Cell().add("SCORE"));
        tableScore.addCell(new Cell().add("AVG SCORE"));
        tableScore.addCell(new Cell().add(arrayList.size() + ""));
        tableScore.addCell(new Cell().add(totlascore + ""));
        tableScore.addCell(new Cell().add(avg + ""));

        Cell cellStatsTable = new Cell();
        cellStatsTable.setWidth(300);
        cellStatsTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        cellStatsTable.setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(Border.NO_BORDER);
        cellStatsTable.add(tableScore);

        tableStatsInfo.addCell(cellStatsInfo);
        tableStatsInfo.addCell(cellStatsTable);


        String filename = "FreeStyleReport_" + System.currentTimeMillis() + ".pdf";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Synchro");
            Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            Log.e("vvvv", uri.getPath() + "");
            if (uri != null) {
                OutputStream outputStream = null;
                try {
                    outputStream = context.getContentResolver().openOutputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                   /* new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AppMethods.hideProgressDialog(getActivity());

                        }
                    });*/
                    progressBar.setVisibility(View.GONE);


                }
                if (outputStream != null) {
                    try {
                        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(new PdfWriter(outputStream));
                        pdfDoc.setDefaultPageSize(PageSize.A4);
                        Document doc = new Document(pdfDoc);
                        doc.add(p1);
                        doc.add(table1);
                        doc.add(tableState);
                        doc.add(tableStatsInfo);
                        doc.close();
                        /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                AppMethods.hideProgressDialog(getActivity());

                            }
                        });*/
                        progressBar.setVisibility(View.GONE);

                        return filename;
                    } catch (Exception e) {
                        e.printStackTrace();
                        /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                AppMethods.hideProgressDialog(getActivity());

                            }
                        });*/
                        progressBar.setVisibility(View.GONE);

                        return "failed";
                    }
                }
            }
        } else {
            String filePath = Environment.getExternalStorageDirectory().toString();
            File sd = new File(filePath, "/Download/Synchro");
            if (!sd.exists()) {
                sd.mkdirs();
            }
            if (sd.canWrite()) {
                File bfile = new File(sd, filename);
                try {
                    com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(new PdfWriter(new FileOutputStream(bfile)));
                    Document doc = new Document(pdfDoc);
                    doc.add(p1);
                    doc.add(table1);
                    doc.add(tableState);
                    doc.add(tableStatsInfo);
                    doc.close();
                   /* new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AppMethods.hideProgressDialog(getActivity());

                        }
                    });*/
                    progressBar.setVisibility(View.GONE);

                    return filename;
                } catch (IOException e) {
                    e.printStackTrace();
                    /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AppMethods.hideProgressDialog(getActivity());

                        }
                    });*/
                    progressBar.setVisibility(View.GONE);

                    return "failed";
                }
            }
        }
        /*new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                AppMethods.hideProgressDialog(getActivity());

            }
        });*/
       // progressBar.setVisibility(View.GONE);

        return "failed";
    }

    private Bitmap createBitmapFromView(View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (view instanceof FrameLayout) {
            view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        } else {
            view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        }
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;

    }

    private void getLongDistancePoint() {
        maxDistance = -1;
        ArrayList<DotsModel> arrayList = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_PREF);

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
        iv_draw_dots_state.drawLineMAxDistance(max_dotsModel1, max_dotsModel2, averagePosition, null);
    }

    public double getDistanceBetwwenTwoPoints(DotsModel dotsModel1, DotsModel dotsModel2) {
        double Dist = Math.pow((Math.pow((dotsModel1.x - dotsModel2.x), 2) + Math.pow((dotsModel1.y - dotsModel2.y), 2)), 0.5);
        return Dist;
    }

    private void startStopTraining() {
        if (BleDeviceActor.isConnected) {
            if (tv_start_stop_training.getVisibility() == View.VISIBLE) {
                if ((System.currentTimeMillis() - last_metrology_time) > (15 * 60 * 1000)) {
                    AppMethods.showProgressDialog(getActivity(), getString(R.string.calculating_speed_of_sound));
                    if ((last_metrology_time > sunrise) && (last_metrology_time < sunset)) {
                        if (mLocation != null) {
                            getMetrologyData();
                        } else {
                            BleCharacteristic.WriteCharacteristic(tarinContext, AppMethods.convertStringToByte(AppConstants.cmd_temp));
                        }
                    } else {
                        BleCharacteristic.WriteCharacteristic(tarinContext, AppMethods.convertStringToByte(AppConstants.cmd_temp));

                    }
                } else {
                    startTraining();
                }
            } else {
                ll_restart_continue.setVisibility(View.GONE);
                tv_start_stop_training.setVisibility(View.VISIBLE);
                tv_stop_training.setVisibility(View.GONE);
                tv_start_stop_training.setText(getString(R.string.start_tarining));
                tv_start_stop_training.setBackground(getResources().getDrawable(R.drawable.back_connect_btn));
                tv_start_stop_training.setTextColor(getResources().getColor(R.color.white));
                BleCharacteristic.WriteCharacteristic(getActivity(), AppMethods.convertStringToByte(AppConstants.stop_tarin_req));
                MainActivity.isStartTraining = false;
                ArrayList<DotsModel> arrayList = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_PREF);
                String freestyleSetting = SharedPref.getValue(tarinContext, SharedPref.FREE_STYLE_SETTING_PREF, "start_tarin_req_indicative");
                if ((arrayList == null || arrayList.size() == 0) && !freestyleSetting.equals("start_tarin_req_darkReportWhenStopped")) {
                    ll_restart_continue.setVisibility(View.GONE);
                    tv_start_stop_training.setVisibility(View.VISIBLE);
                } else {
                    ll_restart_continue.setVisibility(View.VISIBLE);
                    tv_start_stop_training.setVisibility(View.GONE);
                    imgReport.setVisibility(View.VISIBLE);
                }
            }
        } else {
            AppMethods.setAlertDialog(getActivity(), getString(R.string.device_disconnected), "");
        }
    }

    private void restartContinueTraining() {
        if (BleDeviceActor.isConnected) {
            //  if (tv_start_stop_training.getVisibility()==View.VISIBLE) {
            if ((System.currentTimeMillis() - last_metrology_time) > (15 * 60 * 1000)) {
                AppMethods.showProgressDialog(getActivity(), getString(R.string.calculating_speed_of_sound));
                if ((last_metrology_time > sunrise) && (last_metrology_time < sunset)) {
                    if (mLocation != null) {
                        getMetrologyData();
                    } else {
                        BleCharacteristic.WriteCharacteristic(tarinContext, AppMethods.convertStringToByte(AppConstants.cmd_temp));
                    }
                } else {
                    BleCharacteristic.WriteCharacteristic(tarinContext, AppMethods.convertStringToByte(AppConstants.cmd_temp));

                }
            } else {
                startTraining();
            }
            /*  }*/ /*else {
                ll_restart_continue.setVisibility(View.GONE);
                tv_start_stop_training.setVisibility(View.VISIBLE);
                tv_stop_training.setVisibility(View.GONE);
                tv_start_stop_training.setText(getString(R.string.start_tarining));
                tv_start_stop_training.setBackground(getResources().getDrawable(R.drawable.back_connect_btn));
                tv_start_stop_training.setTextColor(getResources().getColor(R.color.white));
                BleCharacteristic.WriteCharacteristic(getActivity(), AppMethods.convertStringToByte(AppConstants.stop_tarin_req));
                MainActivity.isStartTraining = false;
                ArrayList<DotsModel> arrayList = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_PREF);
                String freestyleSetting = SharedPref.getValue(tarinContext, SharedPref.FREE_STYLE_SETTING_PREF, "start_tarin_req_indicative");
                if ((arrayList == null || arrayList.size() == 0) && !freestyleSetting.equals("start_tarin_req_darkReportWhenStopped")) {
                    ll_restart_continue.setVisibility(View.GONE);
                    tv_start_stop_training.setVisibility(View.VISIBLE);
                } else {
                    ll_restart_continue.setVisibility(View.VISIBLE);
                    tv_start_stop_training.setVisibility(View.GONE);
                }
            }*/
        } else {
            AppMethods.setAlertDialog(getActivity(), getString(R.string.device_disconnected), "");
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
        if (responseString.startsWith(AppConstants.masurement_data) && GlobalApplication.isStartAccepted) {
            AppMethods.writeDataTostrem(getActivity(), "On characteristic change called in train screen");
            String[] Mdata = responseString.split("_");
            double[] mDataDouble = new double[8];
            for (int i = 1; i < Mdata.length; i++) {
                mDataDouble[i - 1] = Double.parseDouble(Mdata[i].trim());
            }
            mDataDouble[0] = 0;
            try {
//                double cValue = Double.parseDouble(SharedPref.getValue(getActivity(), SharedPref.C_PREF, AppConstants.default_temp_value));
//                cValue = 331.3 + (0.6 * cValue);
                double cValue = speedOfSound;
                AppMethods.writeDataTostrem(getActivity(), "CalculatePosition method execute");
                new CalculatePosition(this, mDataDouble, cValue, getActivity());
            } catch (Exception e) {
                AppMethods.writeDataTostrem(getActivity(), "CalculatePosition method execute");
                new CalculatePosition(this, mDataDouble, 340, getActivity());
            }

        } else {
            if (responseString.contains(AppConstants.start_tarin_res) || responseString.contains(AppConstants.start_tarin_res_darkReportWhenStopped) || responseString.contains(AppConstants.start_tarin_res_darkReportAfterEachHit)) {
                GlobalApplication.isStartAccepted = true;
            } else if (responseString.contains(AppConstants.stop_tarin_res)) {
                GlobalApplication.isStartAccepted = false;
              /*  getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().onBackPressed();
                    }
                });*/
            } else if (responseString.contains(AppConstants.cmd_temp_reply)) {
                String[] tempdata = responseString.split("_");
                if (tempdata.length > 1) {
                    double temp = Double.parseDouble(tempdata[1].trim());
                    speedOfSound = CalculateSpeedOfSound.calculateSpeedOfSoundFromTemperarure(temp);
                    AppMethods.writeDataTostrem(tarinContext, "Speed of sound:" + String.format("%.3f", speedOfSound));
                    AppMethods.hideProgressDialog(tarinContext);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startTraining();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void connectClickCallback(boolean isConnectClick) {

    }

    @Override
    public void drawTrainDot(float x, float y) {
        AppMethods.writeDataTostrem(getActivity(), "doit() result: " + x + "," + y);
        addDots(x, y);
    }

    @Override
    public void bluetoothCallback(boolean isOn) {

    }

    private void calculateScore() {
        ArrayList<DotsModel> arrayList = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_PREF);
        ArrayList<Integer> scorelist = new ArrayList<>();
        ArrayList<ScoreModel> scoreTableList = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            //  int bosaiScore = CalculateBonsai.bonsai_per_hit(arrayList.get(i).x, arrayList.get(i).y);
            int bosaiScore = CalculateBonsai.bonsai_per_hit_new(context, arrayList.get(i).x, arrayList.get(i).y);
            scorelist.add(bosaiScore);
        }
        ScoreModel model = new ScoreModel("No of Hits", "Total Score", "Avg Score");
        scoreTableList.add(model);
        int totlascore = BonsaiScoreAdapter.getTotelscore(scorelist);
        double avg = BonsaiScoreAdapter.getAvg(totlascore, scorelist.size());
        ScoreModel model1 = new ScoreModel((scorelist.size()) + "", totlascore + "", avg + "");
        scoreTableList.add(model1);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bonsaiScoreAdapter = new BonsaiScoreAdapter(getActivity(), scoreTableList);
                rv_score.setAdapter(bonsaiScoreAdapter);
            }
        });
    }

    private void addDots(float x, float y) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_draw_dots.addDots(x, y);
                AppMethods.writeDataTostrem(getActivity(), "add dot to ui: " + x + "," + y);
            }
        });
        ArrayList<DotsModel> arrayList = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_PREF);
        arrayList.add(new DotsModel(x, y));
        SharedPref.setDotsList(getActivity(), SharedPref.DOTS_PREF, arrayList);
        DateFormat dateFormat = new SimpleDateFormat("EEE d MMM HH:mm");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        PersonalInfo model = SharedPref.getPersonalInfo(context, SharedPref.PERSONAL_INFO, null);
        if (model != null) {
            model.setDateFreeStyle(dateFormat.format(date));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            model.setYearFreeStyle(calendar.get(Calendar.YEAR) + "");
        } else {
            model = new PersonalInfo();
            model.setFullName("");
            model.setId("");
            model.setUnit("");
            model.setSubUnit("");
            model.setCompany("");
            model.setPlatoon("");
            model.setDateFreeStyle(dateFormat.format(date));
            model.setYearFreeStyle(date.getYear() + "");
        }

        SharedPref.setPersonalInfo(context, SharedPref.PERSONAL_INFO, model);

        calculateScore();
    }
}
