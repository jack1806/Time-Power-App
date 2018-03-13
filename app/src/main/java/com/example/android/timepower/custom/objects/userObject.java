package com.example.android.timepower.custom.objects;

/**
 * Created by root on 3/12/18.
 */

public class userObject {

    timeTable table;
    userProfile profile;

    public userObject(){}

    public userObject(userProfile profile,timeTable table){
        this.table = table;
        this.profile = profile;
    }

    public timeTable getTable() {
        return table;
    }

    public userProfile getProfile() {
        return profile;
    }

    public void setProfile(userProfile profile) {
        this.profile = profile;
    }

    public void setTable(timeTable table) {
        this.table = table;
    }

}
