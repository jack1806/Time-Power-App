package com.example.android.timepower.custom.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.android.timepower.R;
import com.example.android.timepower.contract.*;
import com.example.android.timepower.interfaceClass.*;
import com.example.android.timepower.custom.objects.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by root on 18/12/17.
 */

public class MyFriendsRecyclerViewAdapter extends RecyclerView.Adapter<MyFriendsRecyclerViewAdapter.CustomView> {

    ArrayList<userProfile> userProfiles = new ArrayList<>();
    Context mContext;
    SharedPreferences mSharedPreferences;
    sharedPrefLinker prefLinker = new sharedPrefLinker();
    userProfile mProfile;
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    public MyFriendsRecyclerViewAdapter(ArrayList<userProfile> userProfiles,
                                        Context context){
        this.mContext = context;
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
        holder.bind(userProfiles.get(position));
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }

    public class CustomView extends RecyclerView.ViewHolder{

        TextView mName;
        TextView mEmail;

        public CustomView(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.profile_user_name);
            mEmail = (TextView) itemView.findViewById(R.id.profile_email);
        }

        public void bind(userProfile profile){
            mName.setText(profile.getmName());
            mEmail.setText(profile.getmEmail());
        }

    }

}
