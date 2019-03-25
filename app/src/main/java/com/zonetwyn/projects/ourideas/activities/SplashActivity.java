package com.zonetwyn.projects.ourideas.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.zonetwyn.projects.ourideas.R;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private ProgressBar progressBar;

    private static String preferenceFile = "PreferenceFile";
    private static String alreadyLaunched = "AlreadyLaunched";

    private int currentProgress = 0;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            currentProgress++;
            progressBar.setProgress(currentProgress);
            // animate
            if (currentProgress != 100) {
                new Handler().postDelayed(runnable, 50);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // store state
                        SharedPreferences preferences = getSharedPreferences(preferenceFile, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(alreadyLaunched, true);
                        editor.apply();

                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 100);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check
        SharedPreferences preferences = getSharedPreferences(preferenceFile, Context.MODE_PRIVATE);
        if (preferences.contains(alreadyLaunched) && preferences.getBoolean(alreadyLaunched, false)) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        progressBar = findViewById(R.id.progressBar);

        // logo animation
        Runnable logoRunnable = new Runnable() {
            @Override
            public void run() {
                logo.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp).duration(800).playOn(logo);
            }
        };

        // progress animation
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInLeft).duration(800).playOn(progressBar);
            }
        };

        // handlers
        Handler logoHandler = new Handler();
        Handler progressHandler = new Handler();

        logoHandler.postDelayed(logoRunnable, 100);
        progressHandler.postDelayed(progressRunnable, 1300);
        Handler handler = new Handler();
        handler.postDelayed(runnable, 2100);
    }
}
