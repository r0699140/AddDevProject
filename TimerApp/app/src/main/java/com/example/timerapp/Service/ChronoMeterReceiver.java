package com.example.timerapp.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.example.timerapp.Activities.MainActivity;

import java.util.Objects;

public class ChronoMeterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long time = intent.getLongExtra("com.example.timerapp.TIME", SystemClock.elapsedRealtime());

        switch (Objects.requireNonNull(intent.getAction())) {
            case "com.example.timerapp.SETTIME":
                ((MainActivity) context).startChrono(time);
                break;
            case "com.example.timerapp.PAUSETIME":
                ((MainActivity) context).updatePaused(time);
                break;
            case "com.example.timerapp.STOP_CHRONO":
                ((MainActivity) context).stopChrono();
                break;
        }

    }
}
