package com.example.timerapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timerapp.Classes.ChronoControl;
import com.example.timerapp.R;
import com.example.timerapp.Service.ChronoService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class FabActivity  extends AppCompatActivity {
    protected FloatingActionButton mToggleFab;
    protected FloatingActionButton mStopFab;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case ChronoService.SetIntent:
                    pauseFab();
                    break;
                case ChronoService.PauseTimeIntent:
                    ChronoControl.getInstance().pause(context);
                    resumeFab();
                    break;
                case ChronoService.StopChronoIntent:
                    ChronoControl.getInstance().stop(context);
                    stopFab();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupFab(){
        mToggleFab = findViewById(R.id.mainFab);
        mStopFab = findViewById(R.id.stopFab);

        Animation showFab = AnimationUtils.loadAnimation(this, R.anim.fab_appear);
        mToggleFab.startAnimation(showFab);

        mToggleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChronoControl.getInstance().toggleChrono(getBaseContext());

                if(ChronoControl.getInstance().isRunning()){
                    pauseFab();
                }else{
                    resumeFab();
                }
            }
        });

        mStopFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChronoControl.getInstance().stop(getBaseContext());
                stopFab();
            }
        });

        if(ChronoControl.getInstance().isRunning()){
            pauseFab();
        }else{
            resumeFab();

            if(ChronoControl.getInstance().isPaused()){
                showStop();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ChronoControl.getInstance().setReceiver(this, mReceiver);
        ChronoControl.getInstance().getTime(this);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    protected void showStop(){
        Animation showBtnAni = AnimationUtils.loadAnimation(this, R.anim.show_stop);

        if(mStopFab.getVisibility() == View.INVISIBLE){
            mStopFab.setVisibility(View.VISIBLE);
            mStopFab.startAnimation(showBtnAni);
        }
    }

    protected void pauseFab(){
        mToggleFab.setImageResource(R.drawable.ic_pause);
        showStop();
    }

    protected void resumeFab(){
        mToggleFab.setImageResource(R.drawable.ic_timer);
    }

    protected void stopFab(){
        Animation hideBtnAni = AnimationUtils.loadAnimation(this, R.anim.hide_stop);

        mStopFab.setVisibility(View.INVISIBLE);
        mStopFab.startAnimation(hideBtnAni);
        mToggleFab.setImageResource(R.drawable.ic_timer);
    }
}
