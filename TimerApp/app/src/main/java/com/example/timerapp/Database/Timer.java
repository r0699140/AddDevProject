package com.example.timerapp.Database;

import java.util.Calendar;
import java.util.Date;

public class Timer {
    public int uid;
    public int duration;

    public Date date;
    public Date endTime;
    public Date startTime;

    public Timer(int uid, int duration, Date startTime, Date endTime, Date date) {
        this.uid = uid;
        this.duration = duration;
        this.endTime = endTime;
        this.startTime = startTime;
        this.date = date;
    }
}
