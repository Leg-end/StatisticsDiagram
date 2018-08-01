package com.person.legend.statisticsdiagram.util;


import android.util.Log;

import com.person.legend.statisticsdiagram.bean.BusinessFlow;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

public final class FakeUtil {
    private static final Random random = new Random();
    private static final String date = DateUtil.getNowDateInFormat();
    private FakeUtil() { }
    private static int[] randomSequence(int len,int bound) {
        int[] randomArr = new int[len];
        for(int i = 0;i < len;i++) {
            randomArr[i] = random.nextInt(bound);
        }
        return randomArr;
    }
    //rateMeasure = 60/rateFre ex:rateFre = 10:update/10min in one hour update
    //60/10 times -> 6 times
    public static void generateDayData(boolean isCompare,List<BusinessFlow> inFlows
            ,List<BusinessFlow> compareFlows,int bound,int rateMeasure) {
        int inPeople = 0,i = 0,comparePeople = 0,len;
        Log.d("Diagram","hour now:"+DateUtil.getCurrentTimeFiled(Calendar.MINUTE));

        len = (DateUtil.getCurrentTimeFiled(Calendar.HOUR_OF_DAY)-8)*rateMeasure
                +DateUtil.getCurrentTimeFiled(Calendar.MINUTE)/10+1;
        Log.d("Diagram","random len:"+len);
        int[] sequence = randomSequence(len,bound);
        for(;i < sequence.length;i++) {
            inFlows.add(new BusinessFlow(inPeople += sequence[i]*i,i,date));
        }
        if(isCompare) {
            int[] sequenceCompare = randomSequence(16*rateMeasure+1,bound+10);
            for(i = 0;i < sequence.length;i++) {
                compareFlows.add(new BusinessFlow(comparePeople += sequenceCompare[i]*i
                        ,i,date));
            }
        }
    }

    public static void generateMonthData(boolean isCompare,List<BusinessFlow> inFlows
            ,List<BusinessFlow> compareFlows,int bound) {
        int inPeople = 400,i = 0,comparePeople = 450;
        int[] sequence = randomSequence(DateUtil.getCurrentTimeFiled(Calendar.DAY_OF_MONTH),bound);
        int[] sequenceCompare = randomSequence(sequence.length,bound);
        for(;i < sequence.length;i++) {
            inFlows.add(new BusinessFlow(inPeople += sequence[i]*i,i,date));
        }
        if(isCompare) {
            for(i = 0;i < sequence.length;i++) {
                compareFlows.add(new BusinessFlow(comparePeople += sequenceCompare[i]*i,i,date));
            }
        }
        if(DateUtil.getDayNum(DateUtil.getCurrentTimeFiled(Calendar.YEAR)
                ,DateUtil.getCurrentTimeFiled(Calendar.MONTH)+1) > sequence.length) {
            inFlows.add(new BusinessFlow(0,i,date));
            if(isCompare)
                compareFlows.add(new BusinessFlow(0,i,date));
        }
    }

    public static void generateYearData(boolean isCompare,List<BusinessFlow> inFlows
            ,List<BusinessFlow> compareFlows,int bound) {
        int inPeople = 800,i = 0,comparePeople = 900;
        int[] sequence = randomSequence(DateUtil.getCurrentTimeFiled(Calendar.MONTH)+1,bound);
        int[] sequenceCompare = randomSequence(sequence.length,bound);
        for(;i < sequence.length;i++) {
            inFlows.add(new BusinessFlow(inPeople += sequence[i]*i,i,date));
        }
        if(isCompare) {
            for(i = 0;i < sequence.length;i++) {
                compareFlows.add(new BusinessFlow(comparePeople += sequenceCompare[i]*i,i,date));
            }
        }

    }
}
