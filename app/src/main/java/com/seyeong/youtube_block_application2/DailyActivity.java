package com.seyeong.youtube_block_application2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.seyeong.youtube_block_application2.db.DbOpenHelper;
import com.seyeong.youtube_block_application2.decorators.EventDecorator;
import com.seyeong.youtube_block_application2.decorators.OneDayDecorator;
import com.seyeong.youtube_block_application2.decorators.RemoveDecorator;
import com.seyeong.youtube_block_application2.domain.Daily;
import com.seyeong.youtube_block_application2.domain.MyCalendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import static com.seyeong.youtube_block_application2.MainActivity.dates;
import static com.seyeong.youtube_block_application2.MainActivity.mainActivity;
import static com.seyeong.youtube_block_application2.MainActivity.materialCalendarView;
import static com.seyeong.youtube_block_application2.MainActivity.result;

public class DailyActivity extends AppCompatActivity {
    private ListView customListView;
    private static  CustomAdapter adapter;
    private static ArrayList<Daily> dailyList = new ArrayList<Daily>();
    private TextView backSpace;
    private TextView from, to, add, save;
    private DbOpenHelper mDbOpenHelper = new DbOpenHelper(DailyActivity.this);

    private int lastSelectedFromHour=0;
    private int lastSelectedFromMinute=0;
    private int lastSelectedToHour=0;
    private int lastSelectedToMinute=0;

    static PlannerService myService;
    static boolean isService = false; // 서비스 중인 확인용

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            PlannerService.MyBinder mb = (PlannerService.MyBinder) service;
            myService = mb.getService();
            // 서비스가 제공하는 메소드 호출하여
            // 서비스쪽 객체를 전달받을수 있슴
            Log.d("태그" ,"비동기 Service is True");
            isService = true;
        }

        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        Log.d("태그", "isService : " + isService);
        if(!isService) { // 서비스가 실행중이 아니라면
            Log.d("태그", "서비스 실행 시작");
            Intent serviceIntent = new Intent(DailyActivity.this, PlannerService.class);
            bindService(serviceIntent,
                    conn,
                    Context.BIND_AUTO_CREATE);
            startForegroundService(serviceIntent);
        }


        customListView = (ListView) findViewById(R.id.listView);

        backSpace = findViewById(R.id.backSpace);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        add = findViewById(R.id.add);
        save = findViewById(R.id.save);

        Intent i = getIntent();
        MyCalendar myCalendar = new MyCalendar(
                i.getIntExtra("Year", 1),
                i.getIntExtra("Month", 1),
                i.getIntExtra("Day", 1));


        TextView date = findViewById(R.id.date);
        date.setText(myCalendar.getMonth() + "월 " + myCalendar.getDay() + "일");

        openPlan(myCalendar);

        adapter = new CustomAdapter(DailyActivity.this, dailyList);
        customListView.setAdapter(adapter);

        initTimePicker(); // 첫 TimePicker 초기화 값을 현재 시각으로 나타냄

        backSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        from.setOnClickListener((v) -> {
            showHourPicker("From :");
        });

        to.setOnClickListener((v) -> {
            showHourPicker("To :");
        });

        add.setOnClickListener((v) -> {
            String[] fromTime = from.getText().toString().split(":");
            String[] toTime = to.getText().toString().split(":");

            dailyList.add(new Daily(Integer.parseInt(fromTime[0]) * 60 + Integer.parseInt(fromTime[1]),
                    Integer.parseInt(toTime[0]) * 60 + Integer.parseInt(toTime[1])));
            Collections.sort(dailyList, (c1, c2) -> {
                return c1.getFrom() - c2.getFrom();
            });
            adapter.notifyDataSetChanged();
        });

        save.setOnClickListener((v) -> {
            savePlan(myCalendar);
            sharePlan();

            // mainActivity.updateDecorate();
            Log.d("태그", "result.size() : " + result.size());
            finish();
        });

    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (PlannerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void showHourPicker(String title) {
        final Calendar myCalender = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(from.getText().toString().split(":")[0]));
                    myCalender.set(Calendar.MINUTE, Integer.parseInt(to.getText().toString().split(":")[1]));

                    if (title.equals("From :")) {
                        from.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                        lastSelectedFromHour = hourOfDay;
                        lastSelectedFromMinute = minute;
                    } else {
                        to.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                        lastSelectedToHour = hourOfDay;
                        lastSelectedToMinute = minute;
                    }

                }
            }
        };

        TimePickerDialog timePickerDialog = null;
        if (title.equals("From :")) {
            timePickerDialog = new TimePickerDialog(DailyActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, lastSelectedFromHour, lastSelectedFromMinute, true);
        } else {
            timePickerDialog = new TimePickerDialog(DailyActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, lastSelectedToHour, lastSelectedToMinute, true);
        }
        timePickerDialog.setTitle(title);
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();

    }

    public class CustomAdapter extends BaseAdapter {

        private Context context;
        private List<Daily> dailyList;

        public CustomAdapter(Context context, List<Daily> dailyList) {
            this.context = context;
            this.dailyList = dailyList;
        }

        public class ViewHolder {
            public TextView from;
            public TextView to;
            public ImageView delete;
        }

        @Override
        public int getCount() {
            return dailyList.size();
        }

        @Override
        public Object getItem(int i) {
            return dailyList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder holder = null;
            if (view == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.custom_view, null);

                holder = new ViewHolder();
                holder.from = (TextView) view.findViewById(R.id.customFrom);
                holder.to = (TextView) view.findViewById(R.id.customTo);
                holder.delete = (ImageView) view.findViewById(R.id.delete);

                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            Daily country = dailyList.get(i);

            holder.from.setText(String.format("%02d", country.getFrom()/60) + ":" + String.format("%02d", country.getFrom()%60));
            holder.to.setText(String.format("%02d", country.getTo()/60) + ":" + String.format("%02d", country.getTo()%60));
            holder.delete.setOnClickListener((v) -> {
                dailyList.remove(i);
                adapter.notifyDataSetChanged();
            });
            holder.from.setTag(country);
            return view;
        }
    }

    public void openPlan(MyCalendar myCalendar) {
        dailyList.clear();

        mDbOpenHelper.openR();
        List<Daily> plans = mDbOpenHelper.showDaily(myCalendar);
        mDbOpenHelper.close();

        Log.d("태그", "(openPlan) list.size() : " + plans.size());

        // if (list.size() < 1) return;
        dailyList.addAll(plans);
    }

    public void savePlan(MyCalendar myCalendar) { // 선택한 날짜에 대한 객체를 가져옴 (직접 만든 객체)
        mDbOpenHelper.openW();

        mDbOpenHelper.delete(myCalendar); // 하루 일과를 삭제함.

        String day_key = ""+myCalendar.getYear()+String.format("%02d",myCalendar.getMonth())+String.format("%02d", myCalendar.getDay());

        Calendar calendar = Calendar.getInstance();
        calendar.set(myCalendar.getYear(), myCalendar.getMonth()-1, myCalendar.getDay());
        CalendarDay calendarDay = CalendarDay.from(calendar);
        ArrayList<CalendarDay> days = new ArrayList<>();
        days.add(calendarDay);

        if (dailyList.size() < 1) {

            if (result.contains(day_key)) { // 일과가 있었지만 삭제했다면
                Log.d("태그", "// 일과가 있었지만 삭제했다면");
                result.remove(day_key);
                materialCalendarView.addDecorator(new RemoveDecorator(days, mainActivity));
            }

            return; // 계획이 하나도 없다면 아무런 활동도 하지 않도록.
        }

        mDbOpenHelper.createDaily(myCalendar); // 하루 일정을 생성하도록
        mDbOpenHelper.save(myCalendar, dailyList); // 상세 일과를 저장하도록
        mDbOpenHelper.close();

        if (!result.contains(day_key)) { // 일과가 없었지만 새로 만들었다면
            result.add(day_key);
            Log.d("태그" ,"일과가 존재하지 않을 때 생성");
        }
    }

    public void sharePlan() {
        if (!isService) {
            Toast.makeText(DailyActivity.this, "서비스 중이 아닙니다", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("태그", "myService : " + myService);
        myService.setDailyList(dailyList);
    }

    public void initTimePicker() {
        Calendar initCalender = Calendar.getInstance();
        lastSelectedFromHour = initCalender.get(Calendar.HOUR_OF_DAY);
        lastSelectedFromMinute = initCalender.get(Calendar.MINUTE);
        lastSelectedToHour = initCalender.get(Calendar.HOUR_OF_DAY);
        lastSelectedToMinute = initCalender.get(Calendar.MINUTE);
    }

}