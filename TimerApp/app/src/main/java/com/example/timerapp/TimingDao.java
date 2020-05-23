package com.example.timerapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Dao
public interface TimingDao {
    @Query("SELECT * FROM timing")
    LiveData<List<Timing>> getAll();

    @Query("SELECT SUM(duration) as totalDuration, date FROM timing GROUP BY date ORDER BY date DESC")
    LiveData<List<GroupedTiming>> groupDays();

    @Query("Select * FROM timing WHERE date = :date")
    LiveData<List<Timing>> findByDate(Date date);

    @Query("Delete FROM timing WHERE date = :date")
    void deleteByDate(Date date);

    @Query("Delete FROM timing")
    void deleteTimings();

    @Insert
    void insertAll(Timing... timings);

    @Delete
    void delete(Timing timing);
}
