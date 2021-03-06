package com.seyeong.youtube_block_application2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.seyeong.youtube_block_application2.db.DbOpenHelper;
import com.seyeong.youtube_block_application2.decorators.*;
import com.seyeong.youtube_block_application2.domain.MyCalendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private DbOpenHelper mDbOpenHelper = new DbOpenHelper(MainActivity.this);
    String time,kcal,menu;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    Cursor cursor;
    static MaterialCalendarView materialCalendarView;
    CalendarDay calendarDay;
    static List<String> result = new ArrayList<>(); // ?????????????????? ???????????? ?????? ???????????? day_key ?????? (????????? ???????????? ????????? ??????)
    static ArrayList<CalendarDay> dates = new ArrayList<>(); // ?????? ?????????????????? ???????????? ?????? ?????????
    static MainActivity mainActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        /*if (!checkAccessibilityPermissions()) { // ?????? ????????? ????????? ???????????? ?????? ????????????
            requestAccessibilty();
        }*/

        if(!checkPermission())
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

        materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);
        mainActivity = MainActivity.this;

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // ????????? ??????
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // ????????? ???
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        Log.d("??????", "now : " + materialCalendarView.getCurrentDate().getYear());
        Log.d("??????", "now : " + (materialCalendarView.getCurrentDate().getMonth()+1));

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        // ????????? ???????????? ????????? DB?????? ??????.
        mDbOpenHelper.openW();
        mDbOpenHelper.create();
        result = mDbOpenHelper.showCalendar(new MyCalendar(
                materialCalendarView.getCurrentDate().getYear(),
                materialCalendarView.getCurrentDate().getMonth(),
                1
        ));
        mDbOpenHelper.close();

        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();



                Log.i("Year test", Year + "");
                Log.i("Month test", Month + "");
                Log.i("Day test", Day + "");

                String shot_Day = Year + "," + Month + "," + Day;

                Log.i("shot_Day test", shot_Day + "");

                materialCalendarView.clearSelection();

                Intent i = new Intent(MainActivity.this, DailyActivity.class);
                i.putExtra("Year", Year);
                i.putExtra("Month", Month);
                i.putExtra("Day", Day);
                startActivity(i);


                // startShell();

            }
        });

        // serviceStart();
        // isAppRunning(MainActivity.this, "com.google.android.youtube");

    }

    public class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        List<String> Time_Result;

        public ApiSimulator(List<String> Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            CalendarDay day = null;
            dates.clear();

            /*???????????? ????????? ?????????????????????*/
            /*?????? 0??? 1??? ???,?????? ?????????*/
            //string ???????????? Time_Result ??? ???????????? ,??? ????????????????????? string??? int ??? ??????
            for(int i = 0 ; i < Time_Result.size() ; i ++){

                // String[] time = Time_Result.get(i).split(",");
                int year = Integer.parseInt(Time_Result.get(i).substring(0, 4));
                int month = Integer.parseInt(Time_Result.get(i).substring(4, 6));
                int dayy = Integer.parseInt(Time_Result.get(i).substring(6, 8));

                calendar.set(year,month-1, dayy);
                day = CalendarDay.from(calendar);

                dates.add(day);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.GREEN, calendarDays,MainActivity.this));
        }
    }

    public void serviceStart() {
        if(!isServiceRunningCheck()) { // ???????????? ???????????? ????????????
            Log.d("??????", "????????? ?????? ??????");
            Intent intent = new Intent(getApplicationContext(), PlannerService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (PlannerService.class.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // (context, "com.google.android.youtube")
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();

        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                Log.d("??????", "Name : " + processInfo.processName);
                if (processInfo.processName.equals(packageName)) {
                    Log.d("??????", "Name : Equals App : com.google.android.youtube");
                    // return true;
                }
            }
        }
        return false;
    }

    public boolean checkAccessibilityPermissions() { // ?????? ???????????? ????????? ??????????????? boolean ????????? ??????
        AccessibilityManager accessibilityManager =
                (AccessibilityManager) getApplicationContext().getSystemService(Context.ACCESSIBILITY_SERVICE);

        return accessibilityManager.isEnabled();
    }

    public void requestAccessibilty() { // ???????????? ?????? ???????????? ????????? ????????? ??????
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("????????? ?????? ??????")
                .setMessage("????????? ??? ????????? ????????? ???????????? ???????????????.")
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivityForResult(intent, 0); // ????????? ?????? ???????????? ????????? ??????
                    }
                })
                .setCancelable(false)
                .show();
    }

    private boolean checkPermission(){

        boolean granted = false;

        AppOpsManager appOps = (AppOpsManager) getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getApplicationContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (getApplicationContext().checkCallingOrSelfPermission(
                    android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        }
        else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        return granted;
    }

    /*public void updateDecorate() {

    }*/

    @Override
    public void onPause() {
        super.onPause();
        Log.d("??????", "(onPause) : finish()");
    }

    @Override
    public void onResume() {
        super.onResume();
        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("??????", "(onStop)");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("??????", "(onDestroy)");
    }

}