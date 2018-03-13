package com.example.android.timepower.custom.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.timepower.R;

import java.util.ArrayList;

/**
 * Created by root on 18/12/17.
 */

public class FreeFriendsSlotRecyclerViewAdapter extends RecyclerView.Adapter<FreeFriendsSlotRecyclerViewAdapter.CustomView> {

    Context context;
    ArrayList<Range<Integer>> ranges;

    public FreeFriendsSlotRecyclerViewAdapter(Context context,
                                              ArrayList<Range<Integer>> ranges){
        this.ranges = ranges;
        this.context = context;
    }

    @Override
    public CustomView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layout_id = R.layout.layout_free_friends_child_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout_id,parent,false);
        return new CustomView(view);
    }

    @Override
    public int getItemCount() {
        return ranges.size();
    }

    @Override
    public void onBindViewHolder(CustomView holder, int position) {
        holder.bind(ranges.get(position));
    }

    public class CustomView extends RecyclerView.ViewHolder{

        TextView from,to;

        public CustomView(View itemView) {
            super(itemView);
            from = (TextView) itemView.findViewById(R.id.slot_from_time);
            to = (TextView) itemView.findViewById(R.id.slot_to_time);
        }

        public void bind(Range<Integer> range){
            int fromInt = (int)range.getLower();
            int toInt = (int)range.getUpper();
            String fType = "AM";
            String tType = "AM";
            if(fromInt>=720)
                fType = "PM";
            if(toInt>=720)
                tType = "PM";
            String valFrom = String.format("%02d",(fromInt/60)%12) + " : " + String.format("%02d",fromInt%60) + " " + fType;
            String valTo = String.format("%02d",(toInt/60)%12) + " : " + String.format("%02d",toInt%60) + " " + tType;
            from.setText(valFrom);
            to.setText(valTo);
        }
    }

}
