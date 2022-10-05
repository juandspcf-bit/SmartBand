package com.misawabus.project.heartRate.plotting;

import static java.util.stream.Collectors.toList;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.Utils.DateUtils;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlotUtilsSleep {


    private static final int wakeUpColor = Color.rgb(255, 255, 0);;
    private static final int insomniaColor = Color.rgb(255, 128, 128);
    private static final int rapidEyeMovementColor = Color.rgb(255, 153, 187);
    private static final int lightSleepColor = Color.rgb(77, 136, 255);
    private static final int deepSleepColor = Color.rgb(0, 34, 102);
    private static final String TAG = PlotUtilsSleep.class.getSimpleName();

    public static void plotSleepIntegerListData(SleepDataUI sleepDataUI,
                                                List<Integer> lightSleep,
                                                List<Integer> deepSleep,
                                                List<Integer> wakeUp,
                                                XYPlot plot) {

        plot.clear();
        float rangeUpperLimit = 3.0f;


        plot.setRangeUpperBoundary(rangeUpperLimit, BoundaryMode.FIXED);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, rangeUpperLimit / 3.0);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);

        Paint p = new Paint();
        p.setARGB(50, 0, 0, 255);
        plot.getGraph().setRangeGridLinePaint(p);

        PlotUtils.MyBarFormatter formatter0 = new PlotUtils.MyBarFormatter(lightSleepColor, lightSleepColor);
        XYSeries series0 = new SimpleXYSeries(lightSleep, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series0, formatter0);

        PlotUtils.MyBarFormatter formatter1 = new PlotUtils.MyBarFormatter(deepSleepColor, deepSleepColor);
        XYSeries series1 = new SimpleXYSeries(deepSleep, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series1, formatter1);

        PlotUtils.MyBarFormatter formatter2 = new PlotUtils.MyBarFormatter(wakeUpColor, wakeUpColor);
        XYSeries series2 = new SimpleXYSeries(wakeUp, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series2, formatter2);


        PlotUtils.MyBarRenderer renderer = plot.getRenderer(PlotUtils.MyBarRenderer.class);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, PixelUtils.dpToPix(0));

        String sleepDownTimeData = sleepDataUI.getSleepDown();
        sleepDownTimeData = sleepDownTimeData.substring(sleepDownTimeData.lastIndexOf("[") + 1, sleepDownTimeData.lastIndexOf("]"));
        String sleepDownTime = sleepDownTimeData.split(" ")[1];
        String[] sleepDownTimeSplit = sleepDownTime.split(":");

        LocalTime sleepDownLocalTime = LocalTime.of(Integer.parseInt(sleepDownTimeSplit[0]),
                Integer.parseInt(sleepDownTimeSplit[1]),
                Integer.parseInt(sleepDownTimeSplit[2]));


        LocalTime sleepUpTimeLocalTime = DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepUp());


        List<LocalTime> axisTimes = new ArrayList<>();
        LocalTime difference = sleepUpTimeLocalTime.minusMinutes(sleepDownLocalTime.getMinute());
        difference = difference.minusHours(sleepDownLocalTime.getHour());
        difference = difference.minusSeconds(sleepDownLocalTime.getSecond());
        double time = difference.getHour() * 60.0
                + difference.getMinute()
                + difference.getSecond() / 60.0;
        double stepsSampling = Math.abs(time / sleepDataUI.getData().length());

        Stream<Integer> limit = Stream.iterate(0, j -> ++j).limit(lightSleep.size());
        if (stepsSampling > 1) {
            long minutesPlus = Math.round(stepsSampling);
            limit.forEach(index -> axisTimes
                    .add(sleepDownLocalTime.plusMinutes(minutesPlus * index)));
        } else {
            long secondsPLus = Math.round(stepsSampling * 60.0);
            limit.forEach(index -> axisTimes
                    .add(sleepDownLocalTime.plusSeconds(secondsPLus * index)));
        }


        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(axisTimes.get(i));
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        plot.redraw();


    }

    public static void plotSleepPrecisionIntegerListData(SleepDataUI sleepDataUI,
                                                         List<Integer> deepSleep,
                                                         List<Integer> lightSleep,
                                                         List<Integer> rapidEyeMovement,
                                                         List<Integer> insomnia,
                                                         List<Integer> wakeUp,
                                                         XYPlot plot) {

        plot.clear();
        float rangeUpperLimit = 5.0f;

        plot.setRangeUpperBoundary(rangeUpperLimit, BoundaryMode.FIXED);
        plot.setRangeStep(StepMode.SUBDIVIDE, 5);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);

        Paint p = new Paint();
        p.setARGB(50, 0, 0, 255);
        plot.getGraph().setRangeGridLinePaint(p);


        PlotUtils.MyBarFormatter formatter0 = new PlotUtils.MyBarFormatter(deepSleepColor, deepSleepColor);
        XYSeries series0 = new SimpleXYSeries(deepSleep, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series0, formatter0);


        PlotUtils.MyBarFormatter formatter1 = new PlotUtils.MyBarFormatter(lightSleepColor, lightSleepColor);
        XYSeries series1 = new SimpleXYSeries(lightSleep, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series1, formatter1);


        PlotUtils.MyBarFormatter formatter2 = new PlotUtils.MyBarFormatter(rapidEyeMovementColor, rapidEyeMovementColor);
        XYSeries series2 = new SimpleXYSeries(rapidEyeMovement, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series2, formatter2);


        PlotUtils.MyBarFormatter formatter3 = new PlotUtils.MyBarFormatter(insomniaColor, insomniaColor);
        XYSeries series3 = new SimpleXYSeries(insomnia, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series3, formatter3);


        PlotUtils.MyBarFormatter formatter4 = new PlotUtils.MyBarFormatter(wakeUpColor, wakeUpColor);
        XYSeries series4 = new SimpleXYSeries(wakeUp, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series4, formatter4);


        PlotUtils.MyBarRenderer renderer = plot.getRenderer(PlotUtils.MyBarRenderer.class);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, PixelUtils.dpToPix(0));

        String sleepDownTimeData = sleepDataUI.getSleepDown();
        sleepDownTimeData = sleepDownTimeData.substring(sleepDownTimeData.lastIndexOf("[") + 1, sleepDownTimeData.lastIndexOf("]"));
        String sleepDownTime = sleepDownTimeData.split(" ")[1];
        String[] sleepDownTimeSplit = sleepDownTime.split(":");

        LocalTime sleepDownLocalTime = LocalTime.of(Integer.parseInt(sleepDownTimeSplit[0]),
                Integer.parseInt(sleepDownTimeSplit[1]),
                Integer.parseInt(sleepDownTimeSplit[2]));


        LocalTime sleepUpTimeLocalTime = DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepUp());


        List<LocalTime> axisTimes = new ArrayList<>();
        LocalTime difference = sleepUpTimeLocalTime.minusMinutes(sleepDownLocalTime.getMinute());
        difference = difference.minusHours(sleepDownLocalTime.getHour());
        difference = difference.minusSeconds(sleepDownLocalTime.getSecond());
        double time = difference.getHour() * 60.0
                + difference.getMinute()
                + difference.getSecond() / 60.0;
        double stepsSampling = Math.abs(time / sleepDataUI.getData().length());

        Stream<Integer> limit = Stream.iterate(0, j -> ++j).limit(lightSleep.size());
        if (stepsSampling > 1) {
            long minutesPlus = Math.round(stepsSampling);
            limit.forEach(index -> axisTimes
                    .add(sleepDownLocalTime.plusMinutes(minutesPlus * index)));
        } else {
            long secondsPLus = Math.round(stepsSampling * 60.0);
            limit.forEach(index -> axisTimes
                    .add(sleepDownLocalTime.plusSeconds(secondsPLus * index)));
        }


        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(axisTimes.get(i));
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        plot.redraw();

    }


    @NonNull
    public static List<SleepDataUI> joinSleepListData(List<SleepDataUI> sortedSleepData) {
        Map<Integer,List<SleepDataUI>> map = new HashMap<>();
        Log.d(TAG, "List<SleepDataUI>:  " + sortedSleepData);

        int counter = 0;
        if(sortedSleepData.size()==1){
            map.computeIfAbsent(counter, k -> new ArrayList<>());
            map.get(counter).add(sortedSleepData.get(0));
        }else {
            for (int i = 0; i< sortedSleepData.size(); ++i) {
                if (i > 0) {
                    LocalTime localTimeSleepUp = DateUtils.getLocalTimeFromVeepooTimeDateObj(sortedSleepData.get(i - 1).getSleepUp());
                    LocalTime localTimeSleepDown = DateUtils.getLocalTimeFromVeepooTimeDateObj(sortedSleepData.get(i).getSleepDown());
                    int differenceTime = localTimeSleepDown.getHour()*60 + localTimeSleepDown.getMinute()
                            - localTimeSleepUp.getHour()*60 - localTimeSleepUp.getMinute();

                    List<SleepDataUI> sleepDataUIS = map.computeIfAbsent(counter, k -> new ArrayList<>());
                    sleepDataUIS.add(sortedSleepData.get(i - 1));
                    if (Math.abs(differenceTime) < 20 && i== sortedSleepData.size()-1) {
                        sleepDataUIS.add(sortedSleepData.get(i));
                    } else if(Math.abs(differenceTime) >= 20 && i== sortedSleepData.size()-1){
                        ++counter;
                        map.computeIfAbsent(counter, k -> new ArrayList<>());
                        map.get(counter).add(sortedSleepData.get(i));
                    }else if(Math.abs(differenceTime) >= 20){
                        ++counter;
                    }
                }
            }
        }


        Log.d(TAG, "joinSleepListData: map   " + map);

        List<SleepDataUI> joinData = new ArrayList<>();
        map.forEach((k, v)->{
            List<SleepDataUI> sleepDataUIS1 = map.get(k);
            sleepDataUIS1.forEach(sleepDataUI -> Log.d(TAG, "setFragmentViews: " +sleepDataUI.getSleepUp() + " : " + sleepDataUI.getSleepDown()));
            Log.d(TAG, "setFragmentViews: joinData  "+ k + " : " + v.size());
            if(sleepDataUIS1.size()>1){
                SleepDataUI sleepDataUI = new SleepDataUI();

                String sleepDown = sleepDataUIS1.get(0).getSleepDown();
                Log.d(TAG, "setFragmentViews: sleepDown " + sleepDown);
                String  sleepUp = sleepDataUIS1.get(sleepDataUIS1.size() - 1).getSleepUp();
                Log.d(TAG, "setFragmentViews: sleepUp " + sleepUp);
                sleepDataUI.setSleepUp(sleepUp);
                sleepDataUI.setSleepDown(sleepDown);

                String JoinDataSleepLine = sleepDataUIS1.stream().map(SleepDataUI::getData).collect(Collectors.joining());
                sleepDataUI.setData(JoinDataSleepLine);

                sleepDataUI.setIdTypeDataTable( sleepDataUIS1.get(0).idTypeDataTable);

                double avgScoreSleep = sleepDataUIS1.stream().map(SleepDataUI::getSleepQuality).mapToInt(Integer::intValue).average().orElse(0);
                sleepDataUI.setSleepQuality((int) Math.ceil(avgScoreSleep));

                long countWakeUp = sleepDataUIS1.stream().map(SleepDataUI::getWakeCount).mapToInt(Integer::intValue).count();
                sleepDataUI.setWakeCount((int) countWakeUp);

                joinData.add(sleepDataUI);

            }else {
                joinData.add(sleepDataUIS1.get(0));
            }
        });
        return joinData;
    }

    public static List<SleepDataUI> sortSleepListData(List<SleepDataUI> sleepDataUIS) {
        List<Integer> collect = sleepDataUIS.stream().map(sleepDataUI -> {
            LocalTime localTimeFromVeepooTimeDateObj;
            localTimeFromVeepooTimeDateObj =
                    DateUtils.getLocalTimeFromVeepooTimeDateObj(sleepDataUI.getSleepDown());
            return localTimeFromVeepooTimeDateObj.toSecondOfDay();

        }).collect(toList());

        Log.d(TAG, "collect.size() " + collect.size());
        Map<Integer, SleepDataUI> mapSleep = new HashMap<>();
        Stream.iterate(0, i -> ++i).limit(collect.size()).forEach(index -> {
            Log.d(TAG, "Stream.iterate: " + collect.get(index) + "  index: " + index);

            mapSleep.put(collect.get(index), sleepDataUIS.get(index));
        });

        Collections.sort(collect);

        List<SleepDataUI> sortedSleepData = collect
                .stream()
                .map(mapSleep::get)
                .collect(toList());
        return sortedSleepData;
    }
}
