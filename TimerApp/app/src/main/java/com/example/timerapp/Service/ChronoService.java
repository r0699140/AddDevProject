package com.example.timerapp.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.timerapp.Activities.MainActivity;
import com.example.timerapp.App;
import com.example.timerapp.Database.TimingContract;
import com.example.timerapp.Database.TimingDBHelper;
import com.example.timerapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.timerapp.Database.TimingContract.TimingEntry.CONTENT_URI;

public class ChronoService extends Service {
    public static final String ServiceTime = "SetChronoService";

    private long startTime;
    private long pauseTime;
    private long pausedOn;
    private boolean running;
    private Date startDate;

    private Timer updateNotification;
    private TimerTask updateTask;

    @Override
    public void onCreate() {
        super.onCreate();
        startTime = 0;
        pauseTime = 0;
        startDate = null;

        TimingDBHelper dbHelper = new TimingDBHelper(getApplicationContext());
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        IntentFilter filter = new IntentFilter("com.example.timerapp.GETTIME");
        filter.addAction("com.example.timerapp.PAUSE");
        filter.addAction("com.example.timerapp.STOP");
        filter.addAction("com.example.timerapp.PAUSEALL");
        filter.addAction("com.example.timerapp.STARTALL");
        filter.addAction("com.example.timerapp.STOPALL");
        registerReceiver(broadcastReceiver, filter);

        updateNotification = new Timer();
        updateTask = new TimerTask() {
            @Override
            public void run() {
                if (running) {
                    setNotification(timeToString(SystemClock.elapsedRealtime() - startTime));
                } else {
                    long minutesPassed = (SystemClock.elapsedRealtime() - pausedOn) / 60000;

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    int prefMin = prefs.getInt("auto_start_min", 0);

                    if (prefMin > 0 && minutesPassed >= prefMin) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction("com.example.timerapp.SETTIME");
                        sendIntent.putExtra("com.example.timerapp.TIME", startTime);
                        sendBroadcast(sendIntent);

                        startChrono();
                    }
                }
            }
        };
        updateNotification.schedule(updateTask, 1000L, 1000L);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startChrono();

        if(intent.hasExtra(ServiceTime))
            startTime = intent.getLongExtra(ServiceTime, SystemClock.elapsedRealtime());

        if(startDate == null)
            startDate = new Date();

        setNotification(timeToString(SystemClock.elapsedRealtime() - startTime));
        running = true;

        return START_NOT_STICKY;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent();

            switch (Objects.requireNonNull(intent.getAction())) {
                case "com.example.timerapp.GETTIME":
                    if (running) {
                        sendIntent.setAction("com.example.timerapp.SETTIME");
                    } else {
                        sendIntent.setAction("com.example.timerapp.PAUSETIME");
                        startTime = SystemClock.elapsedRealtime() - pauseTime;
                    }

                    sendIntent.putExtra("com.example.timerapp.TIME", startTime);
                    sendBroadcast(sendIntent);

                    break;
                case "com.example.timerapp.PAUSE":
                    pauseChrono();
                    break;
                case "com.example.timerapp.STARTALL":
                    startChrono();

                    setNotification(timeToString(SystemClock.elapsedRealtime() - startTime));

                    sendIntent.setAction("com.example.timerapp.SETTIME");
                    sendIntent.putExtra("com.example.timerapp.TIME", startTime);
                    sendBroadcast(sendIntent);
                    break;
                case "com.example.timerapp.PAUSEALL":
                    pauseChrono();

                    setNotification(timeToString(SystemClock.elapsedRealtime() - startTime));

                    sendIntent.setAction("com.example.timerapp.PAUSETIME");
                    startTime = SystemClock.elapsedRealtime() - pauseTime;
                    sendIntent.putExtra("com.example.timerapp.TIME", startTime);
                    sendBroadcast(sendIntent);
                    break;
                case "com.example.timerapp.STOPALL":
                    sendIntent.setAction("com.example.timerapp.STOP_CHRONO");
                    sendBroadcast(sendIntent);
                case "com.example.timerapp.STOP":
                    stopChrono();
                    break;
            }
        }
    };

    private void startChrono(){
        running = true;
        setNotification(timeToString(SystemClock.elapsedRealtime() - startTime));
        startTime = SystemClock.elapsedRealtime() - pauseTime;
    }

    private void pauseChrono(){
        running = false;
        setNotification(timeToString(SystemClock.elapsedRealtime() - startTime));
        pauseTime = SystemClock.elapsedRealtime() - startTime;
        pausedOn = SystemClock.elapsedRealtime();
    }

    private void setNotification(String input){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationIntent, 0);

        String toggleBtn;
        Intent toggleIntent = new Intent();
        if(running){
            toggleBtn = getString(R.string.btn_pause);
            toggleIntent = new Intent("com.example.timerapp.PAUSEALL");
        }else{
            toggleBtn = getString(R.string.btn_start);
            toggleIntent = new Intent("com.example.timerapp.STARTALL");
        }

        PendingIntent toggleAction = PendingIntent.getBroadcast(this, 0, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent("com.example.timerapp.STOPALL");
        PendingIntent stopAction = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("Timer Running")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentIntent(pendingIntent)
                .addAction(R.mipmap.ic_launcher, toggleBtn, toggleAction)
                .addAction(R.mipmap.ic_launcher, "Stop", stopAction)
                .build();

        startForeground(1, notification);
    }

    private String timeToString(long time){
        time = time/1000;

        long hours = time/(60*60);
        time %= (60*60);

        long minutes = time/60;
        time %= 60;

        String strTime = "";

        if(hours > 0)
            strTime += (hours <= 9 ? "0" + hours : hours) + ":";

        strTime += (minutes <= 9 ? "0" + minutes : minutes) + ":";
        strTime += time <= 9 ? "0" + time : time;

        return strTime;
    }

    private void stopChrono(){
        if(!running)
            startTime = SystemClock.elapsedRealtime() - pauseTime;

        int minPassed = (int)((SystemClock.elapsedRealtime() - startTime)/60000);


        if(minPassed > 0){
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            ContentValues newValues = new ContentValues();
            newValues.put(TimingContract.TimingEntry.COLUMN_NAME_DURATION, minPassed);
            newValues.put(TimingContract.TimingEntry.COLUMN_NAME_START, startDate.getTime());
            newValues.put(TimingContract.TimingEntry.COLUMN_NAME_END, new Date().getTime());
            newValues.put(TimingContract.TimingEntry.COLUMN_NAME_DATE, c.getTime().getTime());

            getContentResolver().insert(CONTENT_URI, newValues);
        }

        stopForeground(true);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        updateTask.cancel();

        updateNotification.cancel();
        updateNotification.purge();

        unregisterReceiver(broadcastReceiver);
    }
}

