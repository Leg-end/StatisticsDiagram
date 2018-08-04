package com.person.legend.statisticsdiagram.util;

import java.util.Calendar;
import java.util.TimeZone;

public final class DateUtil {
    private final static Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));

    private DateUtil() {
    }

    public static String[] generateMonthDays(int year, int month) {
        String[] target;
        int len = getCurrentTimeFiled(Calendar.DAY_OF_MONTH);
        int num = DateUtil.getDayNum(year,month);
        int index = 0;
        if (num - len > 1) {
            target = new String[(len > 16 ? len+2:18)];
            target[target.length-2] = "...";
        } else if(num - len > 0) {
            target = new String[len+1];
        } else
            target = new String[len];
        for (; index < target.length-2; index++) {
            target[index] = String.valueOf((index + 1));
        }
        if(target[index].isEmpty())
            target[index] = String.valueOf((index+1));
        target[target.length-1] = String.valueOf(num);
        return target;
    }

    public static int getDayNum(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if (year % 4 == 0)
                    return 29;
                else
                    return 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
        }
        return -1;

    }

    public static int getCurrentTimeFiled(int filed) {
        return calendar.get(filed);
    }

    public static String getNowDateInFormat() {
        return (getCurrentTimeFiled(Calendar.MONTH) + 1) + "月"
                + getCurrentTimeFiled(Calendar.DAY_OF_MONTH) + "日 "
                + "星期" + getCurrentTimeFiled(Calendar.WEEK_OF_MONTH) ;
    }

    public static String getNowDateInYDM() {
        return getCurrentTimeFiled(Calendar.YEAR) + "/"
                + (getCurrentTimeFiled(Calendar.MONTH) + 1) + "/"
                + getCurrentTimeFiled(Calendar.DAY_OF_MONTH);
    }

}
