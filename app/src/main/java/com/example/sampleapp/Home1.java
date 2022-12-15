package com.example.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class Home1 extends AppCompatActivity {

    public static Button btnPlay, btnMultiPlayer;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home1);

        casioMusic();
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnMultiPlayer = (Button) findViewById(R.id.btnMultiPlayer);

        btnPlay.setOnClickListener(v -> {
            try {
                btnPlayClick();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        btnMultiPlayer.setOnClickListener(v -> {
            try {
                btnMultiPlayerClick();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void casioMusic()
    {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        boolean gameSound = prefs.getBoolean("pref_cb_sound", true);
        if(gameSound){
            stop();
        }

        mp = MediaPlayer
                .create(Home1.this, R.raw.casino);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });
    }

    private void btnPlayClick() throws InterruptedException {
        {
           SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            boolean gameSound = prefs.getBoolean("pref_cb_sound", true);
            if(gameSound){
                stop();
            }

            mp = MediaPlayer
                    .create(Home1.this, R.raw.btn_click);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });
        }
        Intent intent = new Intent(Home1.this, MainActivity.class);
        startActivity(intent);
    }

    private void btnMultiPlayerClick() throws InterruptedException {
        {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            boolean gameSound = prefs.getBoolean("pref_cb_sound", true);
            if(gameSound){
                stop();
            }

            mp = MediaPlayer
                    .create(Home1.this, R.raw.btn_click);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });
        }
        Intent intent = new Intent(Home1.this, Multiplayer.class);
        startActivity(intent);
    }

    public void stop() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

}