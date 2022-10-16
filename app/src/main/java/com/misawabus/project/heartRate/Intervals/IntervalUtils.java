package com.misawabus.project.heartRate.Intervals;

import androidx.annotation.NonNull;

import com.misawabus.project.heartRate.Utils.Constants;
import com.veepoo.protocol.model.datas.TimeData;

import java.time.LocalTime;

public class IntervalUtils {
    public static final String[] intervalLabels30Min;
    private static final String TAG = IntervalUtils.class.getSimpleName();

    static {
        int intervalsTotal = (int) Math.round(24.0/ Constants.INTERVAL_WIDTH_HALF);
        final String[] domainLabels = new String[intervalsTotal];
        for (int i = 0; i < domainLabels.length; i++) {

            String value = String.valueOf((i+1)* Constants.INTERVAL_WIDTH_HALF);//**************
            if(value.contains(".5")){
                value = value.substring(0, value.indexOf("."));
                value += ":30";
            }else{
                value = value.substring(0, value.indexOf("."));
                if(value.equals("24")){
                    value = "00"+":00";
                }else{
                    value += ":00";
                }


            }

            domainLabels[i] = value;
        }
        intervalLabels30Min = domainLabels;
    }

    @NonNull
    public static String[] getStringFiveMinutesIntervals(int fullLengthSeries) {
        LocalTime time = LocalTime.of(0, 5);
        var intervalSize = 5;
        return getTimeIntervals(fullLengthSeries, time, intervalSize);
    }

    @NonNull
    public static String[] getStringHalfHourMinutesIntervals(int fullLengthSeries) {
        LocalTime time = LocalTime.of(0, 30);
        var intervalSize = 30;
        return getTimeIntervals(fullLengthSeries, time, intervalSize);
    }

    @NonNull
    public static String[] getTimeIntervals(int fullLengthSeries, LocalTime time, int intervalSize) {
        String[] minuteIntervals = new String[fullLengthSeries];
        for(int i = 0; i< fullLengthSeries; i++){
            minuteIntervals[i] = time.toString();
            time = time.plusMinutes(intervalSize);
        }
        return minuteIntervals;
    }


    public static int getInterval(TimeData timeData, double intervalWidth){
        double intervalDouble = (timeData.getHour() + timeData.getMinute()/60.0)/intervalWidth;
        if(intervalDouble==0) intervalDouble++;
        return  (int) Math.ceil(intervalDouble);
    }


    public static int getInterval5Min(int hour, int minute){
        double intervalDouble = 12*(hour + minute/60.0)+1;
        if(intervalDouble==0) intervalDouble++;
        return  (int) Math.floor(intervalDouble);
    }

}
