package com.example.android.timepower.custom.Receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.android.timepower.custom.objects.*;
import com.example.android.timepower.contract.*;
import com.example.android.timepower.MainActivity;

/**
 * Created by root on 14/12/17.
 */

public class TimeTableNotifier extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION = "notification";
    public String TAG = "BroadCast Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive: "+intent.getIntExtra(NOTIFICATION_ID,0));

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID,0);
        notificationManager.notify(notificationId,notification);

        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefContractClass.SHARED_PREF_NAME,
                sharedPrefContractClass.SHARED_PREF_MODE_PRIVATE);

        sharedPrefLinker prefLinker = new sharedPrefLinker();
        timeTable mMainTable = prefLinker.getTimeTable(sharedPreferences);

        MainActivity mainActivity = new MainActivity();
        mainActivity.postNotification(context,mMainTable);

    }
}
