package com.seyeong.youtube_block_application2;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

public class PlannerService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel3";
    // 다른 앱의 서비스 컴포넌트에 동일한 이름이 있기에 서비스 생성에 오류가 날까봐 뒤에 3을 붙임

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("태그", "onStartCommand");
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();
        startForeground(3, notification);

        startThread();

        return START_STICKY;
    }



    private void createNotificationChannel() {
        Log.d("태그", "createNotificationChannel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel =
                    new NotificationChannel( CHANNEL_ID, "공부해",
                            NotificationManager.IMPORTANCE_NONE );
            serviceChannel.setShowBadge(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);

        }
    }

    public void startThread() {

        new Thread(() -> {
            int i=0;
            while(i < 1) {
                try {
                    isAppRunning(PlannerService.this, "com.google.android.youtube");
                    Thread.sleep(3000);
                } catch (PackageManager.NameNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
            Log.d("태그", "스레드 종료");
        }).start();


    }

    // (context, "com.google.android.youtube")
    public boolean isAppRunning(final Context context, final String packageName) throws PackageManager.NameNotFoundException {





        return false;
    }

}