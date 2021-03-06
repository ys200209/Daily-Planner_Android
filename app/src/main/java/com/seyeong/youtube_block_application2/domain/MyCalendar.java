package com.seyeong.youtube_block_application2.domain;

public class MyCalendar {

    private int YEAR, MONTH, DAY;

    public MyCalendar(int YEAR, int MONTH, int DAY) {
        this.YEAR = YEAR;
        this.MONTH = MONTH;
        this.DAY = DAY;
    }

    public int getYear() {
        return YEAR;
    }

    public int getMonth() {
        return MONTH;
    }

    public int getDay() {
        return DAY;
    }

    @Override
    public boolean equals(Object obj) {
        MyCalendar aCalender = (MyCalendar)obj;

        if (this.YEAR == aCalender.getYear() &&
        this.MONTH == aCalender.getMonth() &&
        this.DAY == aCalender.getDay()) return true;

        return false;
    }

    @Override
    public int hashCode() {
        return YEAR + MONTH + DAY;
    }

}