package com.example.android.timepower.custom.objects;

import java.sql.Time;

/**
 * Created by root on 6/12/17.
 */

public class timeTableElement {

    String header,
            subHeader,
            toTimeType,
            fromTimeType,
            day;
    int startTime,
            endTime;

    public timeTableElement(){

    }

    public timeTableElement(String header,
                            String subHeader,
                            int startTime,
                            int endTime){
        this.header = header;
        this.subHeader = subHeader;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public timeTableElement(timeTableElement tableElement){
        this.header = tableElement.getHeader();
        this.subHeader = tableElement.getSubHeader();
        this.startTime = tableElement.getStartTime();
        this.endTime = tableElement.getEndTime();
    }

    public String getHeader() {
        return header;
    }

    public String getSubHeader() {
        return subHeader;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public String getFromTimeType() {
        return fromTimeType;
    }

    public String getToTimeType() {
        return toTimeType;
    }

    public void setToTimeType(String toTimeType) {
        this.toTimeType = toTimeType;
    }

    public void setFromTimeType(String fromTimeType) {
        this.fromTimeType = fromTimeType;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
