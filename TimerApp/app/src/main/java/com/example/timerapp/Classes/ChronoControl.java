package com.example.timerapp.Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.timerapp.Service.ChronoMeterReceiver;
import com.example.timerapp.Service.ChronoService;

public class ChronoControl {
    private static boolean running;

    private BroadcastReceiver mChronoServiceReceiver;
    private Context mCurrentContext;

    public ChronoControl(Context context){
        mChronoServiceReceiver = new ChronoMeterReceiver();
        mCurrentContext = context;

        IntentFilter filter = new IntentFilter("com.example.timerapp.SETTIME");
        filter.addAction("com.example.timerapp.PAUSETIME");
        filter.addAction("com.example.timerapp.STOP_CHRONO");
        mCurrentContext.registerReceiver(mChronoServiceReceiver, filter);

        Intent sendIntent = new Intent("com.example.timerapp.GETTIME");
        mCurrentContext.sendBroadcast(sendIntent);
    }

    public boolean startChronoService(){
        if(running) return false;

        Intent serviceIntent = new Intent(mCurrentContext, ChronoService.class);
        serviceIntent.putExtra(ChronoService.ServiceTime, SystemClock.elapsedRealtime());
        ContextCompat.startForegroundService(mCurrentContext, serviceIntent);

        return true;
    }

    public void pause(){
        Intent sendIntent = new Intent("com.example.timerapp.PAUSE");
        mCurrentContext.sendBroadcast(sendIntent);
        running = false;
    }

    public void toggleChrono(){
        if(running){
            pause();
        }else{
            startChronoService();
        }
    }

    public void stop(){
        Intent sendIntent = new Intent("com.example.timerapp.STOP");
        mCurrentContext.sendBroadcast(sendIntent);
        running = false;
    }

    public void destroy(){
        mCurrentContext.unregisterReceiver(mChronoServiceReceiver);
    }
}
