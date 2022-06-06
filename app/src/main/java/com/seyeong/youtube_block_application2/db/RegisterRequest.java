package com.seyeong.youtube_block_application2.db;

import android.provider.BaseColumns;

public class RegisterRequest {
    public static final class Calender implements BaseColumns {
        public static final String KEYNUM = "keynum";
        public static final String YEAR = "year";
        public static final String MONTH = "month";
        public static final String DAY = "day";
        public static final String _TABLENAME = "testtableaa";
        public static final String _CREATE = "create table if not exists "+_TABLENAME+"("
                +KEYNUM+" INTEGER PRIMARY KEY AUTOINCREMENT, " +YEAR+" text NOT NULL PRIMARY KEY, "
                +MONTH+" blob NOT NULL, " +DAY+" text NOT NULL);";
    }

    public static final class DailyPlan implements BaseColumns {
        public static final String KEYNUM = "keynum";
        public static final String FROM = "from";
        public static final String TO = "to";
        public static final String _TABLENAME = "daily_plan";
        public static final String _CREATE = "create table if not exists "+_TABLENAME+"("
                +KEYNUM+" INTEGER PRIMARY KEY AUTOINCREMENT, " +FROM+" text NOT NULL, "
                +TO+" text NOT NULL);";
    }

}
