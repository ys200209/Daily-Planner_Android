package com.seyeong.youtube_block_application2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.seyeong.youtube_block_application2.decorators.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    String time,kcal,menu;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    Cursor cursor;
    MaterialCalendarView materialCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        List<String> result = Arrays.asList("2017,03,18","2017,04,18","2017,05,18","2017,06,18");

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
                Toast.makeText(getApplicationContext(), shot_Day , Toast.LENGTH_SHORT).show();

                Intent i = new Intent(MainActivity.this, DailyActivity.class);
                i.putExtra("month", Month);
                i.putExtra("day", Day);
                startActivity(i);


            }
        });
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        List<String> Time_Result;

        ApiSimulator(List<String> Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.size() ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result.get(i).split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year,month-1,dayy);
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

    /*public void showDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_alertdialog);

        spinner = (Spinner) dialog.findViewById(R.id.formatSpinner);
        editTitle = (EditText) dialog.findViewById(R.id.editTitle);
        final TextView saveButton = (TextView) dialog.findViewById(R.id.saveButton);
        editTitle.setText(str_title);

        ArrayList<String> arrayList = new ArrayList<>();
        audioSize = sizeMap.get("tiny");
        if (!isTwitch) { // 유튜브 다운로드
            String[] existsArr = {"2160p60", "2160p", "1440p60", "1440p", "1080p60", "1080p"
                    , "720p60", "720p", "480p", "360p", "240p", "144p"};

            arrayList.add("mp3");
            boolean highQuality = true;

            for (int i = 0; i < existsArr.length; i++) {
                if (qualityMap.containsKey(existsArr[i]) && sizeMap.containsKey(existsArr[i])) {
                    if (existsArr[i].substring(existsArr[i].length() - 2, existsArr[i].length()).equals("60") && highQuality) {
                        // 고화질이 존재한다면 ( 60프레임 )
                        Long videoSize = sizeMap.get(existsArr[i]);
                        String size = "";

                        if (videoSize != 0 && audioSize != null) {
                            // videoSize와 audioSize를 불러오지 못하는 경우엔 값을 아예 주지말고 존재할때만 이렇게 주자.
                            size = Long.toString(videoSize + audioSize);
                            size = size.substring(0, size.length() - 4);
                            sb = new StringBuilder(size);
                            sb.insert(size.length() - 2, ".");
                            if (sb.indexOf(".") == 0) {
                                sb.insert(0, "0");
                            }
                        } else {
                            sb = new StringBuilder("?");
                        }
                        arrayList.add(existsArr[i] + "  -  ( " + sb.toString() + " MB )");
                        highQuality = false;

                    } else if (existsArr[i].substring(existsArr[i].length() - 1, existsArr[i].length()).equals("p") && !highQuality) {
                        // 60 프레임을 만나고도 30프레임이 또 존재한다면 그것을 무시하고 고화질만 띄워라.
                        highQuality = true;
                        continue;
                    } else if ((!qualityMap.containsKey("2160p") || !qualityMap.containsKey("1440p")
                            || !qualityMap.containsKey("1080p") || !qualityMap.containsKey("720p")) && !highQuality) {
                        // 고화질을 만나서 bool값을 변경했음에도 일반 프레임을 만나지 못했을때.
                        highQuality = true;
                        continue;
                    } else {
                        // 고화질이 아니라면 그냥 상관없이 넣음
                        Long videoSize = sizeMap.get(existsArr[i]);
                        String size = "";

                        if (videoSize != 0 && audioSize != null) {
                            // videoSize와 audioSize를 불러오지 못하는 경우엔 값을 아예 주지말고 존재할때만 이렇게 주자.
                            size = Long.toString(videoSize + audioSize);
                            size = size.substring(0, size.length() - 4);
                            sb = new StringBuilder(size);
                            sb.insert(size.length() - 2, ".");
                            //Log.d("태그", "sb = " + sb+ ", sb.indexOf(\".\") = " + sb.indexOf("."));
                            if (sb.indexOf(".") == 0) {
                                sb.insert(0, "0");
                            }
                        } else {
                            sb = new StringBuilder("?");
                        }

                        arrayList.add(existsArr[i] + "  -  ( " + sb.toString() + " MB )");
                    }
                } else {
                    highQuality = true;
                }
            }
        }

        adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item,
                arrayList);
        spinner.setAdapter(adapter);

        dialog.show(); // 다이얼로그 보여주기

        // 다이얼로그 사이즈 조절

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        //lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = (int) (size.x * 1.0f);
        //lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = (int) (size.x * 1.2f);
        Window window = dialog.getWindow();
        window.setAttributes(lp);
    }*/
}