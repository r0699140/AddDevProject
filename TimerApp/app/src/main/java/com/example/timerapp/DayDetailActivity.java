package com.example.timerapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DayDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private AppDatabase mDb;
    private TimingDao timingDao;

    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_detail);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.detailView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new DetailAdaptor();

        mDb = (AppDatabase) AppDatabase.getDatabase(getApplicationContext());
        timingDao = mDb.userDao();

        Calendar today = Calendar.getInstance();

        Intent intent = getIntent();
        if(intent.hasExtra("date")) {
            date = new Date(intent.getLongExtra("date", 0));
            today.setTime(date);
        }

        timingDao.findByDate(today.getTime()).observe(this, new Observer<List<Timing>>() {
            @Override
            public void onChanged(@Nullable List<Timing> timings) {
                ((DetailAdaptor)mAdapter).setDataset(timings);
            }
        });

        recyclerView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        if (item.getItemId() == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Are you sure?");
            builder.setMessage("Are you sure you want to delete all the records of this day?");
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteAsync(timingDao).execute();
                            finish();
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DeleteAsync extends AsyncTask<Void, Void, Void> {

        TimingDao mAsyncTimingDao;

        DeleteAsync(TimingDao dao) {
            this.mAsyncTimingDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTimingDao.deleteByDate(date);
            return null;
        }
    }
}
