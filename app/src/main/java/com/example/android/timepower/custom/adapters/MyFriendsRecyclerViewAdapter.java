package com.example.android.timepower.custom.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.android.timepower.EditTimeTable;
import com.example.android.timepower.FriendTimeTable;
import com.example.android.timepower.MainActivity;
import com.example.android.timepower.R;
import com.example.android.timepower.contract.*;
import com.example.android.timepower.interfaceClass.*;
import com.example.android.timepower.custom.objects.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

/**
 * Created by root on 18/12/17.
 */

public class MyFriendsRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendsRecyclerViewAdapter.CustomView> {

    ArrayList<userProfile> userProfiles = new ArrayList<>();
    ArrayList<timeTable> timeTables = new ArrayList<>();
    Context mContext;
    SharedPreferences mSharedPreferences;
    sharedPrefLinker prefLinker = new sharedPrefLinker();
    userProfile mProfile;
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    public MyFriendsRecyclerViewAdapter(ArrayList<userProfile> userProfiles,
                                        ArrayList<timeTable> timeTables,
                                        Context context){
        this.mContext = context;
        this.timeTables = timeTables;
        this.userProfiles = userProfiles;
        mSharedPreferences = context.getSharedPreferences(sharedPrefContractClass.SHARED_PREF_NAME,
                sharedPrefContractClass.SHARED_PREF_MODE_PRIVATE);
        mProfile = prefLinker.getProfile(mSharedPreferences);
    }

    @Override
    public CustomView onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layout_id = R.layout.layout_my_friends_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout_id,parent,false);
        return new CustomView(view);
    }

    @Override
    public void onBindViewHolder(CustomView holder, int position) {
        holder.bind(userProfiles.get(position),timeTables.get(position));
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }

    public class CustomView extends RecyclerView.ViewHolder{

        TextView mName;
        TextView mEmail;
        TextView mNameStart;
        ImageView mOnline;
        RelativeLayout viewTable;
        View main;

        public CustomView(View itemView) {
            super(itemView);
            main = itemView;
            mName = (TextView) itemView.findViewById(R.id.profile_user_name);
            mEmail = (TextView) itemView.findViewById(R.id.profile_email);
            mNameStart = (TextView) itemView.findViewById(R.id.username_start);
            mOnline = (ImageView) itemView.findViewById(R.id.online_status);
            viewTable = (RelativeLayout) itemView.findViewById(R.id.view_time_table);
        }

        public void bind(final userProfile profile, final timeTable table){
            mName.setText(profile.getmName().toLowerCase().replace(" ",""));
            mNameStart.setText(""+profile.getmName().replace(" ","").charAt(0));
            mEmail.setText(profile.getmEmail());
            mOnline.setVisibility(View.VISIBLE);
            if(table.isFree())
                mOnline.setVisibility(View.VISIBLE);
            else
                mOnline.setVisibility(View.INVISIBLE);
            viewTable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(table.getData()!=null) {
                        Intent intent = new Intent(mContext, FriendTimeTable.class);
                        Gson gson = new Gson();
                        intent.putExtra("table", gson.toJson(table));
                        intent.putExtra("name",profile.getmName().toLowerCase().split(" ")[0]);
                        mContext.startActivity(intent);
                    }
                    else
                        Toast.makeText(mContext,"No TimeTable found.",Toast.LENGTH_LONG);
                }
            });
            main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.DIAL");
                    intent.setData(Uri.parse("tel:"+profile.getmNumber()));
                    mContext.startActivity(intent);
                    return false;
                }
            });
        }

    }

}
