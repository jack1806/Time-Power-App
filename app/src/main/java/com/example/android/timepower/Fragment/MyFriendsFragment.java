package com.example.android.timepower.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.timepower.R;
import com.example.android.timepower.contract.*;
import com.example.android.timepower.custom.adapters.MyFriendsRecyclerViewAdapter;
import com.example.android.timepower.interfaceClass.*;
import com.example.android.timepower.custom.objects.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyFriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "MyFriend";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFriendsFragment newInstance(String param1, String param2) {
        MyFriendsFragment fragment = new MyFriendsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_friends, container, false);
//        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//        ValueEventListener listener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                final ValueEventListener listener1 = new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        userProfile profile = dataSnapshot.getValue(userProfile.class);
//                        final ArrayList<String> userFriendIds = profile.getFriendsIds();
//                        databaseReference.addListenerForSingleValueEvent(
//                                new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        ArrayList<userProfile> userProfiles = new ArrayList<>();
//                                        ArrayList<timeTable> timeTables = new ArrayList<>();
//                                        userProfile profileIterator;
//                                        timeTable tableIterator;
//                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                                            profileIterator = snapshot.child("profile").getValue(userProfile.class);
//                                            tableIterator = snapshot.child("timetable").getValue(timeTable.class);
//                                            if(userFriendIds.contains(profileIterator.getmId())
//                                                    && tableIterator!=null) {
//                                                userProfiles.add(profileIterator);
//                                                timeTables.add(tableIterator);
//                                            }
//                                        }
//                                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
//                                        MyFriendsRecyclerViewAdapter adapter = new MyFriendsRecyclerViewAdapter(userProfiles,
//                                                timeTables,getContext());
//                                        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_friends_recycler_view);
//                                        recyclerView.setLayoutManager(layoutManager);
//                                        recyclerView.setAdapter(adapter);
//                                    }
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//                                    }
//                                });
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                };
//                databaseReference.child(uId).child("profile").addListenerForSingleValueEvent(listener1);
//                databaseReference.child(uId).child("profile").removeEventListener(listener1);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            };
//        };
//        databaseReference.addListenerForSingleValueEvent(listener);
//        databaseReference.removeEventListener(listener);
//
        SharedPreferences preferences = getContext().getSharedPreferences(sharedPrefContractClass.SHARED_PREF_NAME,
                sharedPrefContractClass.SHARED_PREF_MODE_PRIVATE);
        Gson gson = new Gson();
        sharedPrefLinker prefLinker = new sharedPrefLinker();
        firebaseDatabase database = gson.fromJson(preferences.getString(sharedPrefContractClass.SHARED_PREF_DATABASE,""),firebaseDatabase.class);

        ArrayList<userObject> userObjects = database.getUserObjects();
        ArrayList<userProfile> userProfiles = new ArrayList<>();
        ArrayList<timeTable> timeTables = new ArrayList<>();

        userProfile profile = prefLinker.getProfile(preferences);
        Log.e(TAG,""+userObjects.size());
        Log.e(TAG, ""+profile.getFriendsIds().size());

        for(int i=0;i<profile.getFriendsIds().size();i++){
            if(profile.getFriendsIds().get(i)!=null) {
                for (int j = 0; j < userObjects.size(); j++) {
                    Log.e(TAG,userObjects.get(j).getProfile().getmId()+" - "+profile.getFriendsIds().get(i));
                    if (userObjects.get(j).getProfile().getmId().equals(profile.getFriendsIds().get(i))) {
                        Log.e(TAG," "+userObjects.get(j).getTable());
                        if (userObjects.get(j).getTable() != null) {
                            Log.e(TAG, gson.toJson(userObjects.get(j).getProfile()));
                            userProfiles.add(userObjects.get(j).getProfile());
                            timeTables.add(userObjects.get(j).getTable());
                        }
                    }
                }
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        MyFriendsRecyclerViewAdapter adapter = new MyFriendsRecyclerViewAdapter(userProfiles,
                timeTables,getContext());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_friends_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
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
