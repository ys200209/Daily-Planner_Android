package com.seyeong.youtube_block_application2;

public class MonthDate {

    private int YEAR, MONTH, DAY;

    public MonthDate(int YEAR, int MONTH, int DAY) {
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
        MonthDate aMonthDate = (MonthDate)obj;

        if (this.YEAR == aMonthDate.getYear() &&
        this.MONTH == aMonthDate.getMonth() &&
        this.DAY == aMonthDate.getDay()) return true;

        return false;
    }

    @Override
    public int hashCode() {
        return YEAR + MONTH + DAY;
    }

}