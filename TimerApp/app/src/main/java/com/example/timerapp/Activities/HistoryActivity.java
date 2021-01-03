package com.example.timerapp.Activities;

import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;

import com.example.timerapp.R;

import java.util.Objects;

public class HistoryActivity extends FabActivity{
    public static boolean mMasterDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mMasterDetail = findViewById(R.id.masterDetail) != null;

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupFab();
    }
}
