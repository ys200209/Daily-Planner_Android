package com.seyeong.youtube_block_application2.db;

import android.provider.BaseColumns;

public class RegisterRequest {
    public static final class Calender implements BaseColumns {
        public static final String DAY_KEY = "day_key";
        public static final String YEAR = "year";
        public static final String MONTH = "month";
        public static final String DAY = "day";
        public static final String _TABLENAME = "Calender";
        public static final String _CREATE = "create table if not exists "+_TABLENAME+"("
                +DAY_KEY+" text NOT NULL PRIMARY KEY, " +YEAR+" INTEGER NOT NULL, "
                +MONTH+" INTEGER NOT NULL, " +DAY+" INTEGER NOT NULL);";
    }

    public static final class Daily implements BaseColumns {
        public static final String KEYNUM = "keynum";
        public static final String START = "start";
        public static final String END = "end";
        public static final String DAY_KEY = "day_key";
        public static final String _TABLENAME = "Daily";
        public static final String _CREATE = "create table if not exists "+_TABLENAME+"("
                +KEYNUM+" INTEGER PRIMARY KEY AUTOINCREMENT, " +START+" INTEGER NOT NULL, "
                +END+" INTEGER NOT NULL, " +DAY_KEY+" text NOT NULL "
                +"REFERENCES Calender(day_key) ON DELETE CASCADE );";
    }

}
