package com.misawabus.project.heartRate.Intervals;

import androidx.annotation.NonNull;

import com.misawabus.project.heartRate.Utils.Utils;
import com.veepoo.protocol.model.datas.TimeData;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class IntervalUtils {
    public static final String[] hoursInterval;
    private static final String TAG = IntervalUtils.class.getSimpleName();

    static {
        int intervalsTotal = (int) Math.round(24.0/ Utils.INTERVAL_WIDTH_HALF);
        final String[] domainLabels = new String[intervalsTotal];
        for (int i = 0; i < domainLabels.length; i++) {

            String value = String.valueOf(i*Utils.INTERVAL_WIDTH_HALF);
            if(value.contains(".5")){
                value = value.substring(0, value.indexOf("."));
                value += ":30";
            }else{
                value = value.substring(0, value.indexOf("."));
                value += ":00";
            }

            domainLabels[i] = value;
        }
        hoursInterval = domainLabels;
    }


    public static int getInterval(TimeData timeData, double intervalWidth){
        double intervalDouble = (timeData.getHour() + timeData.getMinute()/60.0)/intervalWidth;
        if(intervalDouble==0) intervalDouble++;
        return  (int) Math.ceil(intervalDouble);
    }
    public static int getInterval(int hour, int minute, double intervalWidth){
        double intervalDouble = (hour + minute/60.0)/intervalWidth;
        if(intervalDouble==0) intervalDouble++;
        return  (int) Math.ceil(intervalDouble);
    }

    public static int getInterval5Min(int hour, int minute){
        double intervalDouble = 12*(hour + minute/60.0)+1;
        if(intervalDouble==0) intervalDouble++;
        return  (int) Math.floor(intervalDouble);
    }

    public static int getInterval30Min(int hour, int minute){
        double intervalDouble = 2*(hour + minute/60.0)+1;
        if(intervalDouble==0) intervalDouble++;
        return  (int) Math.floor(intervalDouble);
    }

    public static Double[] getDoubleSeries(String data, double sizeInterval) {

        int intervalsTotal = (int) Math.round(24.0/ sizeInterval);
        return getDoubles(data, intervalsTotal);
    }

    @NonNull
    public static Double[] getDoubles(String data, int intervalsTotal) {
        data = data.substring(1, data.length()-1);
        Double[] doubleSeries = new Double[intervalsTotal];
        Arrays.fill(doubleSeries, 0.0);

        String[] intervalValuePar = data.split(", ");

        Arrays.stream(intervalValuePar).forEach(intValPar -> {
            String[] div = intValPar.split("=");
            int interval = Integer.parseInt(div[0]);
            doubleSeries[interval-1] = Double.valueOf(div[1]);
        });

        return doubleSeries;
    }

    public static Integer[] getIntegerSeries(String data){
        data = data.substring(1, data.length()-1);
        int intervalsTotal = (int) Math.round(24.0/ Utils.INTERVAL_WIDTH_HALF);
        Integer[] integerSeries = new Integer[intervalsTotal];
        Arrays.fill(integerSeries, 0);

        String[] intervalValuePar = data.split(", ");

        Arrays.stream(intervalValuePar).forEach(intValPar -> {
            String[] div = intValPar.split("=");
            int interval = Integer.parseInt(div[0]);
            integerSeries[interval-1] = Integer.parseInt(div[1]);
        });
        return integerSeries;
    }

    public static Map<Integer, Integer> generateEmptyDataInteger(){
        Map<Integer, Integer> collectInteger = new HashMap<>(48);
        Stream.iterate(1, i->++i).limit(48)
                .forEach(interval -> collectInteger.put(interval, 0));
        return collectInteger;

    }

    public static Map<Integer, Double> generateEmptyDataDouble(){
        Map<Integer, Double> collectDouble = new HashMap<>(48);
        Stream.iterate(1, i->++i).limit(48)
                .forEach(interval -> collectDouble.put(interval, 0.0));
        return collectDouble;
    }

    @NonNull
    public static String[] getStringFiveMinutesIntervals(int fullLengthSeries) {
        LocalTime time = LocalTime.of(0, 0);
        var intervalSize = 5;
        return getTimeIntervals(fullLengthSeries, time, intervalSize);
    }

    @NonNull
    public static String[] getStringHalfHourMinutesIntervals(int fullLengthSeries) {
        LocalTime time = LocalTime.of(0, 0);
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
}
