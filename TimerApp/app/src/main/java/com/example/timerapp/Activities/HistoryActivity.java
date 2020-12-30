package com.example.timerapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.example.timerapp.Service.ChronoService;
import com.example.timerapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class HistoryActivity extends AppCompatActivity{
    public static boolean mMasterDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mMasterDetail = findViewById(R.id.masterDetail) != null;

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                Intent serviceIntent = new Intent(getBaseContext(), ChronoService.class);
                serviceIntent.putExtra(ChronoService.ServiceTime, SystemClock.elapsedRealtime());
                ContextCompat.startForegroundService(getBaseContext(), serviceIntent);*/
            }
        });
    }
}
