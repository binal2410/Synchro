package com.synchro.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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
import com.synchro.activity.GlobalApplication;
import com.synchro.activity.MainActivity;
import com.synchro.activity.ReactionSettingActivity;
import com.synchro.ble.BleCharacteristic;
import com.synchro.ble.CalculateBonsai;
import com.synchro.ble.CalculatePosition;
import com.synchro.model.DotsModel;
import com.synchro.model.PersonalInfo;
import com.synchro.model.ReactionRoundModel;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.BleCallback;
import com.synchro.utils.DrawableDotImageView;
import com.synchro.utils.DrawableDotImageViewReaction;
import com.synchro.utils.MyMarkerView;
import com.synchro.utils.SharedPref;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.synchro.activity.GlobalApplication.context;
import static com.synchro.activity.GlobalApplication.country;
import static com.synchro.activity.GlobalApplication.speedOfSound;
import static com.synchro.activity.MainActivity.bleDeviceActor;
import static com.synchro.utils.AppConstants.Image_pixels_X;
import static com.synchro.utils.AppConstants.Image_pixels_Y;

public class TrainReactionFragment extends Fragment implements BleCallback, View.OnClickListener {
    private static TextView tv_start_stop,tv_stop;
    private static DrawableDotImageView iv_draw_dots;
    private static DrawableDotImageViewReaction iv_draw_dots_state;
    private static Context tarinContext;
    private LinearLayout rl_chart;
    private ImageView iv_pre, iv_next;
    private TextView tv_round;
    private BarChart bar_chart_accuracy, bar_chart_reaction;
    private ProgressBar progressBar;
    private Spinner spiiner_trainingType;
    private TextView tv_score;
    private int preparationTime = 2, noOfRound = 1, noOfHits = 1;
    private String[] trainingType = {"Freestyle", "Reaction", "Zeroing", "Tactical", "Challenge"};
    private int currentRound = -1;
    private double num1 = 0;
    private double pre_num1 = 0;
    private HashMap<Integer, ReactionRoundModel> roundHitsHashMap = new HashMap<>();
    private ImageView imgSettings,imgReport;
    private TextView tvAccuracy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train_reaction, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        tarinContext = getActivity();
        GlobalApplication.currentTraining = "TrainReactionFragment";
        initUi(view);
        setStartUi();
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
    }

    private void initUi(View view) {
        iv_draw_dots = view.findViewById(R.id.iv_draw_dots);
        iv_draw_dots.removeDots();
        iv_draw_dots_state = view.findViewById(R.id.iv_draw_dots_state);
        iv_draw_dots_state.removeDots();
        tv_start_stop = view.findViewById(R.id.tv_start_stop);
        tv_stop = view.findViewById(R.id.tv_stop);
        spiiner_trainingType = view.findViewById(R.id.spiiner_trainingType);
        iv_next = view.findViewById(R.id.iv_next);
        iv_pre = view.findViewById(R.id.iv_pre);
        tv_round = view.findViewById(R.id.tv_round);
        rl_chart = view.findViewById(R.id.rl_chart);
        bar_chart_accuracy = view.findViewById(R.id.bar_chart_accuracy);
        bar_chart_reaction = view.findViewById(R.id.bar_chart_reaction);
        progressBar = view.findViewById(R.id.progressBar);
        tv_score = view.findViewById(R.id.tv_score);
        imgSettings = view.findViewById(R.id.imgSettings);
        imgReport = view.findViewById(R.id.imgReport);
        tvAccuracy = view.findViewById(R.id.tvAccuracy);

        AppMethods.setSelectedSwitchingImage(iv_draw_dots, getActivity());
        AppMethods.setSelectedSwitchingImage(iv_draw_dots_state, getActivity());
        tv_start_stop.setOnClickListener(this::onClick);
        iv_next.setOnClickListener(this::onClick);
        iv_pre.setOnClickListener(this::onClick);
        imgReport.setOnClickListener(this::onClick);

        ViewTreeObserver vto = iv_draw_dots.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                iv_draw_dots.getViewTreeObserver().removeOnPreDrawListener(this);
//                iv_draw_dots.getLayoutParams().height = (iv_draw_dots.getMeasuredWidth() * 1121) / 1586;
                Image_pixels_X = iv_draw_dots.getMeasuredWidth();
                AppConstants.Image_pixels_Y = iv_draw_dots.getMeasuredHeight();
                iv_draw_dots.requestLayout();
                Log.d("canvas==>","reaction image");
                DrawableDotImageView.setCanvasWidthHeight(iv_draw_dots.getMeasuredWidth(),iv_draw_dots.getMeasuredHeight());
                try {
                    roundHitsHashMap = SharedPref.getHashMapRounds(tarinContext, SharedPref.ROUNDS_HITS_LIST_PREF);
                    if (roundHitsHashMap != null && roundHitsHashMap.size() > 0) {
                        noOfHits = roundHitsHashMap.get(1).dotsList.size();
                        setRoundWiseDatatoUi(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, trainingType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiiner_trainingType.setAdapter(aa);

        spiiner_trainingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 && tv_start_stop.getVisibility()==View.VISIBLE) {
                    ((MainActivity) getActivity()).addReplacedFragment(new TrainFreeStyleFragment(), false, "TrainFreeStyleFragment");
                } else if (position == 2 &&  tv_start_stop.getVisibility()==View.VISIBLE) {
                    ((MainActivity) getActivity()).addReplacedFragment(new ZeroingFragment(), false, "ZeroingFragment");
                } else {
                    spiiner_trainingType.setSelection(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spiiner_trainingType.setSelection(1);
        initializeChart(bar_chart_accuracy);
        initializeChart(bar_chart_reaction);

        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reactionIntent = new Intent(getActivity(), ReactionSettingActivity.class);
                startActivity(reactionIntent);
            }
        });
        tv_stop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean isWrite = BleCharacteristic.WriteCharacteristic(tarinContext, AppMethods.convertStringToByte(AppConstants.stop_tarin_req));
                if (!isWrite) {
                    SharedPref.setHashMapRounds(tarinContext, SharedPref.ROUNDS_HITS_LIST_PREF, roundHitsHashMap);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_start_stop.setVisibility(View.VISIBLE);
                            tv_stop.setVisibility(View.GONE);
                            tv_start_stop.setText(getString(R.string.start));
                            progressBar.setVisibility(View.GONE);
                            imgReport.setVisibility(View.VISIBLE);
                        }
                    });
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start_stop:
                imgReport.setVisibility(View.GONE);
                stratReaction();

                /*if (tv_start_stop.getText().toString().trim().equals(getString(R.string.start))) {
                    stratReaction();
                } else {
                    boolean isWrite = BleCharacteristic.WriteCharacteristic(tarinContext, AppMethods.convertStringToByte(AppConstants.stop_tarin_req));
                    if (!isWrite) {
                        SharedPref.setHashMapRounds(tarinContext, SharedPref.ROUNDS_HITS_LIST_PREF, roundHitsHashMap);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_start_stop.setText(getString(R.string.start));
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }*/
                break;

            case R.id.iv_pre:
                if (currentRound == -1) {
                    return;
                }
                if (currentRound <= 1) {
                    setRoundWiseDatatoUi(roundHitsHashMap.size());
                } else {
                    int preRound = currentRound - 1;
                    setRoundWiseDatatoUi(preRound);
                }
                break;

            case R.id.iv_next:
                if (currentRound == -1) {
                    return;
                }
                int nextRound = currentRound + 1;
                if (roundHitsHashMap.size() >= nextRound) {
                    setRoundWiseDatatoUi(nextRound);
                } else {
                    setRoundWiseDatatoUi(1);
                }
                break;
            case R.id.imgReport:
              //  generateReactionReport();
                break;

        }
    }

    private void generateReactionReport()
    {
        AppMethods.showProgressDialog(getActivity(), getString(R.string.pls_wait));

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
            p2 = new Paragraph("\n" + model.getFullName() + "\n" + model.getId() + "\n" + model.getDateReaction() + "\n" + model.getYearReaction() + "\n" + country
                    + "\n" + "Unit: " + model.getUnit() + "\n" + "Sub unit: " + model.getSubUnit() + "\n" + "Company: " + model.getCompany() + "\n"
                    + "Platoon: " + model.getPlatoon());
        }

        try {
            roundHitsHashMap = SharedPref.getHashMapRounds(tarinContext, SharedPref.ROUNDS_HITS_LIST_PREF);
            int curRound=1;
            iv_draw_dots_state.setCurrentRound(curRound);
            addDataToStateImage(curRound);

        } catch (Exception e) {
            e.printStackTrace();
        }
        iv_draw_dots_state.buildDrawingCache();
        Bitmap bmap = iv_draw_dots_state.getDrawingCache();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        ImageData imageData = ImageDataFactory.create(stream.toByteArray());
        Image image = new Image(imageData);
        image.setWidth(200);
        image.setHeight(150);
        Table table1 = new Table(2);
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

        String filename = "ReactionReport_" + System.currentTimeMillis() + ".pdf";
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
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AppMethods.hideProgressDialog(getActivity());

                        }
                    });

                }
                if (outputStream != null) {
                    try {
                        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(new PdfWriter(outputStream));
                        pdfDoc.setDefaultPageSize(PageSize.A4);
                        Document doc = new Document(pdfDoc);
                        doc.add(p1);
                        doc.add(table1);
                        doc.close();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                AppMethods.hideProgressDialog(getActivity());

                            }
                        });
                        return filename;
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                AppMethods.hideProgressDialog(getActivity());

                            }
                        });
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
                    doc.close();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AppMethods.hideProgressDialog(getActivity());

                        }
                    });
                    return filename;
                } catch (IOException e) {
                    e.printStackTrace();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AppMethods.hideProgressDialog(getActivity());

                        }
                    });
                    return "failed";
                }
            }
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                AppMethods.hideProgressDialog(getActivity());

            }
        });
        return "failed";
    }

    private void addDataToStateImage(int curRound)
    {

        if (roundHitsHashMap != null && roundHitsHashMap.size() > 0) {
            if (roundHitsHashMap.containsKey(curRound)) {
                ArrayList<DotsModel> dotsList = roundHitsHashMap.get(curRound).dotsList;
                if (dotsList != null && dotsList.size() > 0) {
                    for (int i = 0; i < dotsList.size(); i++) {
                        iv_draw_dots_state.addDots(dotsList.get(i).x, dotsList.get(i).y);
                    }
                }
                curRound++;
                iv_draw_dots_state.setCurrentRound(curRound);
                addDataToStateImage(curRound);
            }

        }

    }

    private void stratReaction() {
        preparationTime = SharedPref.getValue(tarinContext, SharedPref.PREPARATION_TIME_PREF, AppConstants.preparation_time);
        noOfRound = SharedPref.getValue(tarinContext, SharedPref.NO_OF_ROUND_PREF, AppConstants.no_of_round);
        noOfHits = SharedPref.getValue(tarinContext, SharedPref.NO_OF_HIT_PREF, AppConstants.no_of_hit);

        String startCommand = AppConstants.start_reaction_train + preparationTime + "_" + noOfRound + "_" + noOfHits + AppConstants.getInsticEndChar(SharedPref.RECTION_DETECTION_ZONE, getActivity());
        boolean isWrite = BleCharacteristic.WriteCharacteristic(tarinContext, AppMethods.convertStringToByte(startCommand));
        if (isWrite) {
            setStopUi();
            progressBar.setVisibility(View.VISIBLE);
            roundHitsHashMap = new HashMap<>();
            SharedPref.setHashMapRounds(tarinContext, SharedPref.ROUNDS_HITS_LIST_PREF, roundHitsHashMap);
            num1 = 0;
            pre_num1 = 0;
            currentRound = -1;
            tv_round.setText(getString(R.string.no_round));
            iv_draw_dots.removeDots();
        }
    }

    @Override
    public void scanCallback(String deviceName, String macAddress, String isCoded) {
    }

    @Override
    public void connectionCallback(boolean isConnected, String status) {
        if (isConnected) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stratReaction();
                        }
                    }, 2000);
                }
            });
        }
    }

    @Override
    public void onCharacteristicChanged(byte[] data) {
        String responseString = AppMethods.convertByteToString(data).toLowerCase();
        if (responseString.toLowerCase().contains(AppConstants.accepted_instinc.toLowerCase())) {
            setStopUi();
        } else if (responseString.toLowerCase().contains(AppConstants.round_num.toLowerCase())) {
            String[] roundData = responseString.split("_");
            if (roundData.length > 1) {
                currentRound = Integer.parseInt(roundData[1].trim()) + 1;
                roundHitsHashMap.put(currentRound, new ReactionRoundModel());
            }
        } else if (responseString.toLowerCase().startsWith(AppConstants.masurement_data.toLowerCase())) {
            if (roundHitsHashMap.get(currentRound).dotsList.size() == 0) {
                num1 = 0;
                pre_num1 = 0;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_draw_dots.removeDots();
                        tv_round.setText("Round " + currentRound);
                    }
                });
            }
            calculateDoit(responseString);
        } else if (responseString.toLowerCase().contains(AppConstants.end_rounds.toLowerCase())) {
            SharedPref.setHashMapRounds(tarinContext, SharedPref.ROUNDS_HITS_LIST_PREF, roundHitsHashMap);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_start_stop.setVisibility(View.VISIBLE);
                    tv_stop.setVisibility(View.GONE);
                    tv_start_stop.setText(getString(R.string.start));
                    progressBar.setVisibility(View.GONE);
                    imgReport.setVisibility(View.VISIBLE);
                }
            });
        } else if (responseString.toLowerCase().contains(AppConstants.stop_tarin_res.toLowerCase())) {
            try {
                if (roundHitsHashMap != null && roundHitsHashMap.size() > 0 && roundHitsHashMap.containsKey(currentRound)) {
                    if (roundHitsHashMap.get(currentRound).dotsList == null || roundHitsHashMap.get(currentRound).dotsList.size() == 0) {
                        roundHitsHashMap.remove(currentRound);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SharedPref.setHashMapRounds(tarinContext, SharedPref.ROUNDS_HITS_LIST_PREF, roundHitsHashMap);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_start_stop.setVisibility(View.VISIBLE);
                    tv_stop.setVisibility(View.GONE);
                    tv_start_stop.setText(getString(R.string.start));
                    progressBar.setVisibility(View.GONE);
                   // imgReport.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void connectClickCallback(boolean isConnectClick) {

    }

    @Override
    public void drawTrainDot(float x, float y) {
        addDataToHashMap(x, y);
    }

    @Override
    public void bluetoothCallback(boolean isOn) {

    }

    private void addDataToHashMap(float x, float y) {
        ArrayList<DotsModel> dotsList = roundHitsHashMap.get(currentRound).dotsList;
        ArrayList<Double> accuracyList = roundHitsHashMap.get(currentRound).accuracyList;
        ArrayList<Double> reactionList = roundHitsHashMap.get(currentRound).reactionList;
        ArrayList<Double> scoreList = roundHitsHashMap.get(currentRound).scoreList;

        AppMethods.writeDataTostrem(getActivity(), "CalReactionTime: " + num1 + " - " + pre_num1);
        double reactontime = num1 - pre_num1;
        pre_num1 = num1;
//        double score = (double) (CalculateBonsai.bonsai_per_hit(x, y) - (int) pre_reactionTime);
        double penalty = Double.parseDouble(SharedPref.getValue(getActivity(), SharedPref.TIME_PENALTY_PREF, "1"));
      //  double score = (double) (CalculateBonsai.bonsai_per_hit(x, y) - (penalty) * ((int) reactontime));
        double score = (double) (CalculateBonsai.bonsai_per_hit_new(context,x, y) - (penalty) * ((int) reactontime));
        if (score < 0) {
            score = 0;
        }
        score=score-(penalty*reactontime);
        dotsList.add(new DotsModel(x, y));
        accuracyList.add(AppMethods.calculateAccuracy(x, y));
        scoreList.add(score);
        reactionList.add(reactontime);

        roundHitsHashMap.get(currentRound).accuracyList = accuracyList;
        roundHitsHashMap.get(currentRound).reactionList = reactionList;
        roundHitsHashMap.get(currentRound).scoreList = scoreList;
        AppMethods.writeDataTostrem(getActivity(), "doit() result: " + x + "," + y + "" +
                "\nAccuracy:" + accuracyList.get(accuracyList.size() - 1) +
                "\nReaction time: " + reactontime + ", Score: " + score);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_draw_dots.addDots(x, y);
                tv_score.setText("Score: " + String.format("%.2f", AppMethods.getAverageValue(scoreList) * 10));
                tv_score.setVisibility(View.VISIBLE);
                if (reactionList.size() >= noOfHits) {
                    addDataToChart(accuracyList, bar_chart_accuracy);
                    addDataToChart(reactionList, bar_chart_reaction);
                    rl_chart.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);


                }
                DateFormat dateFormat = new SimpleDateFormat("EEE d MMM HH:mm");
                Date date = new Date();
                PersonalInfo model = SharedPref.getPersonalInfo(context, SharedPref.PERSONAL_INFO, null);
                if (model != null) {
                    model.setDateReaction(dateFormat.format(date));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    model.setYearReaction(calendar.get(Calendar.YEAR) + "");
                } else {
                    model = new PersonalInfo();
                    model.setFullName("");
                    model.setId("");
                    model.setUnit("");
                    model.setSubUnit("");
                    model.setCompany("");
                    model.setPlatoon("");
                    model.setDateReaction(dateFormat.format(date));
                    model.setYearReaction(date.getYear() + "");
                }

                SharedPref.setPersonalInfo(context, SharedPref.PERSONAL_INFO, model);
            }
        });
    }


    private void setRoundWiseDatatoUi(int round) {
        if (roundHitsHashMap.containsKey(round)) {
            if (roundHitsHashMap.get(round).dotsList.size() != noOfHits || roundHitsHashMap.get(round).accuracyList.size() == 0) {
                return;
            }
        } else {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_draw_dots.removeDots();
                currentRound = round;
                tv_round.setText("Round " + currentRound);
                if (roundHitsHashMap.containsKey(currentRound)) {
                    ArrayList<DotsModel> dotsList = roundHitsHashMap.get(currentRound).dotsList;
                    if (dotsList != null && dotsList.size() > 0) {
                        for (int i = 0; i < dotsList.size(); i++) {
                            iv_draw_dots.addDots(dotsList.get(i).x, dotsList.get(i).y);
                        }
                    }
                    tv_score.setText("Score: " + String.format("%.2f", AppMethods.getAverageValue(roundHitsHashMap.get(currentRound).scoreList) * 10));
                    addDataToChart(roundHitsHashMap.get(currentRound).accuracyList, bar_chart_accuracy);
                    addDataToChart(roundHitsHashMap.get(currentRound).reactionList, bar_chart_reaction);
                    tv_score.setVisibility(View.VISIBLE);
                    rl_chart.setVisibility(View.VISIBLE);
                    imgReport.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private synchronized void calculateDoit(String responseString) {
        String[] Mdata = responseString.split("_");
        double[] mDataDouble = new double[8];
        for (int i = 1; i < Mdata.length; i++) {
            mDataDouble[i - 1] = Double.parseDouble(Mdata[i].trim());
        }
        num1 = Double.parseDouble(Mdata[1].trim()) / 1000000;
        mDataDouble[0] = 0;
        try {
            double cValue = speedOfSound;
            new CalculatePosition(this, mDataDouble, cValue, getActivity());
        } catch (Exception e) {
            new CalculatePosition(this, mDataDouble, 340, getActivity());
        }
    }


    private void setStartUi() {
        tv_start_stop.setVisibility(View.VISIBLE);
        tv_start_stop.setText(getString(R.string.start));
        rl_chart.setVisibility(View.INVISIBLE);
        tv_score.setVisibility(View.INVISIBLE);
        tv_stop.setVisibility(View.GONE);
        imgReport.setVisibility(View.GONE);
    }

    private void setStopUi() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // tv_start_stop.setText(getString(R.string.stop));
                tv_start_stop.setVisibility(View.GONE);
                tv_stop.setVisibility(View.VISIBLE);
                rl_chart.setVisibility(View.INVISIBLE);
                tv_score.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initializeChart(BarChart chart) {
        if (SharedPref.getValue(context, SharedPref.SELECTED_UNIT, context.getResources().getString(R.string.metric)).equals(context.getResources().getString(R.string.standard))) {
            tvAccuracy.setText(context.getResources().getString(R.string.accuracy_inch));
        }

        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        MyMarkerView mv = new MyMarkerView(tarinContext, R.layout.custom_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv);

        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);

        chart.getXAxis().setDrawAxisLine(false);
        chart.getXAxis().setDrawLabels(false);
        chart.setVisibleXRangeMaximum(5);

        Typeface tfRegular = Typeface.createFromAsset(getActivity().getAssets(), "open_sans_regular.ttf");
        if (chart.getId() == R.id.bar_chart_reaction) {
            chart.getAxisLeft().setAxisLineColor(getResources().getColor(R.color.reaction_color));
            chart.getAxisLeft().setTextColor(getResources().getColor(R.color.reaction_color));
        } else {
            chart.getAxisLeft().setAxisLineColor(getResources().getColor(R.color.accuracy_color));
            chart.getAxisLeft().setTextColor(getResources().getColor(R.color.accuracy_color));
        }
        chart.getAxisLeft().setTypeface(tfRegular);
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawAxisLine(false);
       /* chart.getAxisLeft().setAxisMaximum(7);
        chart.getAxisLeft().setAxisMinLabels(7);
        chart.getAxisLeft().setAxisMaxLabels(7);*/


       /* String[] rightAxisLabel = new String[]{"0","1","2","3","4"};
        chart.getAxisRight().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return rightAxisLabel[(int)value];
            }
        });*/

        chart.getLegend().setEnabled(false);
    }

    private void addDataToChart(ArrayList<Double> dataList, BarChart chart) {
        ArrayList<BarEntry> values = new ArrayList<>();
       /* for (int i = 0; i < dataList.size(); i++) {
            values.add(new BarEntry(i, (float) dataList.get(i).doubleValue()));
        }*/
        for (int i = 0; i < dataList.size(); i++) {
            if (SharedPref.getValue(context, SharedPref.SELECTED_UNIT, context.getResources().getString(R.string.metric)).equals(context.getResources().getString(R.string.standard))) {
                values.add(new BarEntry(i, (float) (dataList.get(i).doubleValue()*0.3937)));

            }
            else
            {
                values.add(new BarEntry(i, (float) dataList.get(i).doubleValue()));

            }

        }
        BarDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.setData(chart.getData());
            chart.notifyDataSetChanged();
            chart.invalidate();

        } else {
            set1 = new BarDataSet(values, "");
            set1.setDrawIcons(false);
            set1.setDrawValues(false);
            if (chart.getId() == R.id.bar_chart_reaction) {
                set1.setColors(new int[]{getResources().getColor(R.color.reaction_color)});
            } else {
                set1.setColors(new int[]{getResources().getColor(R.color.accuracy_color)});
            }

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            chart.setAutoScaleMinMaxEnabled(true);
            chart.setData(data);
        }
    }

    public boolean isReactionRunning() {
        if (tv_start_stop.getVisibility()==View.GONE) {
            return true;
        } else {
            return false;
        }
    }
}
