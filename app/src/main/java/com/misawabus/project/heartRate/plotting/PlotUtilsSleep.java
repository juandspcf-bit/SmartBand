package com.misawabus.project.heartRate.plotting;

import android.graphics.Color;
import android.graphics.Paint;

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
import java.util.List;
import java.util.stream.Stream;

public class PlotUtilsSleep {


    private static final int wakeUpColor = Color.rgb(255, 255, 0);;
    private static final int insomniaColor = Color.rgb(255, 128, 128);
    private static final int rapidEyeMovementColor = Color.rgb(255, 153, 187);
    private static final int lightSleepColor = Color.rgb(77, 136, 255);
    private static final int deepSleepColor = Color.rgb(0, 34, 102);

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
}
