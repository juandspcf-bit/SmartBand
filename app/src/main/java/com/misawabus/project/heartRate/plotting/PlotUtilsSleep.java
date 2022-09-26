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

        PlotUtils.MyBarFormatter formatter0 = new PlotUtils.MyBarFormatter(Color.rgb(187, 60, 230), Color.rgb(187, 60, 230));
        XYSeries series0 = new SimpleXYSeries(lightSleep, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series0, formatter0);

        PlotUtils.MyBarFormatter formatter1 = new PlotUtils.MyBarFormatter(Color.rgb(13, 33, 128), Color.rgb(13, 33, 128));
        XYSeries series1 = new SimpleXYSeries(deepSleep, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series1, formatter1);

        PlotUtils.MyBarFormatter formatter2 = new PlotUtils.MyBarFormatter(Color.rgb(116, 230, 59), Color.rgb(116, 230, 59));
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


        LocalTime sleepUpTimeLocalTime = DateUtils.getTimeFromVeepooTimeDateObj(sleepDataUI.getSleepUp());


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
