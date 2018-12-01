package com.gilmaimon.israelposttracker;

import android.app.Application;

public class PostTrackerApplication extends Application {
    private boolean appShowing = false;

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
