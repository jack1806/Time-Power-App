package com.example.android.timepower.custom.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Range;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import com.example.android.timepower.contract.*;

/**
 * Created by root on 6/12/17.
 */

public class timeTable {

    int FIND_ELEMENT_DAY_INDEX = 0;
    int FIND_ELEMENT_ELEMENT_INDEX = 1;
    long UPDATED_ON_INT = 0;

    //String profileId,
    //        timeTableId;

    ArrayList<String> days = new ArrayList<>();
    Map<String,ArrayList<timeTableElement>> data;

    public timeTable(){
        initDays();
        data = new HashMap<>();
        for(int i=0;i<days.size();i++)
            data.put(days.get(i),new ArrayList<timeTableElement>());
        //this.profileId = null;
        //this.timeTable = null;
    }
    // String profileId,
    // String timeTableId,

    public void initDays(){
        this.days.add("Mon");
        this.days.add("Tue");
        this.days.add("Wed");
        this.days.add("Thu");
        this.days.add("Fri");
        this.days.add("Sat");
        this.days.add("Sun");
    }

    public ArrayList<String> getDays() {
        return days;
    }

    public Map<String, ArrayList<timeTableElement>> getData() {
        return data;
    }

    /*public String getProfileId() {
        return profileId;
    }*/

    /*public String getTimeTableId() {
        return timeTableId;
    }*/

    public ArrayList<timeTableElement> getDayElements(String day){
        ArrayList<timeTableElement> result = this.data.get(day);
        if(result!=null)
            return result;
        result = new ArrayList<>();
        return result;
    }


    // Returns the location of element in the Map in the form of list
    // result.index(0) has DAY_INDEX
    // result.index(1) has ELEMENT_INDEX
    public ArrayList<Integer> findElement(timeTableElement tableElement){
        int dayIterator,dataIterator;
        Gson gson = new Gson();
        String s1;
        String s2;
        ArrayList<Integer> result = new ArrayList<>();
        timeTableElement tableElementIterator;
        ArrayList<timeTableElement> currentDayIterator;
        for(dayIterator=0;dayIterator<this.days.size();dayIterator++){
            currentDayIterator = this.getDayElements(this.days.get(dayIterator));
            for(dataIterator=0;dataIterator<currentDayIterator.size();dataIterator++){
                tableElementIterator = currentDayIterator.get(dataIterator);
                Log.d("findElement: ",gson.toJson(tableElement)+"\n"+gson.toJson(tableElementIterator));
                s1 = gson.toJson(tableElement);
                s2 = gson.toJson(tableElementIterator);
                Log.d("s1",s1);
                Log.d("s2",s2);
                Log.d("Equals",(s1==s2)+"");
                if(s1.equals(s2)) {
                    result.add(dayIterator);
                    result.add(dataIterator);
                    return result;
                }
            }
            Log.d("findElement: ",tableElement.getHeader());
        }
        return null;
    }

    public timeTableElement addElement(String day,timeTableElement tableElement){
        ArrayList<timeTableElement> currentDay = this.getDayElements(day);
        if(currentDay.size()!=0) {
            int daySize = currentDay.size();
            int updateStart = tableElement.getStartTime();
            int updateEnd = tableElement.getEndTime();
            Range<Integer> updateRange = Range.create(updateStart,updateEnd);
            timeTableElement elementIterator;
            int indexIterator;
            int startTimeIterator;
            int endTimeIterator;
            Range<Integer> rangeIterator;
            for(indexIterator = 0;indexIterator<daySize;indexIterator++){
                elementIterator = currentDay.get(indexIterator);
                startTimeIterator = elementIterator.getStartTime();
                endTimeIterator = elementIterator.getEndTime();
                rangeIterator = Range.create(startTimeIterator+1,endTimeIterator-1);
                Log.d("Range Iterator",rangeIterator.toString());
                Log.d("Range Update",updateRange.toString());
                try {
                    if (rangeIterator.intersect(updateRange) != null)
                        return elementIterator;
                }
                catch (IllegalArgumentException e){
                    if (updateEnd<=startTimeIterator){
                        Log.d("AddElement-1",tableElement.getHeader());
                        currentDay.add(indexIterator,tableElement);
                        this.data.put(day,currentDay);
                        return null;
                    }
                }
            }
            Log.d("AddElement",tableElement.getHeader());
            currentDay.add(tableElement);
            this.data.put(day,currentDay);
            return null;
        }
        else{
            Log.d("AddElement",tableElement.getHeader());
            currentDay.add(tableElement);
            this.data.put(day,currentDay);
            return null;
        }
    }

    public boolean delete(timeTableElement tableElement){
        Gson gson = new Gson();
        ArrayList<Integer> foundElement = findElement(tableElement);
        Log.d("timeTable", "delete: "+data.toString());
        if(foundElement!=null){
            Log.d("timeTable", "delete: FOUND "+foundElement.get(1)+foundElement.get(0));
            Log.d("Value",gson.toJson(this.data.get(this.days.get(foundElement.get(FIND_ELEMENT_DAY_INDEX)))));
            if(data.get(days.get(foundElement.get(FIND_ELEMENT_DAY_INDEX))).remove((int)foundElement.get(FIND_ELEMENT_ELEMENT_INDEX))!=null)
                return true;
        }
        return false;
    }

    public boolean update(String day,
                          timeTableElement oldTableElement,
                          timeTableElement updateTableElement){
        Log.d("Timetable ", "update: "+oldTableElement.getHeader()+" "+updateTableElement.getHeader());
        if(delete(oldTableElement)){
            if(addElement(day,updateTableElement)==null)
                return true;
            addElement(day,oldTableElement);
            return false;
        }
        return false;
    }

    public void setUpdatedOnInt(long UPDATED_ON_INT) {
        this.UPDATED_ON_INT = UPDATED_ON_INT;
    }

    public long getUpdatedOnInt() {
        return UPDATED_ON_INT;
    }

    public boolean sync(Context context){
        final sharedPrefLinker prefLinker = new sharedPrefLinker();
        Gson gson = new Gson();
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPrefContractClass.SHARED_PREF_NAME,
                sharedPrefContractClass.SHARED_PREF_MODE_PRIVATE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = null;
        if(user != null) {
            userId = user.getUid();
            final DatabaseReference database = FirebaseDatabase.getInstance().getReference(userId);
            final timeTable localData = prefLinker.getTimeTable(sharedPreferences);
            database.child("timetable").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    timeTable cloudData = dataSnapshot.getValue(timeTable.class);
                    if(cloudData==null)
                        cloudData = new timeTable();
                    if (localData.getUpdatedOnInt() < cloudData.getUpdatedOnInt()) {
                        cloudData.setUpdatedOnInt(Calendar.getInstance().getTimeInMillis());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(sharedPrefContractClass.SHARED_PREF_TIME_TABLE_DATA,
                                prefLinker.getString(cloudData));
                        editor.commit();
                        Log.d("Sync","Received");
                    }
                    else if (localData.getUpdatedOnInt() > cloudData.getUpdatedOnInt()){
                        localData.setUpdatedOnInt(Calendar.getInstance().getTimeInMillis());
                        database.child("timetable").setValue(localData);
                        Log.d("Sync","Uploaded");
                    }
                    else {
                        Log.d("Sync", "Data uptoDate");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("Sync Function BITCH !!", "onCancelled: ",databaseError.toException());
                }
            });
            return true;
        }
        return false;
    }

    public ArrayList<Range<Integer>> freeSlot(int day,int from,int to){

        Log.d("DayIndex", "freeSlot: "+day);
        ArrayList<Range<Integer>> ranges = new ArrayList<>();
        Range<Integer> iterator ;
        ArrayList<timeTableElement> tableElements = getDayElements(getDays().get(day));
        int startIterator = from;
        for(int i=0;(i<tableElements.size() && startIterator<=to);i++){
            if(startIterator<tableElements.get(i).getStartTime()
                    && tableElements.get(i).getEndTime()<=to) {
                iterator = Range.create(startIterator,tableElements.get(i).getStartTime());
                ranges.add(iterator);
                startIterator = tableElements.get(i).getEndTime();
            }
            else if(startIterator == tableElements.get(i).getStartTime())
                startIterator = tableElements.get(i).getEndTime();
        }
        if(startIterator<to){
            iterator = Range.create(startIterator,to);
            ranges.add(iterator);
        }
        return ranges;
    }

}
