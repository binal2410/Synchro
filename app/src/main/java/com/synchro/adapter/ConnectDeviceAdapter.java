package com.synchro.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.synchro.R;
import com.synchro.ble.BleCharacteristic;
import com.synchro.ble.BleDeviceActor;
import com.synchro.model.ConnectDeviceModel;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.BleCallback;
import com.synchro.utils.SharedPref;

import java.util.ArrayList;

import static com.synchro.activity.MainActivity.bleDeviceActor;
import static com.synchro.fragment.ConnectDeviceFragment.connectPosition;

public class ConnectDeviceAdapter extends RecyclerView.Adapter<ConnectDeviceAdapter.MyViewHolder> {
    private ArrayList<ConnectDeviceModel> deviceList;
    private Context context;
    private BleCallback bleCallback;
    public int blinkcount = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_device_name, tv_connect_btn;
        public RelativeLayout rl_main;
        public ImageView iv_write, iv_battery_level;
        public TextView tv_coded,tv_battery_level;
        public RelativeLayout rl_batteryLevel;

        public MyViewHolder(View view) {
            super(view);
            tv_device_name = (TextView) view.findViewById(R.id.tv_device_name);
            tv_connect_btn = (TextView) view.findViewById(R.id.tv_connect_btn);
            rl_main = (RelativeLayout) view.findViewById(R.id.rl_main);
            iv_write = (ImageView) view.findViewById(R.id.iv_write);
            tv_coded = (TextView) view.findViewById(R.id.tv_coded);
            rl_batteryLevel = (RelativeLayout) view.findViewById(R.id.rl_batteryLevel);
            iv_battery_level = view.findViewById(R.id.iv_battery_level);
            tv_battery_level = view.findViewById(R.id.tv_battery_level);
        }
    }

    public ConnectDeviceAdapter(Context context, ArrayList<ConnectDeviceModel> moviesList, BleCallback bleCallback) {
        this.context = context;
        this.deviceList = moviesList;
        this.bleCallback = bleCallback;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_connect_device, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ConnectDeviceModel model = deviceList.get(position);
        holder.tv_device_name.setText(model.deviceName);
        if (model.isCoded.equals("Long")){
            holder.tv_coded.setText("Long Range");
            holder.tv_coded.setVisibility(View.VISIBLE);
        }else {
            holder.tv_coded.setVisibility(View.GONE);
        }

        if (model.isConnected) {
            holder.tv_connect_btn.setText(context.getString(R.string.discconcet));
            holder.tv_connect_btn.setBackground(context.getResources().getDrawable(R.drawable.back_disconnect_btn));
            holder.tv_connect_btn.setTextColor(context.getColor(R.color.connect_btn_color));
            holder.rl_main.setBackground(context.getResources().getDrawable(R.color.connect_btn_color));
            holder.iv_write.setVisibility(View.VISIBLE);
            holder.rl_batteryLevel.setVisibility(View.VISIBLE);
            holder.iv_write.setImageResource(R.drawable.write_white);
            holder.iv_battery_level.setImageResource(R.drawable.battery_level_white);
            if (model.battery_level.equals("N/A")) {
                holder.tv_battery_level.setText(model.battery_level);
            }else {
                holder.tv_battery_level.setText(model.battery_level + "%");
            }
            holder.tv_battery_level.setTextColor(context.getColor(R.color.connect_btn_color));
        } else {
            holder.tv_connect_btn.setText(context.getString(R.string.connect));
            holder.tv_connect_btn.setBackground(context.getResources().getDrawable(R.drawable.back_connect_btn));
            holder.tv_connect_btn.setTextColor(context.getColor(R.color.white));
            holder.rl_main.setBackground(context.getResources().getDrawable(R.color.backgroung_color));
            holder.iv_write.setVisibility(View.GONE);
            holder.rl_batteryLevel.setVisibility(View.GONE);
        }

        if (model.isBlink) {
            blinkcount = 0;
            blinkDevice(holder.tv_connect_btn, holder.rl_main, holder.iv_write, holder.iv_battery_level, holder.tv_battery_level);
            model.isBlink = false;
        }

        holder.tv_connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deviceList.get(position).isConnected) {
                    setConfirmDialog(context, context.getString(R.string.disconnect_confirm), position);
                } else {
                    holder.tv_connect_btn.setEnabled(false);
                    holder.tv_connect_btn.setAlpha(0.5f);
                    connectPosition = position;
                    bleCallback.connectClickCallback(true);
                    bleDeviceActor.disconnectDevice();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AppMethods.showProgressDialog(context, context.getString(R.string.connecting));
                        }
                    }, 500);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SharedPref.setValue(context, SharedPref.CONNECTED_MAC_ADDRESS_PREF, deviceList.get(position).macAddress);
                            bleDeviceActor.connectToDevice();
                            holder.tv_connect_btn.setEnabled(true);
                            holder.tv_connect_btn.setAlpha(1f);
                        }
                    }, 1000);

                    for (ConnectDeviceModel model : deviceList) {
                        if (model.isConnected) {
                            model.isConnected = false;
                            model.isBlink = false;
                            notifyDataSetChanged();
                            return;
                        }
                    }
                }
            }
        });

        holder.iv_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.iv_write.setEnabled(false);
                holder.iv_write.setAlpha(0.5f);
                BleCharacteristic.WriteCharacteristic(context, AppMethods.convertStringToByte(AppConstants.cmd_Blink_req));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.iv_write.setEnabled(true);
                        holder.iv_write.setAlpha(1f);
                    }
                }, 1000);
            }
        });
    }

    public void blinkDevice(TextView tv_connect_btn, RelativeLayout rl_main, ImageView iv_write, ImageView iv_battery_level, TextView tv_battery_level) {
        iv_write.setEnabled(true);
        iv_write.setAlpha(1f);
        tv_connect_btn.setBackground(context.getResources().getDrawable(R.drawable.back_connect_btn));
        tv_connect_btn.setTextColor(context.getColor(R.color.white));
        rl_main.setBackground(context.getResources().getDrawable(R.color.connected_background));
        iv_write.setImageResource(R.drawable.write_blue);
        iv_battery_level.setImageResource(R.drawable.battery_level_blue);
        tv_battery_level.setTextColor(context.getColor(R.color.white));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_connect_btn.setBackground(context.getResources().getDrawable(R.drawable.back_disconnect_btn));
                tv_connect_btn.setTextColor(context.getColor(R.color.connect_btn_color));
                rl_main.setBackground(context.getResources().getDrawable(R.color.connect_btn_color));
                iv_write.setImageResource(R.drawable.write_white);
                iv_battery_level.setImageResource(R.drawable.battery_level_white);
                tv_battery_level.setTextColor(context.getColor(R.color.connect_btn_color));
                blinkcount = blinkcount + 1;
                if (blinkcount == 3) {
                    return;
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            blinkDevice(tv_connect_btn, rl_main, iv_write, iv_battery_level, tv_battery_level);
                        }
                    }, 500);
                }
            }
        }, 500);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public void setConfirmDialog(final Context context, final String msg, int position) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(msg);
                alertDialogBuilder.setPositiveButton(context.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                BleDeviceActor.disconnectDevice();
                                deviceList.get(position).isConnected = false;
                                deviceList.get(position).isBlink = false;
                                notifyDataSetChanged();
                                bleCallback.connectClickCallback(false);
                                arg0.dismiss();
                            }
                        });
                alertDialogBuilder.setNegativeButton(context.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.setCancelable(true);
                alertDialog.setCanceledOnTouchOutside(false);
            }
        });
    }
}
