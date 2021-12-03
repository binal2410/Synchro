package com.synchro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.synchro.R;
import com.synchro.model.ScoreModel;

import java.util.ArrayList;

import static com.synchro.activity.GlobalApplication.speedOfSound;

public class BonsaiScoreAdapter extends RecyclerView.Adapter<BonsaiScoreAdapter.MyViewHolder> {
    private ArrayList<ScoreModel> scoreList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_noOfHits, tv_totelScore, tv_avgScore;
        public LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);
            tv_noOfHits = (TextView) view.findViewById(R.id.tv_noOfHits);
            tv_totelScore = (TextView) view.findViewById(R.id.tv_totelScore);
            tv_avgScore = (TextView) view.findViewById(R.id.tv_avgScore);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        }
    }

    public BonsaiScoreAdapter(Context context, ArrayList<ScoreModel> moviesList) {
        this.context = context;
        this.scoreList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bonsai_score, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == 0) {
            holder.ll_main.setBackgroundColor(context.getResources().getColor(R.color.connect_btn_color));
        } else {
            holder.ll_main.setBackgroundColor(context.getResources().getColor(R.color.connected_background));
        }

        ScoreModel model = scoreList.get(position);
        holder.tv_noOfHits.setText(model.noOfHits);
        holder.tv_avgScore.setText(model.avgScore);
        if (position!=0) {
            holder.tv_totelScore.setText(String.format("%.3f", Double.parseDouble(model.totelScore)));
        }else {
            holder.tv_totelScore.setText(model.totelScore);
        }
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    public static int getTotelscore(ArrayList<Integer> scorelist) {
        int totel = 0;
        for (int i = 0; i < scorelist.size(); i++) {
            totel = totel+scorelist.get(i);
        }
        return totel;
    }

    public static double getAvg(int totel, int size) {
        double avg = 0;
        avg = Double.parseDouble(totel+"")/(size);
        return avg;
    }
}
