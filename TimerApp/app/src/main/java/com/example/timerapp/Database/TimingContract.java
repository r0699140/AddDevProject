package com.example.timerapp.Database;

import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.timerapp.Database.TimingContent.BASE_CONTENT_URI;

public class TimingContract {
    private TimingContract(){}

    public static class TimingEntry implements BaseColumns{
        public static final String TIMING_PATH = "timing";

        public static final int ALL_TIMING = 100;
        public static final int TIMING_ID = 101;
        public static final int TIMING_GROUP = 102;

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TIMING_PATH).build();

        public static final String TABLE_NAME = "Timing";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_START = "startTime";
        public static final String COLUMN_NAME_END = "endTime";
        public static final String COLUMN_NAME_DATE = "date";
    }
}
