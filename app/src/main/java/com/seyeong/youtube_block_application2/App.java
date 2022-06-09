package com.seyeong.youtube_block_application2;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class App extends Application {

    private ActivityTracker activityTracker;
    private Activity activity;

    @Override
    public void onCreate() {
        super.onCreate();


        if(activity != null) {
            // Do something
        }
    }

    public Activity getActivity() {
        activityTracker = new ActivityTracker();
        registerActivityLifecycleCallbacks(activityTracker);


        activity = activityTracker.getTopForegroundActivity();

        return activity;
    }

}