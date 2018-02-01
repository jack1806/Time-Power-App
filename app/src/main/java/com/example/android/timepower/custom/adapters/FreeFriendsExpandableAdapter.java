package com.example.android.timepower.custom.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.android.timepower.R;
import com.example.android.timepower.custom.objects.*;
import com.example.android.timepower.contract.*;

/**
 * Created by root on 18/12/17.
 */

public class FreeFriendsExpandableAdapter extends BaseExpandableListAdapter {

    HashMap<userProfile,ArrayList<Range<Integer>>> childHashMap = new HashMap<>();
    Context context;
    ArrayList<userProfile> groupLists = new ArrayList<>();
    String TAG = "FreeFriendsExpandableAdapter.java";

    public FreeFriendsExpandableAdapter(Context context,
        ArrayList<userProfile> groupArrayList,
        HashMap<userProfile,ArrayList<Range<Integer>>> childHashMap){
        this.context = context;
        this.childHashMap = childHashMap;
        this.groupLists = groupArrayList;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        userProfile groupItem = (userProfile) getGroup(i);
        if(view==null){
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_free_friends_group_header,null);
        }
        TextView name = (TextView)view.findViewById(R.id.free_user_user_name);
        TextView email = (TextView)view.findViewById(R.id.free_user_email);
        name.setText(groupItem.getmName());
        email.setText(groupItem.getmEmail());
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        Range<Integer> child = (Range<Integer>) getChild(i,i1);
        ArrayList<Range<Integer>> childArray = this.childHashMap.get(getGroup(i));
        if(view==null){
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_free_friends_child_item,null);
        }
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.layout_free_friends_child_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        FreeFriendsSlotRecyclerViewAdapter adapter = new FreeFriendsSlotRecyclerViewAdapter(
                this.context,childArray);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public Object getGroup(int i) {
        Log.d(TAG, "getGroup: "+this.groupLists.get(i).toString());
        return this.groupLists.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        Log.d(TAG, "getGroup: "+this.childHashMap.get(this.groupLists.get(i)).get(i1).toString());
        return this.childHashMap.get(this.groupLists.get(i)).get(i1);
    }

    @Override
    public int getGroupCount() {
        return this.groupLists.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}