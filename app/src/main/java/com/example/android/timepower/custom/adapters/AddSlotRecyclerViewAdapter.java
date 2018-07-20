package com.example.android.timepower.custom.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.timepower.R;
import com.example.android.timepower.custom.objects.addSlot;
import com.example.android.timepower.custom.objects.timeTableElement;
import com.example.android.timepower.interfaceClass.ItemClickListener;
import com.example.android.timepower.interfaceClass.SlotDeleteListener;

import java.util.List;

/**
 * Created by root on 2/15/18.
 */

public class AddSlotRecyclerViewAdapter extends RecyclerView.Adapter<AddSlotRecyclerViewAdapter.CustomView> {

    public List<timeTableElement> addSlots;
    public Context context;
    public SlotDeleteListener slotDeleteListener;

    public AddSlotRecyclerViewAdapter(List<timeTableElement> addSlots, Context context,
                                      SlotDeleteListener slotDeleteListener) {
        this.addSlots = addSlots;
        this.context = context;
        this.slotDeleteListener = slotDeleteListener;
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
        holder.setItemClickListener(this.slotDeleteListener);
    }

    public class CustomView extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView day;
        TextView fromTime;
        TextView toTime;
        ImageButton delete;
        SlotDeleteListener itemClickListener;

        public CustomView(View itemView) {
            super(itemView);
            day = (TextView)itemView.findViewById(R.id.day);
            fromTime = (TextView)itemView.findViewById(R.id.start_time);
            toTime = (TextView)itemView.findViewById(R.id.end_time);
            delete = (ImageButton) itemView.findViewById(R.id.delete_item);
            delete.setOnClickListener(this);
        }

        public void bind(int position){
            timeTableElement element = addSlots.get(position);
            String startFinal = String.format("%02d",element.getStartTime()/60%12)+" : "+String.format("%02d",element.getStartTime()%60)+" "+element.getFromTimeType();
            String endFinal = String.format("%02d",element.getEndTime()/60%12)+" : "+String.format("%02d",element.getEndTime()%60)+" "+element.getToTimeType();
            day.setText(addSlots.get(position).getDay());
            fromTime.setText(startFinal);
            toTime.setText(endFinal);
        }

        public void setItemClickListener(SlotDeleteListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            this.itemClickListener.onDelete(addSlots.get(getAdapterPosition()),getAdapterPosition());
        }
    }
}
