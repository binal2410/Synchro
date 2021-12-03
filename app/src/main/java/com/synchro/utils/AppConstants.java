package com.synchro.utils;

import android.content.Context;

import com.synchro.R;
import com.synchro.model.TargetSwitchingModel;

public class AppConstants {
    public static final int REQUEST_LOCATION_PERMISSION = 01;
    public static final int REQUEST_STORAGE_PERMISSION = 02;
    public static final int REQUEST_ENABLE_BLUETOOTH = 03;
    public static final int MY_MARSHMELLO_PERMISSION = 04;
    public static final int REQUEST_ENABLE_LOCATION = 05;
    public static final String TAG = "ble==>";
    public static String createdfilepath = "";

    public static final String UART_SERVICE_UUID = "6E400001-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String WRITE_CHAR_UUID = "6E400002-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String NOTIFY_CHAR_UUID = "6E400003-B5A3-F393-E0A9-E50E24DCCA9E";
    public static final String DESC_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String cmd_Blink_req = "cmd_Blink";
    public static final String Accepted_Blink_res = "accepted_blink"; //Accepted_Blink
    //    public static final String start_tarin_req_indicative_narrow = "cmd_Indicativeshooting_8_";
    //    public static final String start_tarin_req_indicative_wide = "cmd_Indicativeshooting_5_";
    public static final String start_tarin_req_indicative_narrow = "cmd_IndicativeshootingT_8_"; //offline
    public static final String start_tarin_req_indicative_wide = "cmd_IndicativeshootingT_5_"; //offline
    //    public static final String start_tarin_req_darkReportAfterEachHit_narrow = "cmd_DarkZero_8_";
    //    public static final String start_tarin_req_darkReportAfterEachHit_wide = "cmd_DarkZero_5_";
    public static final String start_tarin_req_darkReportAfterEachHit_narrow = "cmd_DarkZeroT_8_"; //offline
    public static final String start_tarin_req_darkReportAfterEachHit_wide = "cmd_DarkZeroT_5_"; //offline
    public static final String start_tarin_res_darkReportAfterEachHit = "accepted_darkzero"; //Accepted_DarkZeroT
    //    public static final String start_tarin_req_darkReportWhenStopped_narrow = "cmd_Darkshooting_8_";
    //    public static final String start_tarin_req_darkReportWhenStopped_wide = "cmd_Darkshooting_5_";
    public static final String start_tarin_req_darkReportWhenStopped_narrow = "cmd_DarkshootingT_8_"; //offline
    public static final String start_tarin_req_darkReportWhenStopped_wide = "cmd_DarkshootingT_5_"; //offline
    public static final String start_tarin_res_darkReportWhenStopped = "accepted_darkshooting"; //Accepted_DarkShootingT
    public static final String start_tarin_res_old = "accepted_indicativeshooting";  //Accepted_IndicativeShooting
    public static final String start_tarin_res = "accepted_indicativeshootingt";  //accepted_indicativeshootingT
    public static final String masurement_data = "transfer_";  //Transfer_
    public static final String stop_tarin_req = "S";
    public static final String stop_tarin_res = "stopped";
    public static final String filter_devicename = "synchrosense"; //Synchrosense
    public static final String filter_devicename_start = "sy_"; //Sy_
    public static final String coded = "Coded=Long";
    public static final String uncoded = "Coded=Short";
    public static final String default_temp_value = "14.5";
    public static final String getMetroLogyUrl = "http://api.openweathermap.org/data/2.5/weather?";
    public static final String appIdForWebSrevice = "&appid=4e65499af8ea8e68afb146dce1fbeb5f";
    public static final String cmd_temp = "cmd_Temperature";
    public static final String cmd_temp_reply = "temperature_";   //Temperature_
    public static final String cmd_batteryLevel = "cmd_BatteryLevel";
    public static final String cmd_batteryLevel_reply = "batterylevel_";   //BatteryLevel_num
    public static final String cmd_softwareversion = "cmd_softwareVersion";
    public static final String cmd_softwareversion_reply = "softwareversion_";     //SoftwareVersion_string
    public static final String cmd_changename = "cmd_ChangeName_";  //cmd_ChangeName_str_
    public static final String cmd_serialnumber = "cmd_serialnumber";
    public static final String cmd_serialnumber_reply = "serialnumber_";    //serialnumber_num1
    public static final String start_reaction_train = "cmd_InstincT_";  //cmd_Instinc_num1_num2_num3_num4_
    public static final String accepted_instinc = "Accepted_Instinc";
    public static final String round_num = "Round_";   //Round_num
    public static final String end_rounds = "End_Rounds";
    public static final String storage_directoryname = "Synchro";

    public static final int preparation_time = 6;
    public static final int no_of_round = 4;
    public static final int no_of_hit = 3;
    public static final int image_dimen_x = 1587; //1586
    public static final int image_dimen_y = 2245; //1121
    public static float Canvas_pixels_X = 0;
    public static float Canvas_pixels_Y = 0;
    public static float Canvas_AR = 0;
    public static float Image_pixels_X = 0;
    public static float Image_pixels_Y = 0;
    public static float image_AR = 0;
    public static float offset_x = 0;
    public static float offset_y = 0;
    public static float real_life_X = (float) 0.42;
    public static float real_life_Y = (float) 0.594;

    public static void setTargetSwitchingSetting(Context context) {
        TargetSwitchingModel targetSwitchingModel = SharedPref.getSelectedTarget(context, SharedPref.SELECTED_TARGET_SWITCHING_PREF, null);
        if (targetSwitchingModel==null){
            return;
        }
        real_life_X = Float.parseFloat(targetSwitchingModel.widthInRealLife)/100;
        real_life_Y = Float.parseFloat(targetSwitchingModel.heightInRealLife)/100;
        offset_x = Float.parseFloat(targetSwitchingModel.horizontalOffcet)/100;
        offset_y = Float.parseFloat(targetSwitchingModel.verticalOffcet)/100;
        image_AR = Float.parseFloat(targetSwitchingModel.imageAspectRatio);
    }

    public static String getStart_tarin_req_indicative(String key, Context context) {
        if (SharedPref.getValue(context, key, context.getString(R.string.narrow_detection_zone)).equals(context.getString(R.string.narrow_detection_zone))) {
            return AppConstants.start_tarin_req_indicative_narrow;
        } else {
            return AppConstants.start_tarin_req_indicative_wide;
        }
    }

    public static String getstart_tarin_req_darkReportWhenStopped(String key, Context context) {
        if (SharedPref.getValue(context, key, context.getString(R.string.narrow_detection_zone)).equals(context.getString(R.string.narrow_detection_zone))) {
            return AppConstants.start_tarin_req_darkReportWhenStopped_narrow;
        } else {
            return AppConstants.start_tarin_req_darkReportWhenStopped_wide;
        }
    }

    public static String getstart_tarin_req_darkReportAfterEachHit(String key, Context context) {
        if (SharedPref.getValue(context, key, context.getString(R.string.narrow_detection_zone)).equals(context.getString(R.string.narrow_detection_zone))) {
            return AppConstants.start_tarin_req_darkReportAfterEachHit_narrow;
        } else {
            return AppConstants.start_tarin_req_darkReportAfterEachHit_wide;
        }
    }

    public static String getInsticEndChar(String key, Context context) {
        if (SharedPref.getValue(context, key, context.getString(R.string.narrow_detection_zone)).equals(context.getString(R.string.narrow_detection_zone))) {
            return "_8";
        } else {
            return "_5";
        }
    }

}
