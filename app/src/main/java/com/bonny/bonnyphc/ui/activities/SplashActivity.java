package com.bonny.bonnyphc.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bonny.bonnyphc.R;
import com.bonny.bonnyphc.session.SessionManager;

public class SplashActivity extends AppCompatActivity {

    SessionManager sessionManager;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        sessionManager = new SessionManager(this);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                if (sessionManager.isLoggedIn()) {
                    startActivity(new Intent(getApplicationContext(), AllBabiesActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }
}
