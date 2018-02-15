package com.example.android.timepower.custom.objects;

/**
 * Created by root on 2/15/18.
 */

public class addSlot {
    String from;
    String to;
    String day;
    int start;
    int end;

    public addSlot(){}

    public void setDay(String day) {
        this.day = day;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDay() {
        return day;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
