package com.gilmaimon.israelposttracker;

import android.app.Application;
import android.util.Log;

public class PostTrackerApplication extends Application {
    private boolean appShowing = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void activityResumed() {
        appShowing = true;
    }

    public void activityPaused() {
        appShowing = false;
    }

    public boolean isAppShowing() {
        return appShowing;
    }
}
