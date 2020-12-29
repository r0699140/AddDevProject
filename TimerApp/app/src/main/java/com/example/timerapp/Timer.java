package com.example.timerapp;

import java.util.Calendar;
import java.util.Date;

public class Timer {
    public int uid;
    public int duration;

    public Date date;
    public Date endTime;
    public Date startTime;

    public Timer() {
        this.duration = 0;
        this.startTime = new Date();
        this.endTime = new Date();
        this.date = new Date();
    }

    public Timer(int uid, int duration, Date startTime, Date endTime, Date date) {
        this.uid = uid;
        this.duration = duration;
        this.endTime = endTime;
        this.startTime = startTime;
        this.date = date;
    }

    public Timer(int duration, Date startTime, Date endTime) {
        this.duration = duration;
        this.endTime = endTime;
        this.startTime = startTime;

        Calendar c = Calendar.getInstance();
        c.setTime(endTime);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        this.date = c.getTime();
    }


}
