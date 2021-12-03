package com.synchro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.synchro.R;
import com.synchro.activity.SelectedTargetSwitching;
import com.synchro.activity.TargetSwitchingActivity;
import com.synchro.model.TargetSwitchingModel;
import com.synchro.utils.AppConstants;
import com.synchro.utils.AppMethods;
import com.synchro.utils.SharedPref;

import java.util.ArrayList;

public class TargetSwitchingAdapter extends RecyclerView.Adapter<TargetSwitchingAdapter.MyViewHolder> {
    private ArrayList<TargetSwitchingModel> targetsList;
    private Context context;
    private TargetSwitchItemClickListener targetSwitchItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_targetName;
        public LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);
            tv_targetName = (TextView) view.findViewById(R.id.tv_targetName);
            ll_main =  view.findViewById(R.id.ll_main);
        }
    }

    public TargetSwitchingAdapter(Context context, ArrayList<TargetSwitchingModel> moviesList,TargetSwitchItemClickListener targetSwitchItemClickListener) {
        this.context = context;
        this.targetsList = moviesList;
        this.targetSwitchItemClickListener=targetSwitchItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_target_switching, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TargetSwitchingModel model = targetsList.get(position);
        holder.tv_targetName.setText(model.targetName);

        if (model.isSelected){
            holder.tv_targetName.setTextColor(context.getResources().getColor(R.color.connect_btn_color));
            holder.ll_main.setBackgroundResource(R.drawable.back_edittext);
        }else {
            holder.tv_targetName.setTextColor(context.getResources().getColor(R.color.footer_disable_color));
            holder.ll_main.setBackgroundResource(R.drawable.back_edittext);
        }

        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (context instanceof TargetSwitchingActivity) {
                    ((TargetSwitchingActivity)context).setOnItemSelectListener(position);
                }*/
                targetSwitchItemClickListener.onTargetSelected(position);

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return targetsList.size();
    }

    public interface TargetSwitchItemClickListener
    {
        void onTargetSelected(int position);
    }
}
