package com.example.android.timepower;



import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.timepower.Fragment.*;
import com.example.android.timepower.contract.*;
import com.example.android.timepower.custom.Receivers.*;
import com.example.android.timepower.custom.objects.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentTransaction fragmentTransaction;
    Toolbar toolbar;
    NavigationView navigationView;
    SharedPreferences mSharedPreferences;
    sharedPrefLinker prefLinker = new sharedPrefLinker();
    timeTable mMainTable;
    DatabaseReference mDatabaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    userProfile mUserProfile;
    String userId = "";
    Gson gson = new Gson();

    private String TAG = "MainActivity.class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        getLatestData();

        if(firebaseAuth.getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile profile = dataSnapshot.getValue(userProfile.class);
                Gson gson = new Gson();
                prefLinker.setString(sharedPrefContractClass.SHARED_PREF_PROFILE,
                        gson.toJson(profile),
                        mSharedPreferences);

                View headerView = navigationView.getHeaderView(0);
                if(headerView!=null && userId!=null){
                    TextView headerEmail = (TextView)headerView.findViewById(R.id.header_email);
                    TextView headerName = (TextView)headerView.findViewById(R.id.header_name);
                    TextView imageTitle = (TextView)headerView.findViewById(R.id.user_start);
                    headerEmail.setText(profile.getmEmail());
                    if(profile.getmName()!=null) {
                        headerName.setText(profile.getmName());
                        imageTitle.setText(""+profile.getmName().charAt(0));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mSharedPreferences = getSharedPreferences(sharedPrefContractClass.SHARED_PREF_NAME,
                sharedPrefContractClass.SHARED_PREF_MODE_PRIVATE);
        mMainTable = prefLinker.getTimeTable(mSharedPreferences);
        mUserProfile = prefLinker.getProfile(mSharedPreferences);
        userId = mUserProfile.getmId();

        setSupportActionBar(toolbar);
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null) {
            userId = firebaseUser.getUid();
            mDatabaseReference.child(userId).child("profile").addListenerForSingleValueEvent(listener);
            mDatabaseReference.removeEventListener(listener);
            if(mSharedPreferences.contains(sharedPrefContractClass.SHARED_PREF_LOGIN)) {
                if (mSharedPreferences.getBoolean(sharedPrefContractClass.SHARED_PREF_LOGIN, false)) {
                    if (mMainTable.sync(getBaseContext())) {
                        prefLinker.setLogin(false,mSharedPreferences);
                        startActivity(new Intent(MainActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }
            else {
                if (mMainTable.sync(getBaseContext())) {
                    prefLinker.setLogin(false,mSharedPreferences);
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                }
            }
        }
        else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        View headerView = navigationView.getHeaderView(0);
        if(headerView!=null && userId!=null){
            TextView headerEmail = (TextView)headerView.findViewById(R.id.header_email);
            TextView headerName = (TextView)headerView.findViewById(R.id.header_name);
            headerEmail.setText(mUserProfile.getmEmail());
            if(mUserProfile.getmName()!=null)
                headerName.setText(mUserProfile.getmName());
        }

        navigationView.setNavigationItemSelectedListener(this);
        changeFrame(new ViewTimeTableFragment(),R.string.nav_view_time_table);

        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Test")
                .setContentText("It's Working")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{0,300,100,200,0,200});

        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent,0);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);*/
    }

    public void changeFrame(Fragment fragment, int id){
        if(id==R.string.nav_view_time_table)
            id = R.string.today;
        getSupportActionBar().setTitle(id);
        //toolbar.setTitleTextColor(getColor(R.color.colorAccent));
        //toolbar.setBackgroundColor(getColor(R.color.black));
        fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.nav_edit_time_table) {
            startActivity(new Intent(this, EditTimeTable.class));
            finish();
            return true;
        }
        else if (id == R.id.nav_notif){
            /*if(mMainTable.sync(getBaseContext())){
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
            else
                Toast.makeText(this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();*/
            postNotification(MainActivity.this,mMainTable);
            return true;
        }
        else if (id == R.id.nav_logout){
            if(mMainTable.sync(this)) {
                firebaseAuth.signOut();
                timeTable emptyTable = new timeTable();
                String emptyTableJSON = prefLinker.getString(emptyTable);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(sharedPrefContractClass.SHARED_PREF_TIME_TABLE_DATA,emptyTableJSON);
                editor.commit();
                prefLinker.setLogin(true,mSharedPreferences);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
            else
                Toast.makeText(this, "Something Went Wrong !!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_view_time_table)
            changeFrame(new ViewTimeTableFragment(),R.string.nav_view_time_table);
        else if(id == R.id.nav_my_profile)
            changeFrame(new MyProfileFragment(),R.string.nav_my_profile);
        else if(id == R.id.nav_home_page)
            changeFrame(new TimePowerFragment(),R.string.nav_home_page);
        else if(id == R.id.nav_add_fr)
            changeFrame(new AddFriendsFragment(),R.string.nav_add_fr);
        else if(id == R.id.nav_requests)
            changeFrame(new RequestFragment(),R.string.nav_request);
        else if(id == R.id.nav_my_fr)
            changeFrame(new MyFriendsFragment(),R.string.nav_my_friends);
//        else if(id == R.id.nav_free_fr)
//            changeFrame(new FreeFriendsFragment(),R.string.nav_free_friends);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void postNotification(Context context,timeTable table){
        String s = Calendar.getInstance().getTime().toString().split(" ")[0];
        ArrayList<timeTableElement> dayElements = table.getDayElements(s);

        int currentTime = Calendar.getInstance().get(Calendar.MINUTE)*60 +
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*3600
                + Calendar.getInstance().get(Calendar.SECOND) + 20*60;
        timeTableElement iteratorElement = new timeTableElement();
        int flag = 0;
        int iteratorEnd,iteratorStart;
        int i;
        for(i=0;i<dayElements.size();i++){
            Log.d(TAG, "postNotification: "+iteratorElement.getHeader());
            iteratorElement = dayElements.get(i);
            iteratorEnd = iteratorElement.getEndTime();
            iteratorStart = iteratorElement.getStartTime();
            if(iteratorStart*60 < currentTime) {
                Log.d(TAG, "postNotification: "+iteratorElement.getHeader());
                continue;
            }
            flag = 1;
            break;
        }

        Calendar calendar = Calendar.getInstance();
        int currentInSecond = calendar.get(Calendar.MINUTE)*60 + calendar.get(Calendar.HOUR_OF_DAY)*3600 + calendar.get(Calendar.SECOND);

        if(flag==1){
            long delay = ((long)iteratorElement.getStartTime())*60*1000 - ((long)currentInSecond)*1000;
            scheduleNotification(context,iteratorElement,delay,0);
            Log.d(TAG, "postNotification: "+delay);
        }
        Log.d(TAG, "postNotification: "+flag);

    }

    public void scheduleNotification(Context context,timeTableElement tableElement,long delay,int notificationId){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(tableElement.getHeader())
                .setContentText(tableElement.getSubHeader())
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{100,0,100});

        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,notificationId, intent,0);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context,TimeTableNotifier.class);
        notificationIntent.putExtra(TimeTableNotifier.NOTIFICATION_ID , notificationId);
        notificationIntent.putExtra(TimeTableNotifier.NOTIFICATION , notification);
        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(context,notificationId,
                notificationIntent,0);
        long futureInMillis = SystemClock.elapsedRealtime() + delay - 1200000;
        Log.d(TAG, "scheduleNotification: "+futureInMillis);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,futureInMillis,broadcastPendingIntent);

        //Log.d(TAG, "scheduleNotification: "+alarmManager.getNextAlarmClock().toString());

        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefContractClass.SHARED_PREF_NAME,
                sharedPrefContractClass.SHARED_PREF_MODE_PRIVATE);
        sharedPrefLinker prefLinker = new sharedPrefLinker();
        prefLinker.setNotify(false,sharedPreferences);
        Log.d(TAG, "scheduleNotification: "+tableElement.getHeader());
    }

    public boolean getLatestData(){
        ConnectivityManager connectivityManager;
        NetworkInfo networkInfo;
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnectedOrConnecting()) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            final ArrayList<userObject> userObjects = new ArrayList<>();
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        userObjects.add(new userObject(snapshot.child("profile").getValue(userProfile.class),
                                snapshot.child("timetable").getValue(timeTable.class)));
                    prefLinker.setDatabase(new firebaseDatabase(userObjects), mSharedPreferences);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return true;
        }
        return false;
    }

}