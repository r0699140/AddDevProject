package com.example.timerapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity implements DayAdaptor.ListItemClickListener{
    private RecyclerView recyclerViewDetails;
    private RecyclerView.Adapter adapterDetails;
    private RecyclerView.LayoutManager layoutManagerDetails;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String[][] datasetDetails;

    AppDatabase mDb;
    TimingDao timingDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.dayView);
        recyclerView.setHasFixedSize(true);

       layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DayAdaptor( this);
        recyclerView.setAdapter(adapter);

        mDb = (AppDatabase) AppDatabase.getDatabase(getApplicationContext());
        timingDao = mDb.userDao();

        timingDao.groupDays().observe(this, new Observer<List<GroupedTiming>>() {
            @Override
            public void onChanged(@Nullable List<GroupedTiming> timings) {
                ((DayAdaptor)adapter).setDataset(timings);
            }
        });

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerViewDetails = (RecyclerView) findViewById(R.id.detailView);
            recyclerViewDetails.setHasFixedSize(true);

            layoutManagerDetails = new LinearLayoutManager(this);
            recyclerViewDetails.setLayoutManager(layoutManagerDetails);

            ArrayList<Timing> timings = new ArrayList<Timing>();

            adapterDetails = new DetailAdaptor(timings);
            recyclerViewDetails.setAdapter(adapterDetails);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            timingDao.findByDate(today.getTime()).observe(this, new Observer<List<Timing>>() {
                @Override
                public void onChanged(@Nullable List<Timing> timings) {
                    ((DetailAdaptor)adapterDetails).setDataset(timings);
                }
            });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onListItemClick(int clickedItemIndex) {
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            Intent intent = new Intent(this, DayDetailActivity.class);
            intent.putExtra("date", ((DayAdaptor)adapter).getDataset(clickedItemIndex).date.getTime());
            startActivity(intent);
        }else{
            timingDao.findByDate(((DayAdaptor)adapter).getDataset(clickedItemIndex).date).observe(this, new Observer<List<Timing>>() {
                @Override
                public void onChanged(@Nullable List<Timing> timings) {
                    ((DetailAdaptor)adapterDetails).setDataset(timings);
                }
            });
        }
    }
}
