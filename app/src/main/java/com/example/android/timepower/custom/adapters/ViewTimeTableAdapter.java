package com.example.android.timepower.custom.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.timepower.EditElementActivity;
import com.example.android.timepower.EditTimeTable;
import com.example.android.timepower.R;
import com.example.android.timepower.contract.GlobalVariableClass;
import com.example.android.timepower.contract.intentContractClass;
import com.example.android.timepower.custom.objects.timeTableElement;
import com.example.android.timepower.interfaceClass.ItemClickListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import com.example.android.timepower.contract.GlobalVariableClass;

/**
 * Created by root on 11/12/17.
 */

public class ViewTimeTableAdapter extends RecyclerView.Adapter<ViewTimeTableAdapter.CustomView>{

    ArrayList<timeTableElement> tableElements;
    String TAG = "JACK | ";

    public ViewTimeTableAdapter(ArrayList<timeTableElement> timeTableElements){
        tableElements = timeTableElements;
    }

    @Override
    public CustomView onCreateViewHolder(final ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layout_id = R.layout.layout_time_table_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout_id,parent,false);
        return new CustomView(view);
    }

    @Override
    public void onBindViewHolder(CustomView holder, int position) {
        holder.bind(tableElements.get(position));
    }

    @Override
    public int getItemCount() {
        return tableElements.size();
    }

    public class CustomView extends RecyclerView.ViewHolder{

        TextView header,subHeader,startTime,endTime;
        ItemClickListener itemClickListener;
        String startFinal,endFinal;
        View view;
        ImageView indicator;

        public CustomView(View itemView) {
            super(itemView);
            view = itemView;
            header = (TextView)itemView.findViewById(R.id.time_table_item_header);
            subHeader = (TextView)itemView.findViewById(R.id.time_table_item_sub_title);
            startTime = (TextView)itemView.findViewById(R.id.time_table_item_from_time);
            endTime = (TextView)itemView.findViewById(R.id.time_table_item_end_time);
            indicator = (ImageView) itemView.findViewById(R.id.startIndicator);
            int a = (int)(Math.random()*50);
            Log.e(TAG, "CustomView: "+a);
            indicator.setColorFilter(Color.parseColor(GlobalVariableClass.colors[a%5]));
        }

        public void bind(timeTableElement element){
            startFinal = String.format("%02d",element.getStartTime()/60%12)+" : "+String.format("%02d",element.getStartTime()%60)+" "+element.getFromTimeType();
            endFinal = String.format("%02d",element.getEndTime()/60%12)+" : "+String.format("%02d",element.getEndTime()%60)+" "+element.getToTimeType();
            header.setText(element.getHeader());
            subHeader.setText(element.getSubHeader());
            startTime.setText(startFinal);
            endTime.setText(endFinal);
        }

    }

}
