package com.person.legend.statisticsdiagram.util;

import java.util.Calendar;

public final class DateUtil {
    private final static Calendar calendar = Calendar.getInstance();

    private DateUtil() {
    }

    public static String[] generateMonthDays(int year, int month) {
        String[] target;
        int len = getCurrentTimeFiled(Calendar.DAY_OF_MONTH);
        if (len < DateUtil.getDayNum(year, month)) {
            target = new String[len + 1];
            target[len] = "...";
        } else {
            target = new String[len];
        }
        for (int i = 0; i < len; i++) {
            target[i] = String.valueOf((i + 1));
        }
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
        return getCurrentTimeFiled(Calendar.YEAR) + "/"
                + (getCurrentTimeFiled(Calendar.MONTH) + 1) + "/"
                + getCurrentTimeFiled(Calendar.DAY_OF_MONTH);
    }

}
