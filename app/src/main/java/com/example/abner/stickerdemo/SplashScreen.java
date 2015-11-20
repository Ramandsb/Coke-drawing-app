package com.example.abner.stickerdemo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

public class SplashScreen extends AppCompatActivity {
    private ProgressBar progressBar;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = (ProgressBar) findViewById(R.id.progress);
    }

    @Override
    public void onStart() {
        // When the aplication starts, show the progressbar for 2 seconds. After that, execute loadHomeActivity runnable.
        long mStartTime = 0;
        if (mStartTime == 0L) {
            mStartTime = System.currentTimeMillis();
            mHandler.removeCallbacks(loadHomeActivity);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.setProgress(0);
            mHandler.postDelayed(loadHomeActivity, 3000);
        }
        super.onStart();
    }

    // A runnable executed when the progressbar finishes which starts the HomeActivity.
    private Runnable loadHomeActivity = new Runnable() {
        public void run() {
            Intent intenthome = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intenthome);
        }

    };


}
