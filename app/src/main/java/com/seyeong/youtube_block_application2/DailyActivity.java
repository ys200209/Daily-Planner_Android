package com.seyeong.youtube_block_application2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DailyActivity extends AppCompatActivity {
    private ListView customListView;
    static private CustomAdapter adapter;
    private ArrayList<CustomView> customList;
    private TextView backSpace;
    private TextView from, to, add, save;
    private DbOpenHelper mDbOpenHelper = new DbOpenHelper(DailyActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        customListView = (ListView) findViewById(R.id.listView);
        customList = new ArrayList<CustomView>();
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

        adapter = new CustomAdapter(DailyActivity.this, customList);
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
            customList.add(new CustomView(from.getText().toString(), to.getText().toString()));
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
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);

                    Toast.makeText(getApplicationContext(), hourOfDay + ", " + minute , Toast.LENGTH_SHORT).show();

                    if (title.equals("From :")) {
                        from.setText(hourOfDay + ":" + minute);
                        /*if (hourOfDay < 10) {
                            from.setText("0"+hourOfDay+":"+mi);
                        } else {

                        }*/
                    } else {
                        to.setText(hourOfDay + ":" + minute);
                        /*if (hourOfDay < 10) {

                        } else {

                        }*/
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
        private List<CustomView> customViewList;
        public String downloadStatus = "다운로드중...";
        public int public_progress = 0;
        public String public_percent;

        public CustomAdapter(Context context, List<CustomView> customViewList) {
            this.context = context;
            this.customViewList = customViewList;
        }

        public class ViewHolder {
            public TextView from;
            public TextView to;
            public ImageView delete;
        }

        @Override
        public int getCount() {
            return customViewList.size();
        }

        @Override
        public Object getItem(int i) {
            return customViewList.get(i);
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
                //final TextView work = (TextView) view.findViewById(R.id.titl);
                holder = new ViewHolder();
                holder.from = (TextView) view.findViewById(R.id.customFrom);
                holder.to = (TextView) view.findViewById(R.id.customTo);
                holder.delete = (ImageView) view.findViewById(R.id.delete);

                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }

            CustomView country = customList.get(i);

            /*mDbOpenHelper.openR();
            Map<String, String> map = mDbOpenHelper.selectProgress();
            mDbOpenHelper.close();

            holder.thumbnail.setImageBitmap(country.getmBitmap());
            holder.title.setText(country.getTitle()+"  ");
            holder.progressBar.setProgress(Integer.parseInt(map.get("progress"+i)));
            holder.progressPersent.setText(map.get("progress"+i) + "%");
            holder.fileSize.setText(country.getFileSize());
            holder.tvDownloadStatus.setText(map.get("isdownload"+i));*/


            holder.from.setText(country.getFrom());
            holder.to.setText(country.getTo());
            holder.delete.setOnClickListener((v) -> {
                customList.remove(i);
                adapter.notifyDataSetChanged();
            });
            holder.from.setTag(country);
            return view;
        }
    }

    public void openPlan(Calender calender) {
        mDbOpenHelper.openR();
        List<CustomView> list = mDbOpenHelper.dailyShow(calender);
        mDbOpenHelper.close();

        // if (list.size() < 1) return;
        customList.clear();
        customList.addAll(list);
    }

    public void savePlan(Calender calender) { // 선택한 날짜에 대한 객체를 가져옴 (직접 만든 객체)
        mDbOpenHelper.openW();

        mDbOpenHelper.delete(calender); // 하루 일과를 삭제함.

        if (customList.size() < 1) return; // 계획이 하나도 없다면 아무런 활동도 하지 않도록.

        mDbOpenHelper.createDaily(calender); // 하루 일과를 생성하도록
        mDbOpenHelper.save(calender, customList); // 일과를 저장하도록

        mDbOpenHelper.close();
    }

}