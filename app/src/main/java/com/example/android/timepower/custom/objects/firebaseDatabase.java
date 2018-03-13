package com.example.android.timepower.custom.objects;

import java.util.ArrayList;

/**
 * Created by root on 3/12/18.
 */

public class firebaseDatabase {

    ArrayList<userObject> userObjects;

    public ArrayList<userObject> getUserObjects() {
        return userObjects;
    }

    public void setUserObjects(ArrayList<userObject> userObjects) {
        this.userObjects = userObjects;
    }

    public firebaseDatabase(){}
    public firebaseDatabase(ArrayList<userObject> userObjects){
        this.userObjects = userObjects;
    }
}
