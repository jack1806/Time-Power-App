package com.example.android.timepower.custom.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.timepower.EditElementActivity;
import com.example.android.timepower.EditTimeTable;
import com.example.android.timepower.R;
import com.example.android.timepower.custom.objects.timeTableElement;
import com.example.android.timepower.custom.objects.timeTable;
import com.example.android.timepower.custom.objects.sharedPrefLinker;
import com.example.android.timepower.contract.intentContractClass;
import com.example.android.timepower.contract.sharedPrefContractClass;
import com.example.android.timepower.interfaceClass.*;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by root on 7/12/17.
 */

public class TimeTableRecyclerViewAdapter extends RecyclerView.Adapter<TimeTableRecyclerViewAdapter.CustomView> {

    ArrayList<timeTableElement> tableElements;
    boolean isFriendsTimeTable = false;

    public TimeTableRecyclerViewAdapter(ArrayList<timeTableElement> timeTableElements){
        tableElements = timeTableElements;
    }
    public TimeTableRecyclerViewAdapter(ArrayList<timeTableElement> timeTableElements,boolean isFriends){
        tableElements =timeTableElements;
        isFriendsTimeTable = isFriends;
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
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick && !isFriendsTimeTable){
                    Gson gson = new Gson();
                    String data = gson.toJson(tableElements.get(position));
                    Intent intent = new Intent(view.getContext(), EditElementActivity.class);
                    intent.putExtra(intentContractClass.adapterRecycler_To_EditElement_Data,data);
                    view.getContext().startActivity(intent);
                    ((EditTimeTable)view.getContext()).finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableElements.size();
    }

    public class CustomView extends RecyclerView.ViewHolder
            implements View.OnClickListener , View.OnLongClickListener{

        TextView header,subHeader,startTime,endTime;
        ItemClickListener itemClickListener;
        String startFinal,endFinal;
        View view;

        public CustomView(View itemView) {
            super(itemView);
            view = itemView;
            header = (TextView)itemView.findViewById(R.id.time_table_item_header);
            subHeader = (TextView)itemView.findViewById(R.id.time_table_item_sub_title);
            startTime = (TextView)itemView.findViewById(R.id.time_table_item_from_time);
            endTime = (TextView)itemView.findViewById(R.id.time_table_item_end_time);
            view.setOnLongClickListener(this);
            view.setOnClickListener(this);
        }

        public void bind(timeTableElement element){
            startFinal = String.format("%02d",element.getStartTime()/60%12)+" : "+String.format("%02d",element.getStartTime()%60)+" "+element.getFromTimeType();
            endFinal = String.format("%02d",element.getEndTime()/60%12)+" : "+String.format("%02d",element.getEndTime()%60)+" "+element.getToTimeType();
            header.setText(element.getHeader());
            subHeader.setText(element.getSubHeader());
            startTime.setText(startFinal);
            endTime.setText(endFinal);
        }

        public void setClickListener(ItemClickListener clickListener){
            this.itemClickListener = clickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),true);
            return true;
        }
    }

}
