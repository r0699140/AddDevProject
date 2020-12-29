package com.example.timerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.timerapp.HistoryActivity.mMasterDetail;
import static com.example.timerapp.TimingContract.TimingEntry.COLUMN_NAME_DATE;
import static com.example.timerapp.TimingContract.TimingEntry.CONTENT_URI;

public class DetailFragment extends Fragment {
    private Cursor mCursor;
    private DetailAdaptor mAdapter;

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
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.detailView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new DetailAdaptor();

        recyclerView.setAdapter(mAdapter);
        //Log.d("detail", "view made");

        if(!mMasterDetail){
            Intent intent = getActivity().getIntent();
            long date;
            if(intent.hasExtra("date")) {
                date = intent.getLongExtra("date", 0);
//                Log.d("detail", new Date(date).toString());
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd/MM/yyyy");

                TextView dateDisplay = rootView.findViewById(R.id.dateDisplay);
                String dateStr = dateFormat.format(date);
                dateDisplay.setText(dateStr);
                Log.d("detail", dateStr);

                String[] selectArgs = { Long.toString(date) };

                mCursor = getActivity().getContentResolver().query(CONTENT_URI, null, COLUMN_NAME_DATE + " = ?", selectArgs, null);
            }
        }

        readData();
        return rootView;
    }

    public void setCursor(Cursor cursor){
        mCursor = cursor;
    }

    public void readData(){
        if(mCursor == null) return;
        mCursor.moveToFirst();
        Log.d("detail", "reading data");

        ArrayList<Timer> items = new ArrayList<>();

        while(mCursor.moveToNext()) {
            int itemId = (int) mCursor.getLong(mCursor.getColumnIndexOrThrow(TimingContract.TimingEntry._ID));
            int duration = mCursor.getInt(mCursor.getColumnIndexOrThrow(TimingContract.TimingEntry.COLUMN_NAME_DURATION));

            Date startTime = new Date(mCursor.getLong(mCursor.getColumnIndexOrThrow(TimingContract.TimingEntry.COLUMN_NAME_START)));
            Date endTime = new Date(mCursor.getLong(mCursor.getColumnIndexOrThrow(TimingContract.TimingEntry.COLUMN_NAME_END)));

            Date date = new Date(mCursor.getLong(mCursor.getColumnIndexOrThrow(COLUMN_NAME_DATE)));
            items.add(new Timer(itemId, duration, startTime, endTime, date));

            Log.d("detail", items.get(0).date.toString());
        }

        mAdapter.setDataset(items);
    }
}