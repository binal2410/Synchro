package com.synchro.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.synchro.model.DotsModel;
import com.synchro.model.LogsModel;
import com.synchro.model.PersonalInfo;
import com.synchro.model.ReactionRoundModel;
import com.synchro.model.ScoreModel;
import com.synchro.model.TargetSwitchingModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SharedPref {

    @Nullable
    private static SharedPreferences sharedPreferences = null;

    public final static String LOGS_PREF = "logs_pref";
    public final static String DOTS_PREF = "dots_pref";
    public final static String FREE_STYLE_SETTING_PREF = "FREE_STYLE_SETTING_PREF";
    public final static String PREPARATION_TIME_PREF = "PREPARATION_TIME_PREF";
    public final static String NO_OF_ROUND_PREF = "NO_OF_ROUND_PREF";
    public final static String NO_OF_HIT_PREF = "NO_OF_HIT_PREF";
    public final static String ROUNDS_HITS_LIST_PREF = "ROUNDS_HITS_LIST_PREF";
    public final static String GOAL_X_PREF= "GOAL_X_PREF";
    public final static String GOAL_Y_PREF= "GOAL_Y_PREF";
    public final static String CALI_X_PREF= "CALI_X_PREF";
    public final static String CALI_y_PREF= "CALI_y_PREF";
    public final static String ZEROING_INDICATIVE_MODE= "ZEROING_INDICATIVE_MODE";
    public final static String DOTS_ZEROING_PREF = "DOTS_ZEROING_PREF";
    public final static String TIME_PENALTY_PREF = "TIME_PENALTY_PREF";
    public final static String CONNECTED_MAC_ADDRESS_PREF = "CONNECTED_MAC_ADDRESS_PREF";
    public final static String SELECTED_TARGET_SWITCHING_PREF = "SELECTED_TARGET_SWITCHING_PREF";
    public final static String FREESTYLE_DETECTION_ZONE = "FREESTYLE_DETECTION_ZONE";
    public final static String RECTION_DETECTION_ZONE = "RECTION_DETECTION_ZONE";
    public final static String ZEROING_DETECTION_ZONE = "ZEROING_DETECTION_ZONE";
    public final static String FREE_STYLE_VELOCITY_ENABLE_DISABLE = "FREE_STYLE_VELOCITY_ENABLE_DISABLE";
    public final static String VELOCITY = "VELOCITY";
    public final static String SELECTED_UNIT = "SELECTED_UNIT";
    public final static String INDOOR_OUTDOOR = "INDOOR_OUTDOOR";
    public final static String PERSONAL_INFO = "PERSONAL_INFO";
    public final static String SCORE_VALUE = "SCORE_VALUE";
    public final static String MARKER_SIZE = "MARKER_SIZE";
    public final static String MARKER_COLOR = "MARKER_COLOR";
    public final static String MARKER_SHAPE = "MARKER_SHAPE";



    public static void openPref(@NonNull Context context) {

        sharedPreferences = context.getSharedPreferences("synchro_prefs",
                Context.MODE_PRIVATE);
    }

    @Nullable
    public static String getValue(@NonNull Context context, String key, String defaultValue) {
        String result = defaultValue;
        try {
            SharedPref.openPref(context);
            result = SharedPref.sharedPreferences.getString(key, defaultValue);
            SharedPref.sharedPreferences = null;
        } catch (Exception e) {

        }

        return result;
    }

    @Nullable
    public static int getValue(@NonNull Context context, String key, int defaultValue) {
        int result = -1;
        try {
            SharedPref.openPref(context);
            result = SharedPref.sharedPreferences.getInt(key, defaultValue);
            SharedPref.sharedPreferences = null;
        } catch (Exception e) {

        }
        return result;
    }

    @Nullable
    public static Long getValue(@NonNull Context context, String key, Long defaultValue) {
        SharedPref.openPref(context);
        Long result = SharedPref.sharedPreferences.getLong(key, defaultValue);
        SharedPref.sharedPreferences = null;
        return result;
    }

    public static void setValue(@NonNull Context context, String key, String value) {
        try {
            SharedPref.openPref(context);
            Editor prefsPrivateEditor = SharedPref.sharedPreferences.edit();
            prefsPrivateEditor.putString(key, value);
            prefsPrivateEditor.commit();
            prefsPrivateEditor = null;
            SharedPref.sharedPreferences = null;
        } catch (Exception e) {

        }
    }

    public static void setValue(@NonNull Context context, String key, int value) {
        try {
            SharedPref.openPref(context);
            Editor prefsPrivateEditor = SharedPref.sharedPreferences.edit();
            prefsPrivateEditor.putInt(key, value);
            prefsPrivateEditor.commit();
            prefsPrivateEditor = null;
            SharedPref.sharedPreferences = null;
        } catch (Exception e) {

        }
    }

    public static void setValue(@NonNull Context context, String key, Long value) {
        try {
            SharedPref.openPref(context);
            Editor prefsPrivateEditor = SharedPref.sharedPreferences.edit();
            prefsPrivateEditor.putLong(key, value);
            prefsPrivateEditor.commit();
            prefsPrivateEditor = null;
            SharedPref.sharedPreferences = null;
        } catch (Exception e) {
        }
    }

    public static byte[] getSavedByte(Context context, String key) {
        byte[] value = new byte[0];
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences != null) {
                try {
                    value = (byte[]) ObjectSerializer.deserialize(preferences.getString(key, ObjectSerializer.serialize(new byte[0])));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
        return value;
    }

    public static boolean setChangedByte(Context context, String key, byte[] value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            Editor editor = preferences.edit();
            try {
                editor.putString(key, ObjectSerializer.serialize(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return editor.commit();
        }
        return false;
    }

    public static ArrayList<String> getFilePathList(Context context, String key) {
        ArrayList<String> value = new ArrayList<>();
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences != null) {
                try {
                    value = (ArrayList<String>) ObjectSerializer.deserialize(preferences.getString(key, ObjectSerializer.serialize(new ArrayList<String>())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
        return value;
    }

    public static boolean setFilePathList(Context context, String key, ArrayList<String> value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            Editor editor = preferences.edit();
            try {
                editor.putString(key, ObjectSerializer.serialize(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return editor.commit();
        }
        return false;
    }

 public static ArrayList<LogsModel> getArraylistLogs(Context context, String key) {
        ArrayList<LogsModel> value = new ArrayList<LogsModel>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            try {
                value = (ArrayList<LogsModel>) ObjectSerializer.deserialize(preferences.getString(key, ObjectSerializer.serialize(new ArrayList<LogsModel>())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public static boolean setArraylistLogs(Context context, String key, ArrayList<LogsModel> value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            Editor editor = preferences.edit();
            try {
                editor.putString(key, ObjectSerializer.serialize(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return editor.commit();
        }
        return false;
    }

    public static ArrayList<DotsModel> getDotsList(Context context, String key) {
        ArrayList<DotsModel> value = new ArrayList<DotsModel>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            try {
                value = (ArrayList<DotsModel>) ObjectSerializer.deserialize(preferences.getString(key, ObjectSerializer.serialize(new ArrayList<DotsModel>())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public static boolean setDotsList(Context context, String key, ArrayList<DotsModel> value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            Editor editor = preferences.edit();
            try {
                editor.putString(key, ObjectSerializer.serialize(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return editor.commit();
        }
        return false;
    }

    public static ArrayList<ScoreModel> getScoreList(Context context, String key) {
        ArrayList<ScoreModel> value = new ArrayList<ScoreModel>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            try {
                value = (ArrayList<ScoreModel>) ObjectSerializer.deserialize(preferences.getString(key, ObjectSerializer.serialize(new ArrayList<ScoreModel>())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public static boolean setScoreList(Context context, String key, ArrayList<ScoreModel> value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            Editor editor = preferences.edit();
            try {
                editor.putString(key, ObjectSerializer.serialize(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return editor.commit();
        }
        return false;
    }

    public static HashMap<Integer, ReactionRoundModel> getHashMapRounds(Context context, String key) {
        HashMap<Integer, ReactionRoundModel> value = new HashMap<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            try {
                value = (HashMap<Integer, ReactionRoundModel>) ObjectSerializer.deserialize(preferences.getString(key, ObjectSerializer.serialize(new HashMap<Integer, ReactionRoundModel>())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public static boolean setHashMapRounds(Context context, String key, HashMap<Integer, ReactionRoundModel> value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            Editor editor = preferences.edit();
            try {
                editor.putString(key, ObjectSerializer.serialize(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return editor.commit();
        }
        return false;
    }


    public static TargetSwitchingModel getSelectedTarget(Context context, String key, TargetSwitchingModel defaultModel) {
        TargetSwitchingModel value = new TargetSwitchingModel();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            try {
                value = (TargetSwitchingModel) ObjectSerializer.deserialize(preferences.getString(key, ObjectSerializer.serialize(defaultModel)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public static boolean setSelectedTarget(Context context, String key, TargetSwitchingModel value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            Editor editor = preferences.edit();
            try {
                editor.putString(key, ObjectSerializer.serialize(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return editor.commit();
        }
        return false;
    }
    public static PersonalInfo getPersonalInfo(Context context, String key, PersonalInfo defaultModel) {
        PersonalInfo value = new PersonalInfo();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            try {
                value = (PersonalInfo) ObjectSerializer.deserialize(preferences.getString(key, ObjectSerializer.serialize(defaultModel)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
    public static boolean setPersonalInfo(Context context, String key, PersonalInfo value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            Editor editor = preferences.edit();
            try {
                editor.putString(key, ObjectSerializer.serialize(value));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return editor.commit();
        }
        return false;
    }
}
