package com.synchro.activity;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bugfender.android.BuildConfig;
import com.bugfender.sdk.Bugfender;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.synchro.R;
import com.synchro.ble.BleCharacteristic;
import com.synchro.model.ConnectDeviceModel;
import com.synchro.model.TargetSwitchingModel;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.CheckSelfPermission;
import com.synchro.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import static com.synchro.fragment.TrainFreeStyleFragment.calculateSpeedOFSound;
import static com.synchro.utils.AppConstants.appIdForWebSrevice;
import static com.synchro.utils.AppConstants.storage_directoryname;
import static com.synchro.utils.AppMethods.pDialog;

public class GlobalApplication extends Application {
    public static int isCoded = 0;
    public static boolean isStartAccepted = false;

    public static GoogleApiClient mGoogleApiClient;
    public static LocationManager locationManager;
    public static LocationRequest mLocationRequest;
    public static Location mLocation;
    public static Context context;
    public static String currentTraining = "TrainFreeStyleFragment";
    public static ConnectDeviceModel connectedModel = null;
    public static boolean isMisses = false;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        context = this;
        Bugfender.init(this, "j7MmapgE6AJO5euuldilABA8ew2zhfDS", BuildConfig.DEBUG);
        Bugfender.enableCrashReporting();
//        Bugfender.enableUIEventLogging(this);
        Bugfender.enableLogcatLogging();
    }

    public static void buildGoogleApiClient() {
        removeLocationUpdate();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean checkNetwork = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!checkGPS && !checkNetwork) {
            Toast.makeText(context, R.string.location_service_provider_is_not_available, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("location==>", "buildGoogleApiClient");
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Log.d("location==>", "onConnected");
                            getLatLocation();
                            if (mLocation == null) {
                                startLocationUpdates();
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.d("location==>", "onConnectionSuspended");
                            getLatLocation();
                            if (mLocation == null) {
                                buildGoogleApiClient();
                            }
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.d("location==>", "onConnectionFailed");
                            getLatLocation();
                            if (mLocation == null) {
                                buildGoogleApiClient();
                            }
                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    public static void getLatLocation() {
        Log.d("location==>", "getLatLocation");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            Log.d("location==>", mLocation.getLatitude() + "," + mLocation.getLongitude());
            getMetrologyData();
        }
    }

    public static void getMetrologyData() {
        //indoor vs outdoor setting
        if (SharedPref.getValue(context, SharedPref.INDOOR_OUTDOOR, context.getResources().getString(R.string.outdoor)).equals(context.getResources().getString(R.string.indoor))) {
            checkiSDataorNot();

        } else {

            if (!CheckSelfPermission.isNetworkConnected(context)) {
                Toast.makeText(context, context.getString(R.string.pls_check_your_internetconnection), Toast.LENGTH_SHORT).show();
                checkiSDataorNot();
            } else {
                getMetrologyDataFropApi();
            }
        }

      /*  if (!CheckSelfPermission.isNetworkConnected(context)) {
            Toast.makeText(context, context.getString(R.string.pls_check_your_internetconnection), Toast.LENGTH_SHORT).show();
            checkiSDataorNot();
        }else {
            getMetrologyDataFropApi();
        }*/

    }

    public static void checkiSDataorNot() {
        if (pDialog != null && pDialog.isShowing()) {
            BleCharacteristic.WriteCharacteristic(context, AppMethods.convertStringToByte(AppConstants.cmd_temp));
        }
    }

    public static void startLocationUpdates() {
        Log.d("location==>", "startLocationUpdates");
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(15000)
                .setFastestInterval(15000);
        // Request location updates
        if (CheckSelfPermission.checkLocationPermission(context)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, locationListener);
        }
    }

    public static LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mLocation = location;
                if (last_metrology_time == -1) {
                    getMetrologyData();
                }
            }
        }
    };

    @Override
    public void onTerminate() {
        super.onTerminate();
        removeLocationUpdate();
    }

    public static void removeLocationUpdate() {
        try {
            if (mGoogleApiClient != null && CheckSelfPermission.checkLocationPermission(context)) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static double temp = 340, pressure, humidity, speed, deg, speedOfSound = 0;
    public static String country, name;
    public static long sunrise, sunset, last_metrology_time = -1;

    public static void getMetrologyDataFropApi() {
        String url = AppConstants.getMetroLogyUrl + "lat=" + mLocation.getLatitude() + "&lon=" + mLocation.getLongitude() + appIdForWebSrevice;
        Log.d("location==>", "getMetrologyData url: " + url);
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("location==>response:", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectmain = jsonObject.getJSONObject("main");
                    JSONObject jsonObjectwind = jsonObject.getJSONObject("wind");
                    JSONObject jsonObjectsys = jsonObject.getJSONObject("sys");
                    temp = jsonObjectmain.optDouble("temp");
                    pressure = jsonObjectmain.optDouble("pressure");
                    humidity = jsonObjectmain.optDouble("humidity");
                    speed = jsonObjectwind.optDouble("speed");
                    deg = jsonObjectwind.optDouble("deg");
                    country = jsonObjectsys.optString("country");
                    sunrise = jsonObjectsys.optLong("sunrise");
                    sunset = jsonObjectsys.optLong("sunset");
                    name = jsonObject.optString("name");
                    sunrise = sunrise * 1000;
                    sunset = sunset * 1000;
                    Log.d("location==>response:", "temp:" + temp + ", pressure:" + pressure + ", humidity" + humidity + ", speed:"
                            + speed + ", deg:" + deg + ", country:" + country + ", sunrise:" + sunrise + ", sunset:" + sunset + ", name:" + name + ", last_metrology_time:" + last_metrology_time);
                    calculateSpeedOFSound(context);
                } catch (JSONException e) {
                    e.printStackTrace();
                    checkiSDataorNot();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("location==> error api: ", error.toString());
                checkiSDataorNot();
            }
        });
        queue.add(request);
    }


}
