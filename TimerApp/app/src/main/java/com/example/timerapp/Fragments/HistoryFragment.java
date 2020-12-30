package com.example.timerapp.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.timerapp.Activities.DayDetailActivity;
import com.example.timerapp.Adaptors.DayAdaptor;
import com.example.timerapp.Database.GroupedTiming;
import com.example.timerapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.timerapp.Activities.HistoryActivity.mMasterDetail;
import static com.example.timerapp.Database.TimingContract.TimingEntry.COLUMN_NAME_DATE;
import static com.example.timerapp.Database.TimingContract.TimingEntry.CONTENT_URI;

public class HistoryFragment extends Fragment implements DayAdaptor.ListItemClickListener {

    public HistoryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private DayAdaptor adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.dayView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new DayAdaptor( this);
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        fillAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        fillAdapter(adapter);
    }

    void fillAdapter(DayAdaptor adapter){
        Uri groupURI = CONTENT_URI.buildUpon().appendPath("group").build();

        Cursor cursor = getContext().getContentResolver().query(groupURI, null, null, null, null);
        ArrayList<GroupedTiming> days = new ArrayList<>();

        while(cursor.moveToNext()) {
            int duration = cursor.getInt(0);
            Date date = new Date(cursor.getLong(1));
            days.add(new GroupedTiming(duration, date));

            SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd/MM/yyyy", Locale.getDefault());
            String dateStr = dateFormat.format(date);
            Log.d("history", dateStr + " - " + duration);
        }

        cursor.close();
        adapter.setDataset(days);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Date date = adapter.getDataset(clickedItemIndex).date;
        int duration = adapter.getDataset(clickedItemIndex).totalDuration;

        if(!mMasterDetail){
            Intent intent = new Intent(getContext(), DayDetailActivity.class);
            intent.putExtra("date", date.getTime());
            intent.putExtra("duration", duration);
            startActivity(intent);
        }else{
            FragmentManager fMan = getActivity().getSupportFragmentManager();
            DetailFragment dFrag = new DetailFragment();

            String[] selectArgs = { Long.toString(date.getTime()) };
            Cursor cursor = getActivity().getContentResolver().query(CONTENT_URI, null, COLUMN_NAME_DATE + " = ?", selectArgs, null);

            dFrag.setCursor(cursor);
            dFrag.setDate(date.getTime(), duration);

            fMan.beginTransaction()
                    .replace(R.id.dayFrag, dFrag)
                    .commit();
        }
    }

}