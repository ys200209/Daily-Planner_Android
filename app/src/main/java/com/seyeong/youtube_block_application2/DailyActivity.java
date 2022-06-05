package com.seyeong.youtube_block_application2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class DailyActivity extends AppCompatActivity {
    private ListView customListView;
    // static private CustomAdapter adapter;
    private ArrayList<CustomView> customList;
    private TextView backSpace;
    private TextView from, to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        customListView = (ListView) findViewById(R.id.listView);
        customList = new ArrayList<CustomView>();
        backSpace = findViewById(R.id.backSpace);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);

        Intent i = getIntent();
        int Month = i.getIntExtra("month", 1);
        int Day = i.getIntExtra("day", 1);

        TextView date = findViewById(R.id.date);
        date.setText(Month + "월 " + Day + "일");

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
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(DailyActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle(title);
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();

        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), hour+", " + minute, Toast.LENGTH_SHORT).show();
            }
        };

    }

    /*public class CustomAdapter extends BaseAdapter {

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
            public ImageView thumbnail;
            public TextView title;
            public int int_progress = public_progress;
            public ProgressBar progressBar;
            public String string_persent = public_percent;
            public TextView progressPersent;
            public TextView fileSize;
            public Bitmap mBitmap;
            public TextView tvDownloadStatus;
            public String holder_downloadStatus = downloadStatus;
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
                holder.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
                holder.title = (TextView) view.findViewById(R.id.title);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
                holder.progressPersent = (TextView) view.findViewById(R.id.progress_percent);
                holder.fileSize = (TextView) view.findViewById(R.id.fileSize);
                holder.tvDownloadStatus = (TextView) view.findViewById(R.id.downloadStatus);

                view.setTag(holder);


            } else {
                holder = (ViewHolder) view.getTag();
            }

            CustomView country = customList.get(i);

            mDbOpenHelper.openR();
            Map<String, String> map = mDbOpenHelper.selectProgress();
            mDbOpenHelper.close();

            holder.thumbnail.setImageBitmap(country.getmBitmap());
            holder.title.setText(country.getTitle()+"  ");
            holder.progressBar.setProgress(Integer.parseInt(map.get("progress"+i)));
            holder.progressPersent.setText(map.get("progress"+i) + "%");
            holder.fileSize.setText(country.getFileSize());
            holder.tvDownloadStatus.setText(map.get("isdownload"+i));
            holder.title.setTag(country);
            return view;
        }
    }*/

}