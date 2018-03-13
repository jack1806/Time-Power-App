package com.example.android.timepower.Fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.android.timepower.AddElementActivity;
import com.example.android.timepower.MainActivity;
import com.example.android.timepower.R;
import com.example.android.timepower.custom.adapters.*;
import com.example.android.timepower.custom.objects.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FreeFriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FreeFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FreeFriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FreeFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FreeFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FreeFriendsFragment newInstance(String param1, String param2) {
        FreeFriendsFragment fragment = new FreeFriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    int mHourS,mHourE,
        mMinuteS,mMinuteE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String[] strings = new String[]{"AM","PM"};

        final View view = inflater.inflate(R.layout.fragment_free_friends, container, false);

        TextView freeFrom = (TextView) view.findViewById(R.id.free_item_from_time);
        TextView freeTo = (TextView) view.findViewById(R.id.free_item_to_time);
        Calendar calendar = Calendar.getInstance();
        mHourS = calendar.get(Calendar.HOUR_OF_DAY);
        mMinuteS = calendar.get(Calendar.MINUTE);
        int mAMPMS = calendar.get(Calendar.AM_PM);
        String valFrom = String.format("%02d",mHourS%12) + " : " + String.format("%02d",mMinuteS) + " " + strings[mAMPMS];
        freeFrom.setText(valFrom);

        calendar.add(Calendar.HOUR_OF_DAY,1);
        mHourE = calendar.get(Calendar.HOUR_OF_DAY);
        mMinuteE = calendar.get(Calendar.MINUTE);
        int mAMPME = calendar.get(Calendar.AM_PM);
        String valTo = String.format("%02d",mHourE%12) + " : " + String.format("%02d",mMinuteE) + " " + strings[mAMPME];
        freeTo.setText(valTo);

        final Button button = (Button)view.findViewById(R.id.free_search_button);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.free_friend_progress_bar);

        freeFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime((TextView)view,0);
                button.setVisibility(View.VISIBLE);
            }
        });

        freeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime((TextView)view,1);
                button.setVisibility(View.VISIBLE);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progressBar.setVisibility(View.VISIBLE);
                button.setVisibility(View.INVISIBLE);
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                ValueEventListener mainListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userProfile profile = dataSnapshot.getValue(userProfile.class);
                        final ArrayList<String> friends = profile.getFriendsIds();
                        ValueEventListener rootListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                userProfile friendProfile;
                                ArrayList<userProfile> friendProfiles = new ArrayList<>();
                                HashMap<userProfile,ArrayList<Range<Integer>>> hashMap = new HashMap<>();
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    friendProfile = snapshot.child("profile").getValue(userProfile.class);
                                    if(friends.contains(friendProfile.getmId())){
                                        timeTable friendTable = snapshot.child("timetable").getValue(timeTable.class);
                                        if(friendTable!=null) {
                                            Log.d("OKAY", "onDataChange: "+friendTable.toString());
                                            friendProfiles.add(friendProfile);
                                            int dayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2;
                                            if(dayIndex<0)
                                                dayIndex+=7;
                                            ArrayList<Range<Integer>> slotArrayList = friendTable.freeSlot(dayIndex,(mHourS * 60 + mMinuteS), (mHourE * 60 + mMinuteE));
                                            hashMap.put(friendProfile, slotArrayList);
                                        }
                                    }
                                }
                                ExpandableListView freeFriendsExpandableListView = (ExpandableListView) view.findViewById(R.id.free_fr_expandable);
                                FreeFriendsExpandableAdapter freeFriendsExpandableAdapter = new FreeFriendsExpandableAdapter(getContext(),
                                        friendProfiles,hashMap);
                                Log.d("OKAY", "onDataChange: "+freeFriendsExpandableListView.toString());
                                if(freeFriendsExpandableListView!=null) {
                                    freeFriendsExpandableListView.setAdapter(freeFriendsExpandableAdapter);
                                    freeFriendsExpandableListView.invalidate();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    button.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        };
                        reference.addListenerForSingleValueEvent(rootListener);
                        reference.removeEventListener(rootListener);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.VISIBLE);
                    }
                };
                reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile").addListenerForSingleValueEvent(mainListener);
                reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile").removeEventListener(mainListener);
            }
        });

        return view;
    }

    int mStartTimeHour =  Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    int mStartTimeMinute =  Calendar.getInstance().get(Calendar.MINUTE);

    public void setTime(final TextView view,final int i) {
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                //Calendar msettedTime = Calendar.getInstance();
                String time = selectedHour + ":" + selectedMinute;
                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                Date date = null;
                try {
                    date = fmt.parse(time);
                } catch (ParseException e) {

                    e.printStackTrace();
                }
                SimpleDateFormat fmtOut = new SimpleDateFormat("hh : mm aa");
                String formattedTime = fmtOut.format(date);
                //msettedTime.set(Calendar.HOUR_OF_DAY,selectedHour);
                //msettedTime.set(Calendar.MINUTE,selectedMinute);
                if(i==0){
                    mHourS = selectedHour;
                    mMinuteS = selectedMinute;
                }
                else{
                    mHourE = selectedHour;
                    mMinuteE = selectedMinute;
                }
                view.setText(formattedTime);
            }
        }, mStartTimeHour, mStartTimeMinute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
