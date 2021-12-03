package com.synchro.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.synchro.R;
import com.synchro.model.DotsModel;
import com.synchro.model.LogsModel;
import com.synchro.model.TargetSwitchingModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.synchro.utils.AppConstants.storage_directoryname;

public class AppMethods {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static ProgressDialog pDialog;
    public static AlertDialog alertDialog;
    public static FileOutputStream outputStream = null;

    public static double calculateAccuracy(float x, float y) {
        double accuracy = Math.pow((Math.pow(x, 2) + Math.pow(y, 2)), 0.5);
        return accuracy * 100;
    }

    public static double getAverageValue(ArrayList<Double> arrayList) {
        double sum = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            sum = sum + arrayList.get(i);
        }
        return sum / arrayList.size();
    }

    public static float getAverageOfX(ArrayList<DotsModel> arrayList) {
        float sum = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            sum = sum + arrayList.get(i).x;
        }

        return sum / arrayList.size();
    }

    public static float getAverageOfY(ArrayList<DotsModel> arrayList) {
        float sum = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            sum = sum + arrayList.get(i).y;
        }
        return sum / arrayList.size();
    }

    public static byte[] convertStringToByte(String text) {
        try {
            return text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String convertByteToString(byte[] data) {
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void showProgressDialog(Context context, String msg) {
        hideProgressDialog(context);
        try {
            if (context != null) {
                pDialog = new ProgressDialog(context);
                pDialog.setMessage(msg);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressDialog(Context context) {
        try {
            if (context != null && pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setAlertDialog(final Context context, final String msg, String title) {
        hideAlertDialog();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(msg);
                alertDialogBuilder.setTitle(title);
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                            }
                        });

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.setCancelable(true);
                alertDialog.setCanceledOnTouchOutside(false);
            }
        });
    }

    public static void hideAlertDialog() {
        try {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void createFile() {
        try {
            String filePath = Environment.getExternalStorageDirectory().toString();
            File sd = new File(filePath, storage_directoryname);
            if (!sd.exists()) {
                sd.mkdirs();
            }
            if (sd.canWrite()) {
                File bfile = new File(sd, "SynchroLogs.txt");
                AppConstants.createdfilepath = bfile.getPath();
                try {
                    outputStream = new FileOutputStream(bfile, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getMarkerColor(Context context, String color) {
        if (color.equals(context.getResources().getString(R.string.red))) {
            return context.getResources().getColor(R.color.dot_color_red);

        } else if (color.equals(context.getResources().getString(R.string.yellow))) {
            return context.getResources().getColor(R.color.dot_color_yellow);

        } else if (color.equals(context.getResources().getString(R.string.blue))) {
            return context.getResources().getColor(R.color.dot_color_blue);

        } else if (color.equals(context.getResources().getString(R.string.green))) {
            return context.getResources().getColor(R.color.dot_color_green);

        } else if (color.equals(context.getResources().getString(R.string.pink))) {
            return context.getResources().getColor(R.color.dot_color_pink);

        }
        return context.getResources().getColor(R.color.dot_color_red);
    }
    public static int getMarkerShape(Context context, String shape)
    {
        if (shape.equals(context.getResources().getString(R.string.circle))) {
            return 1;

        } else if (shape.equals(context.getResources().getString(R.string.rectangle))) {
            return 2;

        } else if (shape.equals(context.getResources().getString(R.string.pentagon))) {
            return 3;

        } else if (shape.equals(context.getResources().getString(R.string.oval))) {
            return 4;

        } else if (shape.equals(context.getResources().getString(R.string.star))) {
            return 5;

        }
        return 1;
    }


    public static void writeDataTostrem(Context context, String data) {
        if (context != null && !data.equals(AppConstants.coded) && !data.equals(AppConstants.uncoded)) {
            ArrayList<LogsModel> logArrayList = SharedPref.getArraylistLogs(context, SharedPref.LOGS_PREF);
            logArrayList.add(new LogsModel(data, System.currentTimeMillis()));
            SharedPref.setArraylistLogs(context, SharedPref.LOGS_PREF, logArrayList);
        }
        if (outputStream == null) {
            createFile();
        }
        try {
            if (outputStream != null) {
                outputStream.write(convertStringToByte("\n" + getCurrentDate() + " " + data));
                Log.d(AppConstants.TAG, "writeDataTostrem: " + data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentDate() {
        long statictime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        return formatter.format(statictime);
    }

    public static String getLogDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("E, MMM dd, yyyy");
        return formatter.format(time);
    }

    public static String getLogTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
        return formatter.format(time);
    }

    public static String getDateFormateFormLong(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(time);
    }

    public static void setLocale(Activity context) {
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static boolean isDeviceConnected(Context context, String macAddress) {
        BluetoothManager btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = btManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            return false;
        }
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
        if (btManager.getConnectionState(bluetoothDevice, BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    public static synchronized void setSelectedSwitchingImage(ImageView imageView, Context context) {
       /* AppMethods.showProgressDialog(context,context.getString(R.string.loading_image));
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                        TargetSwitchingModel model = SharedPref.getSelectedTarget(context, SharedPref.SELECTED_TARGET_SWITCHING_PREF,null);
                        if (model!=null){
                            String photoPath = Environment.getExternalStorageDirectory() + "/"+AppConstants.storage_directoryname+"/"+model.targetName;
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8;
                            final Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
                            if (bitmap==null){
                                return;
                            }
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmap);
                                    AppMethods.hideProgressDialog(context);
                                }
                            });
//            imageView.setImageDrawable(Drawable.createFromPath(photoPath.toString()));
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();*/

        TargetSwitchingModel model = SharedPref.getSelectedTarget(context, SharedPref.SELECTED_TARGET_SWITCHING_PREF, null);
        if (model != null) {
            String photoPath = Environment.getExternalStorageDirectory() + "/" + AppConstants.storage_directoryname + "/" + model.targetName;
            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
            if (bitmap == null) {
                return;
            }
            imageView.setImageBitmap(bitmap);
        }
    }

}
