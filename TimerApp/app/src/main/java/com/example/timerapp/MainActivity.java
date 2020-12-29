package com.example.timerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.timerapp.HistoryActivity.mMasterDetail;
import static com.example.timerapp.TimingContract.TimingEntry.COLUMN_NAME_DATE;
import static com.example.timerapp.TimingContract.TimingEntry.CONTENT_URI;


public class MainActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private Button toggle_btn;
    private boolean running;
    private long pauseOffset;

    BroadcastReceiver chronoServiceReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mMasterDetail = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        chronometer = findViewById(R.id.chronometer);
        toggle_btn = findViewById(R.id.toggle_btn);
        chronoServiceReceiver = new ChronoMeterReceiver();

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        String[] selectArgs = {Long.toString(today.getTime().getTime())};
        Cursor cursor = getContentResolver().query(CONTENT_URI, null, COLUMN_NAME_DATE + " = ?", selectArgs, null);

        FragmentManager fMan = this.getSupportFragmentManager();

        ((DetailFragment)fMan.findFragmentById(R.id.homeFrag)).setCursor(cursor);

        Log.d("detail", (fMan == null) ? "null" : "not null");
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter("com.example.timerapp.SETTIME");
        filter.addAction("com.example.timerapp.PAUSETIME");
        filter.addAction("com.example.timerapp.STOP_CHRONO");
        registerReceiver(chronoServiceReceiver, filter);

        Intent sendIntent = new Intent("com.example.timerapp.GETTIME");
        sendBroadcast(sendIntent);

        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(chronoServiceReceiver);
    }

    private void startServiceChrono(){
        Intent serviceIntent = new Intent(this, ChronoService.class);
        serviceIntent.putExtra(ChronoService.ServiceTime, chronometer.getBase());
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_history:
                intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startChrono(){
        startChrono(SystemClock.elapsedRealtime() - pauseOffset);
    }

    public void startChrono(long time){
        toggle_btn.setText(R.string.btn_pause);
        findViewById(R.id.stop_btn).setEnabled(true);

        chronometer.start();
        chronometer.setBase(time);
        startServiceChrono();
        running = true;
    }

    public void updatePaused(long time){
        chronometer.setBase(time);
        toggle_btn.setText(R.string.btn_start);
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
        findViewById(R.id.stop_btn).setEnabled(true);

        running = false;
    }

    public void pauseChrono(){
        toggle_btn.setText(R.string.btn_start);
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();

        Intent sendIntent = new Intent("com.example.timerapp.PAUSE");
        sendBroadcast(sendIntent);
        running = false;
    }

    public void toggleChrono(View v){
        if(running){
            pauseChrono();
        }else{
            startChrono();
        }
    }

    public void stopChrono(){
        chronometer.setBase(SystemClock.elapsedRealtime());
        toggle_btn.setText(R.string.btn_start);

        chronometer.stop();
        pauseOffset = 0;
        running = false;
    }


    public void stopChrono(View v){
        Intent sendIntent = new Intent("com.example.timerapp.STOP");
        v.setEnabled(false);
        sendBroadcast(sendIntent);

        stopChrono();
    }
}
