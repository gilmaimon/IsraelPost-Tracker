package com.gilmaimon.israelposttracker;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        PostTrackerApplication app = (PostTrackerApplication) getApplication();
        app.activityResumed();
    }

    @Override
    protected void onPause() {
        PostTrackerApplication app = (PostTrackerApplication) getApplication();
        app.activityPaused();
        super.onPause();
    }
}
