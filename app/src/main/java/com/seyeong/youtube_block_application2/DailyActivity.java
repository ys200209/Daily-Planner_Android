package com.seyeong.youtube_block_application2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.seyeong.youtube_block_application2.db.DbOpenHelper;
import com.seyeong.youtube_block_application2.domain.Calender;
import com.seyeong.youtube_block_application2.domain.Daily;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class DailyActivity extends AppCompatActivity {
    private ListView customListView;
    static private CustomAdapter adapter;
    private ArrayList<Daily> dailyList;
    private TextView backSpace;
    private TextView from, to, add, save;
    private DbOpenHelper mDbOpenHelper = new DbOpenHelper(DailyActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        customListView = (ListView) findViewById(R.id.listView);
        dailyList = new ArrayList<Daily>();
        backSpace = findViewById(R.id.backSpace);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        add = findViewById(R.id.add);
        save = findViewById(R.id.save);

        Intent i = getIntent();
        Calender calender = new Calender(
                i.getIntExtra("year", 1),
                i.getIntExtra("month", 1),
                i.getIntExtra("day", 1));


        TextView date = findViewById(R.id.date);
        date.setText(calender.getMonth() + "월 " + calender.getDay() + "일");

        openPlan(calender);

        adapter = new CustomAdapter(DailyActivity.this, dailyList);
        customListView.setAdapter(adapter);

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
            savePlan(calender);
            finish();
        });

    }

    public void showHourPicker(String title) {
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);


        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(from.getText().toString().split(":")[0]));
                    myCalender.set(Calendar.MINUTE, Integer.parseInt(to.getText().toString().split(":")[1]));

                    if (title.equals("From :")) {
                        from.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    } else {
                        to.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    }
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(DailyActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
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

    public void openPlan(Calender calender) {
        dailyList.clear();

        mDbOpenHelper.openR();
        List<Daily> plans = mDbOpenHelper.dailyShow(calender);
        mDbOpenHelper.close();

        Log.d("태그", "(openPlan) list.size() : " + plans.size());

        // if (list.size() < 1) return;
        dailyList.addAll(plans);
    }

    public void savePlan(Calender calender) { // 선택한 날짜에 대한 객체를 가져옴 (직접 만든 객체)
        mDbOpenHelper.openW();

        mDbOpenHelper.delete(calender); // 하루 일과를 삭제함.

        if (dailyList.size() < 1) {
            Log.d("태그", "return;");
            return; // 계획이 하나도 없다면 아무런 활동도 하지 않도록.
        }

        mDbOpenHelper.createDaily(calender); // 하루 일과를 생성하도록
        mDbOpenHelper.save(calender, dailyList); // 일과를 저장하도록

        mDbOpenHelper.close();
    }

}