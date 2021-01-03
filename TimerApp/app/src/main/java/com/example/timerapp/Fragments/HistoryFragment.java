package com.example.timerapp.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.timerapp.Activities.DayDetailActivity;
import com.example.timerapp.Adaptors.DayAdaptor;
import com.example.timerapp.Database.GroupedTiming;
import com.example.timerapp.Database.Timer;
import com.example.timerapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.provider.BaseColumns._ID;
import static com.example.timerapp.Activities.HistoryActivity.mMasterDetail;
import static com.example.timerapp.Database.TimingContract.TimingEntry.COLUMN_NAME_DATE;
import static com.example.timerapp.Database.TimingContract.TimingEntry.CONTENT_URI;

public class HistoryFragment extends Fragment implements DayAdaptor.ListItemClickListener {

    public HistoryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private DayAdaptor mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.dayView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        mAdapter = new DayAdaptor( this);
        recyclerView.setAdapter(mAdapter);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);

        fillAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        fillAdapter(mAdapter);
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
        Date date = mAdapter.getDataset(clickedItemIndex).date;
        int duration = mAdapter.getDataset(clickedItemIndex).totalDuration;

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

    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final GroupedTiming item = mAdapter.getItem(viewHolder.getAdapterPosition());

            String[] selectArgs = {String.valueOf(item.date.getTime())};
            getContext().getContentResolver().delete(CONTENT_URI, COLUMN_NAME_DATE + " = ?", selectArgs);

            mAdapter.remove(viewHolder.getAdapterPosition());
        }
    };

}