package com.example.android.timepower.custom.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.timepower.R;
import com.example.android.timepower.custom.objects.addSlot;

import java.util.List;

/**
 * Created by root on 2/15/18.
 */

public class AddSlotRecyclerViewAdapter extends RecyclerView.Adapter<AddSlotRecyclerViewAdapter.CustomView> {

    public List<addSlot> addSlots;
    public Context context;

    public AddSlotRecyclerViewAdapter(List<addSlot> addSlots,Context context) {
        this.addSlots = addSlots;
        this.context = context;
    }

    @Override
    public CustomView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_add_slot_item,parent,false);
        return new CustomView(view);
    }

    @Override
    public int getItemCount() {
        return addSlots.size();
    }

    @Override
    public void onBindViewHolder(CustomView holder, int position) {
        holder.bind(position);
    }

    public class CustomView extends RecyclerView.ViewHolder{

        TextView day;
        TextView fromTime;
        TextView toTime;
        Button delete;

        public CustomView(View itemView) {
            super(itemView);
            day = (TextView)itemView.findViewById(R.id.day);
            fromTime = (TextView)itemView.findViewById(R.id.start_time);
            toTime = (TextView)itemView.findViewById(R.id.end_time);
            delete = (Button)itemView.findViewById(R.id.delete_item);
        }

        public void bind(int position){
            day.setText(addSlots.get(position).getDay());
            fromTime.setText(addSlots.get(position).getFrom());
            toTime.setText(addSlots.get(position).getTo());
        }
    }
}
