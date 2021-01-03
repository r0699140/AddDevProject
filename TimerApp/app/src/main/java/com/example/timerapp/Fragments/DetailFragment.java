package com.example.timerapp.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.timerapp.Database.Timer;
import com.example.timerapp.Database.TimingContract;
import com.example.timerapp.Adaptors.DetailAdaptor;
import com.example.timerapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.provider.BaseColumns._ID;
import static com.example.timerapp.Activities.HistoryActivity.mMasterDetail;
import static com.example.timerapp.Database.TimingContract.TimingEntry.COLUMN_NAME_DATE;
import static com.example.timerapp.Database.TimingContract.TimingEntry.COLUMN_NAME_START;
import static com.example.timerapp.Database.TimingContract.TimingEntry.CONTENT_URI;

public class DetailFragment extends Fragment {
    private Cursor mCursor;
    private DetailAdaptor mAdapter;

    private View mRootView;

    private TextView mDateDisplay;
    private TextView mTimeDisplay;

    private String mDateStr;
    private String mDurationStr;

    private Long mDate;
    private int mDuration;


    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        readData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        RecyclerView recyclerView = mRootView.findViewById(R.id.detailView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new DetailAdaptor();

        recyclerView.setAdapter(mAdapter);
        mDateDisplay = mRootView.findViewById(R.id.dateDisplay);
        mTimeDisplay = mRootView.findViewById(R.id.timeDisplay);
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);

        if(!mMasterDetail){
            Intent intent = getActivity().getIntent();
            long date;
            if(intent.hasExtra("date") && intent.hasExtra("duration")) {
                date = intent.getLongExtra("date", 0);
                int duration = intent.getIntExtra("duration", 0);
                setDate(date, duration);

                String[] selectArgs = { Long.toString(date) };

                mCursor = getActivity().getContentResolver().query(CONTENT_URI, null, COLUMN_NAME_DATE + " = ?", selectArgs, COLUMN_NAME_START + " DESC");
            }
        }

        displayDate();
        readData();
        return mRootView;
    }

    public void setCursor(Cursor cursor){
        mCursor = cursor;
    }

    public void readData(){
        if(mCursor == null) return;
        mCursor.moveToPosition(-1);
        ArrayList<Timer> items = new ArrayList<>();

        while(mCursor.moveToNext()) {
            int itemId = (int) mCursor.getLong(mCursor.getColumnIndexOrThrow(TimingContract.TimingEntry._ID));
            int duration = mCursor.getInt(mCursor.getColumnIndexOrThrow(TimingContract.TimingEntry.COLUMN_NAME_DURATION));

            Date startTime = new Date(mCursor.getLong(mCursor.getColumnIndexOrThrow(TimingContract.TimingEntry.COLUMN_NAME_START)));
            Date endTime = new Date(mCursor.getLong(mCursor.getColumnIndexOrThrow(TimingContract.TimingEntry.COLUMN_NAME_END)));

            Date date = new Date(mCursor.getLong(mCursor.getColumnIndexOrThrow(COLUMN_NAME_DATE)));
            items.add(new Timer(itemId, duration, startTime, endTime, date));
        }

        mAdapter.setDataset(items);
    }

    public void setDate(Long date, int duration){
        mDate = date;
        mDuration = duration;
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd/MM/yyyy", Locale.getDefault());
        mDateStr = dateFormat.format(date);

        int hours = duration / 60;
        int minutes = duration % 60;

        String strHours = hours > 9 ? String.valueOf(hours) : "0" + hours;
        String strMinutes = minutes > 9 ? String.valueOf(minutes) : "0" + minutes;
        mDurationStr = String.format("-  %s:%s", strHours, strMinutes);
    }

    public void displayDate(){
        mTimeDisplay.setText(mDurationStr);
        mDateDisplay.setText(mDateStr);
    }

    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final Timer item = mAdapter.getItem(viewHolder.getAdapterPosition());
            mDuration -= item.duration;
            setDate(mDate, mDuration);
            displayDate();

            String[] selectArgs = {String.valueOf(item.uid)};
            getContext().getContentResolver().delete(CONTENT_URI, _ID + " = ?", selectArgs);

            mAdapter.remove(viewHolder.getAdapterPosition());
        }
    };
}