package com.example.android.timepower.custom.objects;

import java.util.ArrayList;

/**
 * Created by root on 16/12/17.
 */

public class userProfile {

    String mName;
    String mNumber;
    String mEmail;
    String mId;
    ArrayList<String> requestIds = new ArrayList<>();
    ArrayList<String> friendsIds = new ArrayList<>();
    int mFriendsCount = 0;
    int mPoints = 0;

    public userProfile(){}

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public void setmPoints(int mPoints) {
        this.mPoints = mPoints;
    }

    public int getmFriendsCount() {
        return mFriendsCount;
    }

    public int getmPoints() {
        return mPoints;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmName() {
        return mName;
    }

    public String getmNumber() {
        return mNumber;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setmFriendsCount(int mFriendsCount) {
        this.mFriendsCount = mFriendsCount;
    }

    public void addRequest(String id){
        requestIds.add(id);
    }

    public ArrayList<String> getRequestIds() {
        return requestIds;
    }

    public void setFriendsIds(ArrayList<String> friendsIds) {
        this.friendsIds = friendsIds;
    }

    public ArrayList<String> getFriendsIds() {
        return friendsIds;
    }

    public void setRequestIds(ArrayList<String> requestIds) {
        this.requestIds = requestIds;
    }

    public void addFriend(String id){
        friendsIds.add(id);
    }

    public void removeRequest(String id){
        requestIds.remove(id);
    }
}
