package com.example.android.timepower.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.timepower.R;
import com.example.android.timepower.custom.adapters.RequestRecyclerViewAdapter;
import com.example.android.timepower.custom.objects.*;
import com.example.android.timepower.contract.*;
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
 * {@link RequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
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
        final sharedPrefLinker prefLinker = new sharedPrefLinker();
        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final View view = inflater.inflate(R.layout.fragment_request, container, false);
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(sharedPrefContractClass.SHARED_PREF_NAME,
                sharedPrefContractClass.SHARED_PREF_MODE_PRIVATE);

        final TextView noRequestText = (TextView) view.findViewById(R.id.no_request_text);
        noRequestText.setVisibility(View.INVISIBLE);
        final ImageView noRequestImage = (ImageView) view.findViewById(R.id.no_request_image);
        noRequestImage.setVisibility(View.INVISIBLE);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userProfile profile = dataSnapshot.getValue(userProfile.class);
                Gson gson = new Gson();
                prefLinker.setString(sharedPrefContractClass.SHARED_PREF_PROFILE,
                        gson.toJson(profile),
                        sharedPreferences);

                final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
                final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.request_recycler_view);
                final ArrayList<String> requestIds = profile.getRequestIds();
                if(requestIds.size()==0) {
                    noRequestImage.setVisibility(View.VISIBLE);
                    noRequestText.setVisibility(View.VISIBLE);
                }
                else {
                    final ArrayList<userProfile> userProfiles = new ArrayList<>();
                    Log.d("LoL", "onCreateView: " + gson.toJson(profile));
                    ValueEventListener listener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userProfile profile1 = dataSnapshot.getValue(userProfile.class);
                            if (profile1 != null) {
                                userProfiles.add(profile1);
                                if (profile1.getmId().equals(requestIds.get(requestIds.size() - 1))) {
                                    RequestRecyclerViewAdapter viewAdapter = new RequestRecyclerViewAdapter(userProfiles, getContext());
                                    recyclerView.setAdapter(viewAdapter);
                                    recyclerView.setLayoutManager(layoutManager);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    for (int i = 0; i < requestIds.size(); i++) {
                        databaseReference.child(requestIds.get(i)).child("profile").addListenerForSingleValueEvent(listener);
                        databaseReference.child(requestIds.get(i)).child("profile").removeEventListener(listener);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile").addListenerForSingleValueEvent(listener);
        mDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile").removeEventListener(listener);

        /*View view = inflater.inflate(R.layout.fragment_request, container, false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.request_recycler_view);
        final ArrayList<String> requestIds = profile.getRequestIds();
        final ArrayList<userProfile> userProfiles = new ArrayList<>();
        Gson gson = new Gson();
        Log.d("LoL", "onCreateView: "+gson.toJson(profile));
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile profile1 = dataSnapshot.getValue(userProfile.class);
                if(profile1!=null) {
                    userProfiles.add(profile1);
                    if(profile1.getmId().equals(requestIds.get(requestIds.size()-1))) {
                        RequestRecyclerViewAdapter viewAdapter = new RequestRecyclerViewAdapter(userProfiles,getContext());
                        recyclerView.setAdapter(viewAdapter);
                        recyclerView.setLayoutManager(layoutManager);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        for(int i=0;i<requestIds.size();i++){
            databaseReference.child(requestIds.get(i)).child("profile").addListenerForSingleValueEvent(listener);
            databaseReference.child(requestIds.get(i)).child("profile").removeEventListener(listener);
        }*/
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
