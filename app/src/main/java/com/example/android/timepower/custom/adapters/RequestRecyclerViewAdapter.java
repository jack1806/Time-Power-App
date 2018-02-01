package com.example.android.timepower.custom.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.timepower.R;
import com.example.android.timepower.contract.*;
import com.example.android.timepower.custom.objects.*;
import com.example.android.timepower.interfaceClass.RequestClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by root on 17/12/17.
 */

public class RequestRecyclerViewAdapter extends RecyclerView.Adapter<RequestRecyclerViewAdapter.CustomView>{

    ArrayList<userProfile> userProfiles = new ArrayList<>();
    Context mContext;
    SharedPreferences mSharedPreferences;
    sharedPrefLinker prefLinker = new sharedPrefLinker();
    userProfile mProfile;
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    public RequestRecyclerViewAdapter(ArrayList<userProfile> userProfiles,
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
        int layout_id = R.layout.layout_request_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout_id,parent,false);
        return new CustomView(view);
    }

    @Override
    public void onBindViewHolder(final CustomView holder, int position) {
        final userProfile current = userProfiles.get(position);
        holder.setListener(new RequestClickListener() {
            @Override
            public void onClick(View view, int position) {
                TextView textView = (TextView)view;
                if(textView.getText().toString().equals("Accept")){
                    holder.action("Accepted");
                    mProfile.removeRequest(current.getmId());
                    mProfile.addFriend(current.getmId());
                    current.addFriend(mProfile.getmId());
                    current.setmFriendsCount(current.getmFriendsCount()+1);
                    mProfile.setmFriendsCount(mProfile.getmFriendsCount()+1);
                    mDatabaseReference.child(mProfile.getmId()).child("profile").setValue(mProfile);
                    mDatabaseReference.child(current.getmId()).child("profile").setValue(current);
                }
                else{
                    holder.action("Declined");
                    mProfile.removeRequest(current.getmId());
                    mDatabaseReference.child(mProfile.getmId()).child("profile").setValue(mProfile);
                }
            }
        });
        holder.bind(current);
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }

    public class CustomView extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView username,email,accept,decline,action;
        RequestClickListener requestClickListener;

        public CustomView(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.profile_user_name);
            //email = (TextView) itemView.findViewById(R.id.profile_email);
            accept = (TextView) itemView.findViewById(R.id.accept_request);
            decline = (TextView) itemView.findViewById(R.id.decline_request);
            action = (TextView) itemView.findViewById(R.id.action_took);
            accept.setVisibility(View.VISIBLE);
            decline.setVisibility(View.VISIBLE);
            action.setVisibility(View.INVISIBLE);
        }

        public void bind(userProfile profile){
            username.setText(profile.getmName());
            //email.setText(profile.getmEmail());
            accept.setOnClickListener(this);
            decline.setOnClickListener(this);
        }

        public void action(String val){
            accept.setVisibility(View.INVISIBLE);
            decline.setVisibility(View.INVISIBLE);
            action.setVisibility(View.VISIBLE);
            action.setText(val);
        }

        public void setListener(RequestClickListener listener){
            this.requestClickListener = listener;
        }

        @Override
        public void onClick(View view) {
            this.requestClickListener.onClick(view,getAdapterPosition());
        }
    }

}