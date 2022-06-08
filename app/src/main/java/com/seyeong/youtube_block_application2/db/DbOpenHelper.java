package com.seyeong.youtube_block_application2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.seyeong.youtube_block_application2.CustomView;
import com.seyeong.youtube_block_application2.domain.Calender;
import com.seyeong.youtube_block_application2.domain.Daily;

import java.util.ArrayList;
import java.util.List;

public class DbOpenHelper {
    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(RegisterRequest.Calender._CREATE);
            db.execSQL(RegisterRequest.Daily._CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+RegisterRequest.Calender._TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS "+RegisterRequest.Daily._TABLENAME);
            onCreate(db);
        }

        public void onDrop(SQLiteDatabase db){
            db.execSQL("DROP TABLE IF EXISTS " + RegisterRequest.Calender._TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS " + RegisterRequest.Daily._TABLENAME);
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
            if (!db.isReadOnly()) { // 일정 삭제를 위한 외래키 설정 ON (Default는 OFF)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    String query = String.format("PRAGMA foreign_key = %s", "ON");
                    db.execSQL(query);
                } else {
                    db.setForeignKeyConstraintsEnabled(true);
                }
            }
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper openW() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public DbOpenHelper openR() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getReadableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void update(){
        mDBHelper.onUpgrade(mDB, DATABASE_VERSION, DATABASE_VERSION);
    }

    public void drop() {
        mDBHelper.onDrop(mDB);
    }

    public void close(){
        mDB.close();
    }

    public Boolean isOpened() {
        return mDB.isOpen();
    }

    // SELECT 구문 (비트맵값이 제대로 들어갔는지 확인하는 메서드)
    /*public ArrayList<Map<String, byte[]>> selectDownloadBitmap() {
        int i = 0;
        ArrayList<Map<String, byte[]>> list = new ArrayList<>();
        Map<String, byte[]> map = new HashMap<>();

        String SQL = "SELECT * FROM " + RegisterRequest.CreateTB._TABLENAME2;
        Cursor cursor = mDB.rawQuery(SQL, null);
        int idx_thumbnail = cursor.getColumnIndex("thumbnail");
        while(cursor.moveToNext()) {
            map.put("thumbnail" + i, cursor.getBlob(idx_thumbnail));
            list.add(map);
            i++;
        }
        return list;
    }

    // SELECT 구문 (다운로드 리스트를 불러오는 메서드)
    public ArrayList<Map<String, String>> selectDownloadList() {
        int i = 0;
        ArrayList<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();

        String SQL = "SELECT * FROM " + RegisterRequest.CreateTB._TABLENAME2;
        Cursor cursor = mDB.rawQuery(SQL, null);
        int idx_title = cursor.getColumnIndex("title");
        int idx_progress = cursor.getColumnIndex("progress");
        int idx_isdownload = cursor.getColumnIndex("isdownload");
        int idx_filesize = cursor.getColumnIndex("filesize");
        int idx_url = cursor.getColumnIndex("videourl");
        int idx_quality = cursor.getColumnIndex("quality");
        while(cursor.moveToNext()) {
            map.put("title"+i, cursor.getString(idx_title));
            map.put("progress"+i, cursor.getString(idx_progress));
            map.put("isdownload"+i, cursor.getString(idx_isdownload));
            map.put("filesize"+i, cursor.getString(idx_filesize));
            map.put("videourl"+i, cursor.getString(idx_url));
            map.put("quality"+i, cursor.getString(idx_quality));
            list.add(map);
            i++;
        }
        return list;
    }*/

    // SELECT 구문 (프로그레스바만 개별로 추출하기 위한 메서드)
    /*public Map<String, String> selectProgress() {
        int i = 0;
        Map<String, String> map = new HashMap<>();
        String SQL = "SELECT * FROM " + RegisterRequest.CreateTB._TABLENAME2;
        Cursor cursor = mDB.rawQuery(SQL, null);
        int idx_progress = cursor.getColumnIndex("progress");
        int idx_status = cursor.getColumnIndex("isdownload");
        while(cursor.moveToNext()) { // ex) 3 - (3-i) 를 함으로써 여기서 미리 리스트뷰에 올라갈 프로그레스바를 정렬해서 넣어준다
            map.put("progress"+(cursor.getCount()-i-1), cursor.getString(idx_progress));
            map.put("isdownload"+(cursor.getCount()-i-1), cursor.getString(idx_status));
            i++;
        }
        return map;
    }

    // SELECT
    // 현재 다운로드를 원하는 파일이 이미 존재하는지 확인여부
    public int existsDownload(String title) {
        String SQL = "SELECT * FROM " + RegisterRequest.CreateTB._TABLENAME2
                + " WHERE "+ RegisterRequest.CreateTB.TITLE + " = '" + title + "'";
        Cursor cursor = mDB.rawQuery(SQL,null);
        //Log.d("태그", "파일이 존재하는가..");
        return cursor.getCount();
    }

    // 존재하는데도 불구하고 삭제후 다시 다운로드를 원한다면 그 파일의 다운로드 상태를 가져와서 그에 따라 파일 삭제방식을 달리한다.
    public String selectExistsIsDownload(String title) {
        String SQL = "SELECT * FROM " + RegisterRequest.CreateTB._TABLENAME2
                + " WHERE "+ RegisterRequest.CreateTB.TITLE + " = '" + title + "'";
        Cursor cursor = mDB.rawQuery(SQL, null);
        int idx_status = cursor.getColumnIndex("isdownload");
        if(cursor.moveToNext()) {}

        return cursor.getString(idx_status);
    }

    // INSERT 구문 (다운로드 등록)
    public long insertDownload(String title, byte[] data, String progress, String isdownload,
                               String filesize, String url, String selectQuality) {
        ContentValues values = new ContentValues();
        values.put(RegisterRequest.CreateTB.TITLE, title);
        values.put(RegisterRequest.CreateTB.THUMBNAIL, data);
        values.put(RegisterRequest.CreateTB.PROGRESS, progress);
        values.put(RegisterRequest.CreateTB.ISDOWNLOAD, isdownload);
        values.put(RegisterRequest.CreateTB.FILESIZE, filesize);
        values.put(RegisterRequest.CreateTB.VIDEOURL, url);
        values.put(RegisterRequest.CreateTB.QUALITY, selectQuality);
        return mDB.insert(RegisterRequest.CreateTB._TABLENAME2, null, values);
    }*/

    // 특정 날짜를 클릭 시 해당 날짜의 일과표를 보여주는 메서드
    public List<Daily> dailyShow(Calender calender) {

        String SQL = "SELECT * FROM " + RegisterRequest.Daily._TABLENAME + " WHERE day_key = " +
                ("" + calender.getYear() + calender.getMonth() + calender.getDay()) +
                " ORDER BY " + RegisterRequest.Daily.START;

        List<Daily> list = new ArrayList<>();

        Cursor cursor = mDB.rawQuery(SQL, null);
        int idx_start = cursor.getColumnIndex("start");
        int idx_end = cursor.getColumnIndex("end");
        Daily daily;
        while(cursor.moveToNext()) {
            daily = new Daily(Integer.parseInt(cursor.getString(idx_start)),
                    Integer.parseInt(cursor.getString(idx_end)));
            list.add(daily);
        }

        return list;
    }

    // 일일 계획에서 '왼료'를 누를 시 저장되는 계획들
    public long save(Calender calender, List<Daily> plans) {
        ContentValues values = new ContentValues();
        String day_key = ""+calender.getYear()+calender.getMonth()+calender.getDay();

        Log.d("태그", "day_key : " + day_key);

        int count=0;
        for(Daily plan : plans) {
            values.put(RegisterRequest.Daily.START, plan.getFrom());
            values.put(RegisterRequest.Daily.END, plan.getTo());
            values.put(RegisterRequest.Daily.DAY_KEY, day_key);
            mDB.insert(RegisterRequest.Daily._TABLENAME, null, values);
            count++;
        }

        return count; // 삽입된 ROW 수만큼 count
    }

    // UPDATE 구문 (프로그레스값 변경)
    /*public int updateProgress(String title, int progress) {
        ContentValues values = new ContentValues();
        if (progress == 100) { // 다운로드 프로그레스가 다 찼으면 상태값 변경
            values.put(RegisterRequest.CreateTB.ISDOWNLOAD, "다운로드됨");
        } else {
            values.put(RegisterRequest.CreateTB.ISDOWNLOAD, "다운로드중..");
        }
        values.put(RegisterRequest.CreateTB.PROGRESS, Integer.toString(progress));
        int a = mDB.update(RegisterRequest.CreateTB._TABLENAME2, values,
                "title = ?", new String[]{title});
        //Log.d("태그", "결과값 return : " + a);
        return a;
    }

    public int updateDownloadPause(String title) { // 일시정지
        ContentValues values = new ContentValues();
        values.put(RegisterRequest.CreateTB.ISDOWNLOAD, "일시정지");
        Log.d("태그", "일시정지 시도");
        //Log.d("태그", "일시정지를 시도한 title = " + title);
        return mDB.update(RegisterRequest.CreateTB._TABLENAME2, values,
                "title = ?", new String[]{title});
    }

    // DELETE 구문 (다운로드 삭제)
    public int deleteDownload(String title) {
        return mDB.delete(RegisterRequest.CreateTB._TABLENAME2,
                "title = ?", new String[]{title});
    }*/

    // 하루 일과를 생성함
    public long createDaily(Calender calender) {
        ContentValues values = new ContentValues();

        String day_key = ""+calender.getYear()+calender.getMonth()+calender.getDay();
        values.put(RegisterRequest.Calender.DAY_KEY, day_key);
        values.put(RegisterRequest.Calender.YEAR, calender.getYear());
        values.put(RegisterRequest.Calender.MONTH, calender.getMonth());
        values.put(RegisterRequest.Calender.DAY, calender.getDay());

        Log.d("태그", "하루 생성 : " + day_key);
        return mDB.insert(RegisterRequest.Calender._TABLENAME, null, values);
        // INSERT INTO Calender VALUES('202267', 2022, 6, 7);
    }

    // 하루 일과를 다시 세팅하도록 날짜를 새로 생성해줌
    public int delete(Calender calender) {
        String day_key = ""+calender.getYear()+calender.getMonth()+calender.getDay();

        int deleteCount = mDB.delete(RegisterRequest.Calender._TABLENAME,
                "day_key = ?", new String[]{day_key});
        Log.d("태그", "하루 삭제 : " + deleteCount);

        return deleteCount;
        // DELETE FROM Calender WHERE day_key = '202267';
    }
}
