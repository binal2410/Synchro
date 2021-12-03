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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.synchro.activity.ZeroingSettingActivity;
import com.synchro.ble.BleCharacteristic;
import com.synchro.ble.CalculatePosition;
import com.synchro.ble.CalculateSpeedOfSound;
import com.synchro.model.DotsModel;
import com.synchro.model.PersonalInfo;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.BleCallback;
import com.synchro.utils.DrawableDotImageViewState;
import com.synchro.utils.DrawableDotImageView;
import com.synchro.utils.DrawableDotImageViewZeroing;
import com.synchro.utils.OnSwipeTouchListener;
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

import static com.synchro.activity.GlobalApplication.context;
import static com.synchro.activity.GlobalApplication.country;
import static com.synchro.activity.GlobalApplication.getMetrologyData;
import static com.synchro.activity.GlobalApplication.last_metrology_time;
import static com.synchro.activity.GlobalApplication.mLocation;
import static com.synchro.activity.GlobalApplication.speedOfSound;
import static com.synchro.activity.GlobalApplication.sunrise;
import static com.synchro.activity.GlobalApplication.sunset;
import static com.synchro.activity.MainActivity.bleDeviceActor;
import static com.synchro.activity.MainActivity.isStartZeroing;
import static com.synchro.utils.AppConstants.Image_pixels_X;
import static com.synchro.utils.AppConstants.Image_pixels_Y;

public class ZeroingFragment extends Fragment implements View.OnClickListener, BleCallback {
    private static Context zeroingContext;
    private static ImageView imgSettings, imgReport;
    float calculateX = 0;
    float calculateY = 0;
    private TextView tv_start_stop_zeroing, tv_stop_zeroing;
    private DrawableDotImageViewZeroing iv_draw_dots;
    private Spinner spiiner_trainingType;
    private TextView tv_tickx, tv_ticky, tv_goalx, tv_goaly, tv_removeOutlier;
    private LinearLayout ll_main, ll_restart_continue;
    private TextView tv_restart, tv_continue;
    private ArrayList<DotsModel> arrayListDots = new ArrayList<>();
    private DotsModel averagePosition = null;
    private double maxDiatance = 0;
    private int maxDiatanceListPosition = 0;
    private TextView tvHorizontalCm, tvVerticalCm;
    private String[] trainingType = {"Freestyle", "Reaction", "Zeroing", "Tactical", "Challenge"};
    private DrawableDotImageViewState iv_draw_dots_state;
    private double maxDistance = -1;
    private DotsModel max_dotsModel1;
    private DotsModel max_dotsModel2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train_zeroing, container, false);
        zeroingContext = getActivity();
        GlobalApplication.currentTraining = "ZeroingFragment";
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_start_stop_zeroing:
                setStopUi();
                refreshUi();
                startZeroingCommand();
               /* if (tv_start_stop_zeroing.getText().toString().trim().equals(getString(R.string.start))) {
                    setStopUi();
                    refreshUi();
                    startZeroingCommand();
                } else {
                    setStartUi();
                    BleCharacteristic.WriteCharacteristic(getActivity(), AppMethods.convertStringToByte(AppConstants.stop_tarin_req));
                    isStartZeroing = false;
                }*/
                break;

            case R.id.tv_removeOutlier:
                if (arrayListDots.size() > 2) {
                    tv_removeOutlier.setAlpha(1f);
                    getFarestPosition();
                    arrayListDots.remove(maxDiatanceListPosition);
                    SharedPref.setDotsList(getActivity(), SharedPref.DOTS_ZEROING_PREF, arrayListDots);
                    iv_draw_dots.removeDots();
                    if (arrayListDots != null && arrayListDots.size() > 0) {
                        for (int i = 0; i < arrayListDots.size(); i++) {
                            iv_draw_dots.addDots(arrayListDots.get(i).x, arrayListDots.get(i).y);
                        }
                        drawAverageHit();
                    }
                } else {
                    tv_removeOutlier.setAlpha(0.4f);
                }
                break;

            case R.id.tv_restart:
                setStopUi();
                refreshUi();
                startZeroingCommand();
                break;

            case R.id.tv_continue:
                setStopUi();
                startZeroingCommand();
                break;
            case R.id.imgReport:
             //   generateZeroingReport();
                break;
        }
    }

    private void generateZeroingReport() {
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


        for (int i = 0; i < arrayListDots.size(); i++) {
            iv_draw_dots_state.addDots(arrayListDots.get(i).x, arrayListDots.get(i).y, i);
        }
        getLongDistancePoint();

        Paragraph p1 = new Paragraph("TRAINING REPORT");
        p1.setTextAlignment(TextAlignment.CENTER);
        PersonalInfo model = SharedPref.getPersonalInfo(context, SharedPref.PERSONAL_INFO, null);
        Paragraph p2 = new Paragraph();
        if (model != null) {
            p2 = new Paragraph("\n" + model.getFullName() + "\n" + model.getId() + "\n" + model.getDateZeroing() + "\n" + model.getYearZeroing() + "\n" + country
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

        iv_draw_dots_state.buildDrawingCache();
        Bitmap bitmap = iv_draw_dots_state.getDrawingCache();

        ByteArrayOutputStream streamState = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, streamState);
        ImageData imageDataState = ImageDataFactory.create(streamState.toByteArray());
        Image imageState = new Image(imageDataState);
        imageState.setWidth(200);
        imageState.setHeight(150);

        Table table2 = new Table(2);
        table2.setWidthPercent(100);
        Cell cellState = new Cell();
        cellState.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cellState.setMarginTop(50);
        cellState.add(imageState);
        cellState.setBorder(Border.NO_BORDER);

        Paragraph info = new Paragraph("\n" + arrayListDots.size() + " detected hits" + "\n" + "Max dist=" + String.format("%.2f", (maxDistance) * 100) + " cm" + "\n\n");

        Cell cellInfo = new Cell();
        cellInfo.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        cellInfo.setTextAlignment(TextAlignment.CENTER);
        //   cellInfo.setWidth(350);
        cellInfo.add(info);
        cellInfo.setBorder(Border.NO_BORDER);


        Image imageArrowX = null;
        Image imageArrowY = null;
        InputStream inputStreamX;
        InputStream inputStreamY;
        try {
            if (calculateX > 0) {
                inputStreamX = context.getAssets().open("arrow_left.png");

            } else {
                inputStreamX = context.getAssets().open("arrow_right.png");

            }
            if (calculateY > 0) {
                inputStreamY = context.getAssets().open("arrow_bottom.png");
            } else {
                inputStreamY = context.getAssets().open("arrow_top.png");

            }
            // get input stream
            Bitmap bmpX = BitmapFactory.decodeStream(inputStreamX);
            Bitmap mutableBitmapX = bmpX.copy(Bitmap.Config.ARGB_8888, true);

            Paint paint = new Paint();
            ColorFilter filter = new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_IN);
            paint.setColorFilter(filter);

            Canvas canvas = new Canvas(mutableBitmapX);
            canvas.drawBitmap(mutableBitmapX, 0, 0, paint);
            ByteArrayOutputStream streamArrowX = new ByteArrayOutputStream();
            mutableBitmapX.compress(Bitmap.CompressFormat.PNG, 100, streamArrowX);
            ImageData imageDataArrowX = ImageDataFactory.create(streamArrowX.toByteArray());
            imageArrowX = new Image(imageDataArrowX);
            imageArrowX.setWidth(15);
            imageArrowX.setHeight(15);

            Bitmap bmpY = BitmapFactory.decodeStream(inputStreamY);
            Bitmap mutableBitmapY = bmpY.copy(Bitmap.Config.ARGB_8888, true);

            Paint paintY = new Paint();
            ColorFilter filterY = new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_IN);
            paintY.setColorFilter(filterY);

            Canvas canvasY = new Canvas(mutableBitmapY);
            canvasY.drawBitmap(mutableBitmapY, 0, 0, paintY);
            ByteArrayOutputStream streamArrowY = new ByteArrayOutputStream();
            mutableBitmapY.compress(Bitmap.CompressFormat.PNG, 100, streamArrowY);
            ImageData imageDataArrowY = ImageDataFactory.create(streamArrowY.toByteArray());
            imageArrowY = new Image(imageDataArrowY);
            imageArrowY.setWidth(15);
            imageArrowY.setHeight(15);
        } catch (IOException ex) {
        }

        Table tableState = new Table(3);
        tableState.addCell(new Cell().add("")).setTextAlignment(TextAlignment.CENTER);
        tableState.addCell(new Cell().add("X"));
        tableState.addCell(new Cell().add("Y"));
        tableState.addCell(new Cell().add("Goal"));
        tableState.addCell(new Cell().add(tv_goalx.getText().toString().trim()));
        tableState.addCell(new Cell().add(tv_goaly.getText().toString().trim()));
        tableState.addCell(new Cell().add("AVG"));
        tableState.addCell(new Cell().add(String.format("%.2f", (averagePosition.x * 100))));
        tableState.addCell(new Cell().add(String.format("%.2f", (averagePosition.y * 100))));
        tableState.addCell(new Cell().add("Clicks"));

        Table tableTickX = new Table(2);
        tableTickX.addCell(new Cell().add(tv_tickx.getText().toString().trim()).setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.RIGHT));
        tableTickX.addCell(new Cell().add(imageArrowX).setBorder(Border.NO_BORDER));
        tableState.addCell(tableTickX).setHorizontalAlignment(HorizontalAlignment.CENTER);
        Table tableTickY = new Table(2);
        tableTickY.addCell(new Cell().add(tv_ticky.getText().toString().trim()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        tableTickY.addCell(new Cell().add(imageArrowY).setBorder(Border.NO_BORDER));
        tableState.addCell(tableTickY).setHorizontalAlignment(HorizontalAlignment.CENTER);
        tableState.addCell(new Cell().add("Clicks per cm"));
        tableState.addCell(new Cell().add("1"));
        tableState.addCell(new Cell().add("1"));

        cellInfo.add(tableState);

        table2.addCell(cellState);
        table2.addCell(cellInfo);


        String filename = "ZeroingReport_" + System.currentTimeMillis() + ".pdf";
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
                        doc.add(table2);
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
                    doc.add(table2);
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

    private void getLongDistancePoint() {
        maxDistance = -1;
        for (int i = 0; i < arrayListDots.size(); i++) {
            DotsModel dotsModel1 = arrayListDots.get(i);
            for (int j = 0; j < arrayListDots.size(); j++) {
                DotsModel dotsModel2 = arrayListDots.get(j);
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
        averagePosition = new DotsModel(AppMethods.getAverageOfX(arrayListDots), AppMethods.getAverageOfY(arrayListDots));
        // tv_no_hits.setText(arrayList.size()+"");
        //  tv_avg_hit.setText("("+String.format("%.2f", (averagePosition.x*100))+","+String.format("%.2f",(averagePosition.y*100))+") cm");
        //   tv_maxDistance.setText("("+String.format("%.2f", (maxDistance)*100)+") cm");

        iv_draw_dots_state.drawLineMAxDistance(max_dotsModel1, max_dotsModel2, averagePosition, null);

    }

    public double getDistanceBetwwenTwoPoints(DotsModel dotsModel1, DotsModel dotsModel2) {
        double Dist = Math.pow((Math.pow((dotsModel1.x - dotsModel2.x), 2) + Math.pow((dotsModel1.y - dotsModel2.y), 2)), 0.5);
        return Dist;
    }

    @Override
    public void onResume() {
        super.onResume();
        bleDeviceActor.setCallback(this);
    }

    private void initUi(View view) {
        arrayListDots = SharedPref.getDotsList(getActivity(), SharedPref.DOTS_ZEROING_PREF);
        iv_draw_dots = view.findViewById(R.id.iv_draw_dots);
        iv_draw_dots.removeDots();
        tv_start_stop_zeroing = view.findViewById(R.id.tv_start_stop_zeroing);
        tv_stop_zeroing = view.findViewById(R.id.tv_stop_zeroing);
        spiiner_trainingType = view.findViewById(R.id.spiiner_trainingType);
        tv_tickx = view.findViewById(R.id.tv_tickx);
        tv_ticky = view.findViewById(R.id.tv_ticky);
        tv_goalx = view.findViewById(R.id.tv_goalx);
        tv_goaly = view.findViewById(R.id.tv_goaly);
        tv_removeOutlier = view.findViewById(R.id.tv_removeOutlier);
        ll_main = view.findViewById(R.id.ll_main);
        ll_restart_continue = view.findViewById(R.id.ll_restart_continue);
        tv_restart = view.findViewById(R.id.tv_restart);
        tv_continue = view.findViewById(R.id.tv_continue);
        imgSettings = view.findViewById(R.id.imgSettings);
        imgReport = view.findViewById(R.id.imgReport);
        tvHorizontalCm = view.findViewById(R.id.tvHorizontalCm);
        tvVerticalCm = view.findViewById(R.id.tv_no_hits);
        iv_draw_dots_state = view.findViewById(R.id.iv_draw_dots_state);

        AppMethods.setSelectedSwitchingImage(iv_draw_dots, getActivity());
        AppMethods.setSelectedSwitchingImage(iv_draw_dots_state, getActivity());

        ViewTreeObserver vto = iv_draw_dots.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                iv_draw_dots.getViewTreeObserver().removeOnPreDrawListener(this);
//                iv_draw_dots.getLayoutParams().height = (iv_draw_dots.getMeasuredWidth() * 1121) / 1586;
                Image_pixels_X = iv_draw_dots.getMeasuredWidth();
                AppConstants.Image_pixels_Y = iv_draw_dots.getMeasuredHeight();
                iv_draw_dots.requestLayout();
                Log.d("canvas==>","zeroing image");
                DrawableDotImageView.setCanvasWidthHeight(iv_draw_dots.getMeasuredWidth(),iv_draw_dots.getMeasuredHeight());
                iv_draw_dots.removeDots();
                arrayListDots = SharedPref.getDotsList(context, SharedPref.DOTS_ZEROING_PREF);
                if (arrayListDots != null && arrayListDots.size() > 0) {
                    for (int i = 0; i < arrayListDots.size(); i++) {
                        iv_draw_dots.addDots(arrayListDots.get(i).x, arrayListDots.get(i).y);
                    }
                    drawAverageHit();
                }
                return true;
            }
        });


        tv_start_stop_zeroing.setOnClickListener(this::onClick);
        tv_removeOutlier.setOnClickListener(this::onClick);
        tv_continue.setOnClickListener(this::onClick);
        tv_restart.setOnClickListener(this::onClick);
        imgReport.setOnClickListener(this::onClick);

        if (isStartZeroing) {
            setStopUi();
        } else {
            setStartUi();
        }

        String goalx = SharedPref.getValue(getActivity(), SharedPref.GOAL_X_PREF, "0");
        String goaly = SharedPref.getValue(getActivity(), SharedPref.GOAL_Y_PREF, "0");
        if (SharedPref.getValue(zeroingContext, SharedPref.SELECTED_UNIT, zeroingContext.getResources().getString(R.string.metric)).equals(zeroingContext.getResources().getString(R.string.standard))) {
            tvHorizontalCm.setText(zeroingContext.getResources().getString(R.string.horizontal_com_inch));
            tvVerticalCm.setText(zeroingContext.getResources().getString(R.string.vertical_com_inch));

            //  double xValue=(Double.parseDouble(goalx)*0.3937);
            //  double yValue=(Double.parseDouble(goaly)*0.3937);
            tv_goalx.setText(goalx + "");
            tv_goaly.setText(goaly + "");
        } else {
            tv_goalx.setText(goalx + "");
            tv_goaly.setText(goaly + "");
        }


        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, trainingType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiiner_trainingType.setAdapter(aa);

        spiiner_trainingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 && tv_start_stop_zeroing.getVisibility() == View.VISIBLE) {
                    ((MainActivity) getActivity()).addReplacedFragment(new TrainFreeStyleFragment(), false, "TrainFreeStyleFragment");
                } else if (position == 1 && tv_start_stop_zeroing.getVisibility() == View.VISIBLE) {
                    ((MainActivity) getActivity()).addReplacedFragment(new TrainReactionFragment(), false, "ZeroingFragment");
                } else if (position == 0 && ll_restart_continue.getVisibility() == View.VISIBLE) {
                    ((MainActivity) getActivity()).addReplacedFragment(new TrainFreeStyleFragment(), false, "TrainFreeStyleFragment");

                } else if (position == 1 && ll_restart_continue.getVisibility() == View.VISIBLE) {
                    ((MainActivity) getActivity()).addReplacedFragment(new TrainReactionFragment(), false, "ZeroingFragment");

                } else {
                    spiiner_trainingType.setSelection(2);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spiiner_trainingType.setSelection(2);

        ll_main.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeLeft() {
                ((MainActivity) getActivity()).addReplacedFragment(new FreeStyleStatFragment(), true, "ZeroingStatFragment");
            }
        });
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent zeroingIntent = new Intent(getActivity(), ZeroingSettingActivity.class);
                startActivity(zeroingIntent);
            }
        });
        tv_stop_zeroing.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setStartUi();
                BleCharacteristic.WriteCharacteristic(getActivity(), AppMethods.convertStringToByte(AppConstants.stop_tarin_req));
                isStartZeroing = false;
                return false;
            }
        });
    }

    private String getZeroingSettingForWrite() {
        int zeroingSetting = SharedPref.getValue(getActivity(), SharedPref.ZEROING_INDICATIVE_MODE, 1);
        if (zeroingSetting == 1) {
            return AppConstants.getStart_tarin_req_indicative(SharedPref.ZEROING_DETECTION_ZONE, getActivity());
        } else {
            return AppConstants.getstart_tarin_req_darkReportAfterEachHit(SharedPref.ZEROING_DETECTION_ZONE, getActivity());
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
        if (responseString.startsWith(AppConstants.masurement_data)) {
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

        } else if (responseString.contains(AppConstants.cmd_temp_reply)) {
            String[] tempdata = responseString.split("_");
            if (tempdata.length > 1) {
                double temp = Double.parseDouble(tempdata[1].trim());
                speedOfSound = CalculateSpeedOfSound.calculateSpeedOfSoundFromTemperarure(temp);
                AppMethods.writeDataTostrem(zeroingContext, "Speed of sound:" + String.format("%.3f", speedOfSound));
                BleCharacteristic.WriteCharacteristic(getActivity(), AppMethods.convertStringToByte(getZeroingSettingForWrite()));
            }
        }
    }

    @Override
    public void connectClickCallback(boolean isConnectClick) {

    }

    @Override
    public void drawTrainDot(float x, float y) {
        AppMethods.writeDataTostrem(getActivity(), "doit() result: " + x + "," + y);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_draw_dots.addDots(x, y);
            }
        });
        arrayListDots.add(new DotsModel(x, y));
        SharedPref.setDotsList(getActivity(), SharedPref.DOTS_ZEROING_PREF, arrayListDots);
        drawAverageHit();
    }

    @Override
    public void bluetoothCallback(boolean isOn) {

    }

    private void drawAverageHit() {
        if (arrayListDots != null) {
            averagePosition = new DotsModel(AppMethods.getAverageOfX(arrayListDots), AppMethods.getAverageOfY(arrayListDots));
            String goalx = SharedPref.getValue(getActivity(), SharedPref.GOAL_X_PREF, "0");
            String goaly = SharedPref.getValue(getActivity(), SharedPref.GOAL_Y_PREF, "0");
            calculateTicks(Float.parseFloat(goalx), Float.parseFloat(goaly));
        }
    }

    private void calculateTicks(float goalx, float goaly) {
        String calix = SharedPref.getValue(getActivity(), SharedPref.CALI_X_PREF, "1");
        String caliy = SharedPref.getValue(getActivity(), SharedPref.CALI_y_PREF, "1");
        calculateX = (averagePosition.x * 100) - goalx;
        calculateX = calculateX / Float.parseFloat(calix);
        calculateY = (averagePosition.y * 100) - goaly;
        calculateY = calculateY / Float.parseFloat(caliy);

        AppMethods.writeDataTostrem(getActivity(), " Avg position=> x:" + (averagePosition.x * 100) + ",y:" + (averagePosition.y * 100) + "\ncali indication=>x:" + calculateX + ",y:" + calculateY);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_draw_dots.addAverageDot(averagePosition, goalx, goaly);
                tv_tickx.setText(Math.round(Math.abs(calculateX)) + "");
                tv_ticky.setText(Math.round(Math.abs(calculateY)) + "");
                tv_goalx.setText(goalx + "");
                tv_goaly.setText(goaly + "");
                if (calculateX > 0) {
                    tv_tickx.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_left, 0);
                } else {
                    tv_tickx.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right, 0);

                }
                if (calculateY > 0) {
                    tv_ticky.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_bottom, 0);
                } else {
                    tv_ticky.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_top, 0);

                }

                if (arrayListDots.size() > 2) {
                    tv_removeOutlier.setEnabled(true);
                    tv_removeOutlier.setAlpha(1f);
                } else {
                    tv_removeOutlier.setEnabled(false);
                    tv_removeOutlier.setAlpha(0.4f);
                }

                Log.d("set data to ui", "indication");
            }
        });
    }

    private void refreshUi() {
        arrayListDots = new ArrayList<>();
        iv_draw_dots.removeDots();
        tv_removeOutlier.setEnabled(false);
        tv_removeOutlier.setAlpha(0.4f);
        tv_tickx.setText("-");
        tv_ticky.setText("-");
        tv_tickx.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tv_ticky.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        SharedPref.setDotsList(getActivity(), SharedPref.DOTS_ZEROING_PREF, arrayListDots);

        DateFormat dateFormat = new SimpleDateFormat("EEE d MMM HH:mm");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        PersonalInfo model = SharedPref.getPersonalInfo(context, SharedPref.PERSONAL_INFO, null);
        if (model != null) {
            model.setDateZeroing(dateFormat.format(date));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            model.setYearZeroing(calendar.get(Calendar.YEAR) + "");
        } else {
            model = new PersonalInfo();
            model.setFullName("");
            model.setId("");
            model.setUnit("");
            model.setSubUnit("");
            model.setCompany("");
            model.setPlatoon("");
            model.setDateZeroing(dateFormat.format(date));
            model.setYearZeroing(date.getYear() + "");
        }

        SharedPref.setPersonalInfo(context, SharedPref.PERSONAL_INFO, model);
    }

    private void getFarestPosition() {
        for (int i = 0; i < arrayListDots.size(); i++) {
            double distanceFromAvg = Math.pow((averagePosition.x - arrayListDots.get(i).x), 2) + Math.pow((averagePosition.y - arrayListDots.get(i).y), 2);
            if (i == 0) {
                maxDiatance = distanceFromAvg;
                maxDiatanceListPosition = 0;
            } else {
                if (distanceFromAvg > maxDiatance) {
                    maxDiatance = distanceFromAvg;
                    maxDiatanceListPosition = i;
                }
            }
        }
    }

    private void setStartUi() {
        if (arrayListDots != null && arrayListDots.size() > 0) {
            tv_start_stop_zeroing.setVisibility(View.GONE);
            ll_restart_continue.setVisibility(View.VISIBLE);
            tv_stop_zeroing.setVisibility(View.GONE);
            imgReport.setVisibility(View.VISIBLE);
        } else {
            tv_start_stop_zeroing.setVisibility(View.VISIBLE);
            tv_stop_zeroing.setVisibility(View.GONE);
            ll_restart_continue.setVisibility(View.GONE);
        }
       /* tv_start_stop_zeroing.setText(getString(R.string.start));
        tv_start_stop_zeroing.setBackground(getResources().getDrawable(R.drawable.back_connect_btn));
        tv_start_stop_zeroing.setTextColor(getResources().getColor(R.color.white));
        tv_start_stop_zeroing.setVisibility(View.GONE);
        tv_stop_zeroing.setVisibility(View.GONE);*/
    }

    private void setStopUi() {
        tv_start_stop_zeroing.setVisibility(View.GONE);
        tv_stop_zeroing.setVisibility(View.VISIBLE);
        ll_restart_continue.setVisibility(View.GONE);
        imgReport.setVisibility(View.GONE);
        //  tv_start_stop_zeroing.setText(getString(R.string.stop));
        //  tv_start_stop_zeroing.setBackground(getResources().getDrawable(R.drawable.back_disconnect_btn));
        // tv_start_stop_zeroing.setTextColor(getResources().getColor(R.color.connect_btn_color));
    }

    private void startZeroingCommand() {
        if ((System.currentTimeMillis() - last_metrology_time) > (15 * 60 * 1000)) {
            AppMethods.showProgressDialog(getActivity(), getString(R.string.calculating_speed_of_sound));
            if ((last_metrology_time > sunrise) && (last_metrology_time < sunset)) {
                if (mLocation != null) {
                    getMetrologyData();
                } else {
                    BleCharacteristic.WriteCharacteristic(zeroingContext, AppMethods.convertStringToByte(AppConstants.cmd_temp));
                }
            } else {
                BleCharacteristic.WriteCharacteristic(zeroingContext, AppMethods.convertStringToByte(AppConstants.cmd_temp));
            }
        } else {
            BleCharacteristic.WriteCharacteristic(getActivity(), AppMethods.convertStringToByte(getZeroingSettingForWrite()));
        }
        isStartZeroing = true;
    }
}
