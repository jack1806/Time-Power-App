package com.example.android.timepower.custom.objects;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.android.timepower.contract.sharedPrefContractClass;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by root on 7/12/17.
 */

public class sharedPrefLinker {

    Gson gson = new Gson();
    public static final String TAG = "sharedPrefLinker";

    public sharedPrefLinker(){}

    public String getString(timeTable table){
        Gson gson = new Gson();
        return gson.toJson(table);
    }

    public timeTable getTimeTable(SharedPreferences sharedPreferences){
        timeTable result = new timeTable();
        if(sharedPreferences.contains(sharedPrefContractClass.SHARED_PREF_TIME_TABLE_DATA)){
            String data = sharedPreferences.getString(sharedPrefContractClass.SHARED_PREF_TIME_TABLE_DATA,
                    "Found nothing !!");
            Gson gson = new Gson();
            result = gson.fromJson(data,timeTable.class);
            return result;
        }
        return result;
    }

    public userProfile getProfile(SharedPreferences sharedPreferences){
        userProfile result = new userProfile();
        if(sharedPreferences.contains(sharedPrefContractClass.SHARED_PREF_PROFILE)){
            String data = sharedPreferences.getString(sharedPrefContractClass.SHARED_PREF_PROFILE,
                    "Found nothing !!");
            result = gson.fromJson(data,userProfile.class);
            return result;
        }
        return result;
    }

    public void setLogin(boolean val,SharedPreferences sharedPreferences){
        SharedPreferences.Editor loginEditor = sharedPreferences.edit();
        loginEditor.putBoolean(sharedPrefContractClass.SHARED_PREF_LOGIN,val);
        loginEditor.commit();
    }

    public void setString(String key,String val,SharedPreferences sharedPreferences){
        SharedPreferences.Editor loginEditor = sharedPreferences.edit();
        loginEditor.putString(key,val);
        loginEditor.commit();
    }

    public void setNotify(boolean val,SharedPreferences sharedPreferences){
        SharedPreferences.Editor loginEditor = sharedPreferences.edit();
        loginEditor.putBoolean(sharedPrefContractClass.SHARED_PREF_NOTIF,val);
        loginEditor.commit();
    }

    public void setFirstLogin(boolean val,SharedPreferences sharedPreferences){
        SharedPreferences.Editor loginEditor = sharedPreferences.edit();
        loginEditor.putBoolean(sharedPrefContractClass.SHARED_PREF_FIRST_LOGIN,val);
        loginEditor.commit();
    }

    public void setDatabase(firebaseDatabase database,SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sharedPrefContractClass.SHARED_PREF_DATABASE,gson.toJson(database));
        Log.e(TAG, gson.toJson(database));
        editor.commit();
    }

}
