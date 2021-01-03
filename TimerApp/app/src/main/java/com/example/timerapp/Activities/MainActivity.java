package com.example.timerapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.example.timerapp.Classes.ChronoControl;
import com.example.timerapp.Fragments.DetailFragment;
import com.example.timerapp.R;
import com.example.timerapp.Database.TimingContract;
import com.example.timerapp.Service.ChronoService;

import java.util.Calendar;
import java.util.Objects;

import static com.example.timerapp.Activities.HistoryActivity.mMasterDetail;
import static com.example.timerapp.Database.TimingContract.TimingEntry.COLUMN_NAME_DATE;
import static com.example.timerapp.Database.TimingContract.TimingEntry.COLUMN_NAME_START;
import static com.example.timerapp.Database.TimingContract.TimingEntry.CONTENT_URI;


public class MainActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private Button toggle_btn;
    private Button stop_btn;
    private boolean running;
    private long pauseOffset;

    private ChronoControl chronoControl;

    private final BroadcastReceiver mChronoServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long time = intent.getLongExtra(ChronoService.TimeIntent, SystemClock.elapsedRealtime());

            switch (Objects.requireNonNull(intent.getAction())) {
                case ChronoService.SetIntent:
                    startChrono(time);
                    break;
                case ChronoService.PauseTimeIntent:
                    setPaused(time);
                    break;
                case ChronoService.StopChronoIntent:
                    stopChrono();
                    break;
                case ChronoService.SaveIntent:
                    loadDay();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mMasterDetail = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        chronometer = findViewById(R.id.chronometer);
        toggle_btn = findViewById(R.id.toggle_btn);
        stop_btn = findViewById(R.id.stop_btn);
        chronoControl = ChronoControl.getInstance();

        loadDay();
    }

    public void loadDay(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        String[] selectArgs = {Long.toString(today.getTime().getTime())};

        Cursor cursor = getContentResolver().query(CONTENT_URI, null, COLUMN_NAME_DATE + " = ?", selectArgs, COLUMN_NAME_START + " DESC");

        int duration = 0;
        while(cursor.moveToNext()){
            duration += cursor.getInt(cursor.getColumnIndexOrThrow(TimingContract.TimingEntry.COLUMN_NAME_DURATION));
        }
        cursor.moveToFirst();

        FragmentManager fMan = this.getSupportFragmentManager();
        DetailFragment frag = ((DetailFragment)fMan.findFragmentById(R.id.homeFrag));
        frag.setCursor(cursor);
        frag.setDate(today.getTime().getTime(), duration);
        frag.displayDate();
        frag.readData();
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


    @Override
    protected void onResume() {
        super.onResume();
        chronoControl.setReceiver(this, mChronoServiceReceiver);

        chronoControl.getTime(this);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mChronoServiceReceiver);

        super.onPause();
    }

    public void startChrono(){
        startChrono(SystemClock.elapsedRealtime() - pauseOffset);
    }

    public void startChrono(long time){
        toggle_btn.setText(R.string.btn_pause);
        findViewById(R.id.stop_btn).setEnabled(true);

        chronometer.start();
        chronometer.setBase(time);

        chronoControl.startChronoService(time, this);
        running = true;
    }

    public void setPaused(long time){
        chronometer.setBase(time);
        findViewById(R.id.stop_btn).setEnabled(true);
        pauseChrono();
    }

    public void pauseChrono(){
        toggle_btn.setText(R.string.btn_start);
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();

        chronoControl.pause(this);
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
        chronoControl.stop(this);
        v.setEnabled(false);
        stopChrono();
    }
}
