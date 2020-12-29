package com.example.timerapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.timerapp.TimingContract.TimingEntry.CONTENT_URI;
import static com.example.timerapp.TimingContract.TimingEntry.TIMING_PATH;
import static com.example.timerapp.TimingContract.TimingEntry.ALL_TIMING;
import static com.example.timerapp.TimingContract.TimingEntry.TIMING_GROUP;
import static com.example.timerapp.TimingContract.TimingEntry.TIMING_ID;

public class TimingContent extends ContentProvider {
    private TimingDBHelper mdbHelper;
    public static final String AUTHORITY = "com.example.timerapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, TIMING_PATH, ALL_TIMING);
        uriMatcher.addURI(AUTHORITY, TIMING_PATH + "/group", TIMING_GROUP);
        uriMatcher.addURI(AUTHORITY, TIMING_PATH + "/#", TIMING_ID);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mdbHelper = new TimingDBHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String extraString) {
        final SQLiteDatabase db = mdbHelper.getReadableDatabase();
        Cursor data;

        int match = uriMatcher.match(uri);

        switch (match){
            case ALL_TIMING:
            case TIMING_ID:
                data = db.query(TimingContract.TimingEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, extraString);
                break;
            case TIMING_GROUP:
                data = db.rawQuery("SELECT SUM(duration) as totalDuration, date FROM Timing GROUP BY date ORDER BY date DESC;", null);
                break;
            default:
                throw  new UnsupportedOperationException("Unknown URI: " + uri);
        }

        return data;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) { return null; }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mdbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        Uri returnUri = null;

        switch (match){
            case ALL_TIMING:
                long id = db.insert(TimingContract.TimingEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                }else{
                    throw new SQLException("Failed to insert into row: " + uri);
                }
                break;
            default:
                throw  new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int match = uriMatcher.match(uri);
        int deleted = 0;
        final SQLiteDatabase db = mdbHelper.getWritableDatabase();

        switch (match){
            case ALL_TIMING:
                db.delete(TimingContract.TimingEntry.TABLE_NAME, s, strings);
                break;
            case TIMING_ID:
                deleted = db.delete(TimingContract.TimingEntry.TABLE_NAME, s, strings);
                break;
            default:
                throw  new UnsupportedOperationException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) { return 0; }
}
