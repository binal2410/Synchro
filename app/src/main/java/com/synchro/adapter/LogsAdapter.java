package com.synchro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.synchro.R;
import com.synchro.model.LogsModel;
import com.synchro.utils.AppMethods;

import java.util.ArrayList;

public class LogsAdapter  extends RecyclerView.Adapter<LogsAdapter.MyViewHolder> {
    private ArrayList<LogsModel> logsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_msg, tv_time, tv_date;
        private LinearLayout ll_item;

        public MyViewHolder(View view) {
            super(view);
            tv_msg = (TextView) view.findViewById(R.id.tv_msg);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            ll_item = (LinearLayout) view.findViewById(R.id.ll_item);
        }
    }

    public LogsAdapter(Context context, ArrayList<LogsModel> moviesList) {
        this.context = context;
        this.logsList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_logs, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LogsModel model = logsList.get(position);
        if (position>0){
            if (AppMethods.getLogDate(logsList.get(position-1).logTime).equals(AppMethods.getLogDate(model.logTime))){
                holder.tv_date.setVisibility(View.GONE);
            }else {
                holder.tv_date.setVisibility(View.VISIBLE);
            }
        }else {
            holder.tv_date.setVisibility(View.VISIBLE);
        }
        holder.tv_date.setText(AppMethods.getLogDate(model.logTime));
        holder.tv_time.setText(AppMethods.getLogTime(model.logTime));
        holder.tv_msg.setText(model.logMsg);
        if (position%2==0){
            holder.ll_item.setBackground(context.getResources().getDrawable(R.color.white));
        }else {
            holder.ll_item.setBackground(context.getResources().getDrawable(R.color.logs_item_color));
        }
    }

    @Override
    public int getItemCount() {
        return logsList.size();
    }
}
