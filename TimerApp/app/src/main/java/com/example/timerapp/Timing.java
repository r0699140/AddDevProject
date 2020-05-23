package com.example.timerapp;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Entity
public class Timing {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    public int duration;

    public Date date;
    public Date endTime;
    public Date startTime;

    public Timing() {
        this.duration = 0;
        this.startTime = new Date();
        this.endTime = new Date();
        this.date = new Date();
    }

    public Timing(int duration, Date startTime, Date endTime) {
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

