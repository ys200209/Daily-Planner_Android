package com.seyeong.youtube_block_application2;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActivityTracker implements Application.ActivityLifecycleCallbacks {
    private final Map<Activity, ActivityData> activities = new HashMap<>();


    public Activity getTopForegroundActivity() {
        if (activities.isEmpty()) {
            return null;
        }
        ArrayList<ActivityData> list = new ArrayList<>(activities.values());
        Collections.sort(list, (o1, o2) -> {
            int compare = Long.compare(o2.started, o1.started);
            return compare != 0 ? compare : Long.compare(o2.resumed, o1.resumed);
        });

        ActivityData topActivity = list.get(0);
        return topActivity.started != -1 ? topActivity.activity : null;
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        activities.put(activity, new ActivityData(activity));
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        ActivityData activityData = activities.get(activity);
        if (activityData != null) {
            activityData.started = System.currentTimeMillis();
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        ActivityData activityData = activities.get(activity);
        if (activityData != null) {
            activityData.resumed = System.currentTimeMillis();
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        ActivityData activityData = activities.get(activity);
        if (activityData != null) {
            activityData.resumed = -1;
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        ActivityData activityData = activities.get(activity);
        if (activityData != null) {
            activityData.started = -1;
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        activities.remove(activity);
    }

    private static class ActivityData {

        public final Activity activity;
        public long started;
        public long resumed;

        private ActivityData(Activity activity) {
            this.activity = activity;
        }
    }
}