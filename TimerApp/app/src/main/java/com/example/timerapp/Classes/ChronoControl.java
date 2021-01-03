package com.example.timerapp.Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

import androidx.core.content.ContextCompat;

import com.example.timerapp.Service.ChronoService;

public class ChronoControl {
    private boolean running;
    private boolean paused;

    private static final ChronoControl instance = new ChronoControl();

    public static ChronoControl getInstance(){
        return instance;
    }

    public void setReceiver(Context context, BroadcastReceiver receiver){
        IntentFilter filter = new IntentFilter(ChronoService.SetIntent);
        filter.addAction(ChronoService.PauseTimeIntent);
        filter.addAction(ChronoService.StopChronoIntent);
        filter.addAction(ChronoService.SaveIntent);
        context.registerReceiver(receiver, filter);
    }

    public void getTime(Context context){
        Intent sendIntent = new Intent(ChronoService.GetIntent);
        context.sendBroadcast(sendIntent);
    }

    public boolean startChronoService(Context context){
        return startChronoService(-1, context);
    }

    public boolean startChronoService(long time, Context context){
        if(running) return false;

        Intent serviceIntent = new Intent(context, ChronoService.class);

        if(paused){
            if(time > 0) serviceIntent.putExtra(ChronoService.ServiceTime, time);
            paused = false;
        }

        ContextCompat.startForegroundService(context, serviceIntent);
        running = true;
        return true;
    }

    public void pause(Context context){
        if(paused) return;

        Intent sendIntent = new Intent(ChronoService.PauseIntent);
        context.sendBroadcast(sendIntent);
        running = false;
        paused = true;
    }

    public void toggleChrono(Context context){
        if(running){
            pause(context);
        }else{
            startChronoService(context);
        }
    }

    public void stop(Context context){
        Intent sendIntent = new Intent(ChronoService.StopIntent);
        context.sendBroadcast(sendIntent);
        running = false;
        paused = false;
    }

    public boolean isRunning(){
        return running;
    }

    public boolean isPaused() {
        return paused;
    }
}
