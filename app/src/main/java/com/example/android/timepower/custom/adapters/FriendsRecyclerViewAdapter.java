package com.example.android.timepower.custom.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.timepower.MainActivity;
import com.example.android.timepower.R;
import com.example.android.timepower.custom.objects.*;
import com.example.android.timepower.contract.*;
import com.example.android.timepower.interfaceClass.RequestClickListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by root on 17/12/17.
 */

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.CustomView>{

    ArrayList<userProfile> userProfiles = new ArrayList<>();
    Context mContext;
    SharedPreferences mSharedPreferences;
    sharedPrefLinker prefLinker = new sharedPrefLinker();
    userProfile mProfile;
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    public FriendsRecyclerViewAdapter(ArrayList<userProfile> userProfiles,
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
        int layout_id = R.layout.layout_profiles_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layout_id,parent,false);
        return new CustomView(view);
    }

    @Override
    public void onBindViewHolder(final CustomView holder, int position) {
        holder.bind(userProfiles.get(position));
        final userProfile current = userProfiles.get(position);
        ArrayList<String> requestIds = current.getRequestIds();
        holder.setClickListener(new RequestClickListener() {
            @Override
            public void onClick(View view, int position) {
                current.addRequest(mProfile.getmId());
                Task<Void> task = mDatabaseReference.child(current.getmId()).child("profile").setValue(current);
                if(!task.isSuccessful())
                    holder.setRequest();
                else
                    Toast.makeText(view.getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
        if(current.getFriendsIds().contains(mProfile.getmId()))
            holder.setFriend();
        else if(requestIds.contains(mProfile.getmId()))
            holder.setRequest();
        else if(mProfile.getRequestIds().contains(current.getmId()))
            holder.setRequestPresent();
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }

    public class CustomView extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        TextView username,email,addFriend,requestSent,friends,requestAlreadyPresent;
        RequestClickListener requestClickListener;
        userProfile profile = new userProfile();

        public CustomView(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.profile_user_name);
            email = (TextView) itemView.findViewById(R.id.profile_email);
            requestSent = (TextView) itemView.findViewById(R.id.request_sent);
            addFriend = (TextView) itemView.findViewById(R.id.add_friend_button);
            friends = (TextView) itemView.findViewById(R.id.friends_already);
            requestAlreadyPresent = (TextView) itemView.findViewById(R.id.already_requested_by);
            requestAlreadyPresent.setVisibility(View.INVISIBLE);
            friends.setVisibility(View.INVISIBLE);
            requestSent.setVisibility(View.INVISIBLE);
            addFriend.setVisibility(View.VISIBLE);
            addFriend.setOnClickListener(this);
        }

        public void setFriend(){
            requestSent.setVisibility(View.INVISIBLE);
            requestAlreadyPresent.setVisibility(View.INVISIBLE);
            friends.setVisibility(View.VISIBLE);
            addFriend.setVisibility(View.INVISIBLE);
        }

        public void setRequest(){
            requestSent.setVisibility(View.VISIBLE);
            requestAlreadyPresent.setVisibility(View.INVISIBLE);
            friends.setVisibility(View.INVISIBLE);
            addFriend.setVisibility(View.INVISIBLE);
        }

        public void bind(userProfile profile){
            this.profile = profile;
            username.setText(profile.getmName());
            email.setText(profile.getmEmail());
            addFriend.setOnClickListener(this);
        }

        public void setRequestPresent(){
            requestSent.setVisibility(View.INVISIBLE);
            friends.setVisibility(View.INVISIBLE);
            addFriend.setVisibility(View.INVISIBLE);
            requestAlreadyPresent.setVisibility(View.VISIBLE);
        }

        public void setClickListener(RequestClickListener clickListener){
            this.requestClickListener = clickListener;
        }

        @Override
        public void onClick(View view) {
            this.requestClickListener.onClick(view,getAdapterPosition());
            Log.d("Requested to ", "onClick: "+this.profile.getmName());
        }
    }

}
