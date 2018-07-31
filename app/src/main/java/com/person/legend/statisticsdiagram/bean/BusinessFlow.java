package com.person.legend.statisticsdiagram.bean;

public class BusinessFlow {
    private int num;
    private int hour;
    private String date;
    private String[] ymd;
    private int day;
    private int month;
    private int year;

    public BusinessFlow() {
    }

    public BusinessFlow(int num, int hour, String date) {
        this.num = num;
        this.hour = hour;
        this.date = date;
        ymd = this.date.split("/");
        this.day = Integer.parseInt(ymd[2]);
        this.month = Integer.parseInt(ymd[1]);
        this.year = Integer.parseInt(ymd[0]);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String[] getYmd() {
        return ymd;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
