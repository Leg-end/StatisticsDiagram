package com.person.legend.statisticsdiagram.util;

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

    public static void generateDayData(boolean isCompare,List<BusinessFlow> inFlows
            ,List<BusinessFlow> compareFlows,int bound) {
        int inPeople = 0,i = 0,comparePeople = 0;
        int[] sequence = randomSequence(17,bound);
        int[] sequenceCompare = randomSequence(sequence.length,bound);
        for(;i < sequence.length;i++) {
            inFlows.add(new BusinessFlow(inPeople += sequence[i]*i,i+8,date));
        }
        if(isCompare) {
            for(i = 0;i < sequence.length;i++) {
                compareFlows.add(new BusinessFlow(comparePeople += sequenceCompare[i]*i,i+8,date));
            }
        }
    }

    public static void generateMonthData(boolean isCompare,List<BusinessFlow> inFlows
            ,List<BusinessFlow> compareFlows,int bound) {
        int inPeople = 0,i = 0,comparePeople = 0;
        int[] sequence = randomSequence(DateUtil.getCurrentTimeFiled(Calendar.DAY_OF_MONTH),bound);
        int[] sequenceCompare = randomSequence(sequence.length,bound);
        for(;i < sequence.length;i++) {
            inFlows.add(new BusinessFlow(inPeople += sequence[i]*i,i+8,date));
        }
        if(isCompare) {
            for(i = 0;i < sequence.length;i++) {
                compareFlows.add(new BusinessFlow(comparePeople += sequenceCompare[i]*i,i+8,date));
            }
        }
        if(DateUtil.getDayNum(DateUtil.getCurrentTimeFiled(Calendar.YEAR)
                ,DateUtil.getCurrentTimeFiled(Calendar.MONTH)+1) > sequence.length) {
            inFlows.add(new BusinessFlow(0,i+8,date));
            i -= 1;
            if(isCompare)
                compareFlows.add(new BusinessFlow(0,i+8,date));
        }
    }

    public static void generateYearData(boolean isCompare,List<BusinessFlow> inFlows
            ,List<BusinessFlow> compareFlows,int bound) {
        int inPeople = 0,i = 0,comparePeople = 0;
        int[] sequence = randomSequence(DateUtil.getCurrentTimeFiled(Calendar.MONTH)+1,bound);
        int[] sequenceCompare = randomSequence(sequence.length,bound);
        for(;i < sequence.length;i++) {
            inFlows.add(new BusinessFlow(inPeople += sequence[i]*i,i+8,date));
        }
        if(isCompare) {
            for(i = 0;i < sequence.length;i++) {
                compareFlows.add(new BusinessFlow(comparePeople += sequenceCompare[i]*i,i+8,date));
            }
        }

    }
}
