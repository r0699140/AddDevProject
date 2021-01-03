package com.example.timerapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.timerapp.Database.TimingContract.*;

import androidx.annotation.Nullable;

public class TimingDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "timinglist.db";
    public static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TimingEntry.TABLE_NAME +
            "( " + TimingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TimingEntry.COLUMN_NAME_DURATION + " INTEGER NOT NULL, " +
            TimingEntry.COLUMN_NAME_START + " INTEGER NOT NULL , " +
            TimingEntry.COLUMN_NAME_END + " INTEGER NOT NULL, " +
            TimingEntry.COLUMN_NAME_DATE + " INTEGER NOT NULL);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TimingEntry.TABLE_NAME;

    public TimingDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
