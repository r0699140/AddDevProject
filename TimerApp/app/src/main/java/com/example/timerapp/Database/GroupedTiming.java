package com.example.timerapp.Database;
import java.util.Date;

public class GroupedTiming {
    public int totalDuration;
    public Date date;

    public GroupedTiming(int totalDuration, Date date){
        this.totalDuration = totalDuration;
        this.date = date;
    }
}
