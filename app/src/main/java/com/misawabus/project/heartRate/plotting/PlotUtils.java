package com.misawabus.project.heartRate.plotting;

import static com.misawabus.project.heartRate.fragments.summaryFragments.utils.UtilsSummaryFrag.interpolateSeries;
import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.toList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;

import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CandlestickFormatter;
import com.androidplot.xy.CandlestickMaker;
import com.androidplot.xy.CandlestickSeries;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.Utils.Utils;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Stream;

public class PlotUtils {

    private static final String TAG = PlotUtils.class.getSimpleName();
    private static MyBarFormatter selectionFormatter;
    private static PlotUtils plotUtils;
    private static CustomPlot xyCustomPlotForSummarySteps;

    public PlotUtils() {

    }

    public static PlotUtils getInstance() {
        if (PlotUtils.plotUtils == null) {
            return new PlotUtils();
        }
        return PlotUtils.plotUtils;
    }

    @NonNull
    public static XYDataArraysForPlotting get5MinFieldXYDataArraysForPlotting(List<Map<String, Double>> mapsSop2,
                                                                              String field) {
        Double[] subArrayWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(mapsSop2, field);
        int lengthSubArray = subArrayWithReplacedZeroValuesAsAvg.length;
        String[] timeAxisSubArray = IntervalUtils.getStringFiveMinutesIntervals(lengthSubArray);

        return new XYDataArraysForPlotting(timeAxisSubArray, subArrayWithReplacedZeroValuesAsAvg);
    }

    @NonNull
    public static XYDataArraysForPlotting get30MinFieldXYDataArraysForPlotting(List<Map<String, Double>> data,
                                                                               String field) {
        Double[] subArrayWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(data, field);
        int lengthSubArray = subArrayWithReplacedZeroValuesAsAvg.length;
        String[] timeAxisSubArray = IntervalUtils.getStringHalfHourMinutesIntervals(lengthSubArray);

        return new XYDataArraysForPlotting(timeAxisSubArray, subArrayWithReplacedZeroValuesAsAvg);
    }

    @NonNull
    public static Double[] getSubArrayWithReplacedZeroValuesAsAvg(List<Map<String, Double>> data, String field) {
        int indexOfLastNonZeroElement = getIndexOfLastNonZeroElement(data, field);
        Double[] subArray = getSubArrayFromFieldList(data, field, indexOfLastNonZeroElement);
        return setArrayZeroValuesWithAvg(subArray);
    }

    @NonNull
    private static Double[] getSubArrayFromFieldList(List<Map<String, Double>> mapsSop2, String field, int indexOfLastNonZeroElement) {
        List<Map<String, Double>> maps = mapsSop2.subList(0, indexOfLastNonZeroElement + 1);

        return maps
                .stream()
                .map(doubleMap -> doubleMap.get(field)).toArray(Double[]::new);
    }

    @NonNull
    private static Double[] setArrayZeroValuesWithAvg(Double[] subArray) {
        int lengthSubArray = subArray.length;

        Double average = Arrays
                .stream(subArray)
                .filter(value -> value != null && value > 20.0)
                .collect(averagingDouble(Double::doubleValue));

        subArray = Arrays.stream(subArray)
                .map(value -> value != null && value > 20.0 ? value : average)
                .collect(toList()).toArray(new Double[lengthSubArray]);

        Double[] numericalTimeAxisSubArray = new Double[lengthSubArray];
        var seriesList = new ArrayList<List<Double>>();
        if (lengthSubArray > 3)
            interpolateSeries(subArray, numericalTimeAxisSubArray, lengthSubArray, seriesList);

        return subArray;


    }


    private static int getIndexOfLastNonZeroElement(@NonNull List<Map<String, Double>> data, String field) {
        ListIterator<Map<String, Double>> mapListIterator = data.listIterator(data.size());
        int index = 0;

        while (mapListIterator.hasPrevious()) {
            Map<String, Double> previous = mapListIterator.previous();
            Double value = previous.get(field);
            if (value != null && value > 0.0) {
                index = mapListIterator.previousIndex() + 1;
                break;
            }
        }
        return index;
    }


    public static void setRangeMargins(int rangeStepsUpperLimit, XYPlot plot) {
        if (rangeStepsUpperLimit > 10000) {
            plot.getGraph().setMarginLeft(80);
        } else if (rangeStepsUpperLimit > 1000) {
            plot.getGraph().setMarginLeft(60);
        } else if (rangeStepsUpperLimit > 100) {
            plot.getGraph().setMarginLeft(50);
        } else if (rangeStepsUpperLimit > 10) {
            plot.getGraph().setMarginLeft(25);
        }
    }


    public void processingDoublesIntervalsBP(String[] domainLabels,
                                             Double[] integerHPSeries,
                                             Double[] integerLPSeries,
                                             XYPlot plot,
                                             Context context) {
        plot.clear();
        CandlestickSeries candlestickSeries = new CandlestickSeries();
        SimpleXYSeries simpleHYSeries = new SimpleXYSeries(Arrays.asList(integerHPSeries), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "HP");
        SimpleXYSeries simpleLYSeries = new SimpleXYSeries(Arrays.asList(integerLPSeries), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "LP");


        LineAndPointFormatter lpfH =
                new LineAndPointFormatter(Color.TRANSPARENT,
                        Color.rgb(255, 100, 100),
                        Color.TRANSPARENT,
                        null);
        lpfH.getLinePaint().setStrokeWidth(32.0f);
        lpfH.getVertexPaint().setStrokeWidth(16.0f);

        LineAndPointFormatter lpfL =
                new LineAndPointFormatter(Color.TRANSPARENT,
                        Color.rgb(57, 73, 171),
                        Color.TRANSPARENT,
                        null);
        lpfL.getLinePaint().setStrokeWidth(32.0f);
        lpfL.getVertexPaint().setStrokeWidth(16.0f);

        //LineAndPointFormatter lpfH = new LineAndPointFormatter(context, R.xml.line_point_formatter_high_pressure);
        //LineAndPointFormatter lpfL = new LineAndPointFormatter(context, R.xml.line_point_formatter_low_pressure);

        candlestickSeries.setHighSeries(simpleHYSeries);
        candlestickSeries.setLowSeries(simpleLYSeries);
        candlestickSeries.setCloseSeries(simpleHYSeries);
        candlestickSeries.setOpenSeries(simpleLYSeries);

        CandlestickFormatter formatter = new CandlestickFormatter();

        Paint wickPaint = new Paint();
        wickPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        wickPaint.setARGB(100, 66, 165,245);
        formatter.setWickPaint(wickPaint);

        Paint upperCapPaint = new Paint();
        upperCapPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        upperCapPaint.setARGB(100, 255, 100,100);
        formatter.setUpperCapPaint(upperCapPaint);

        Paint lowerCapPaint = new Paint();
        lowerCapPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        lowerCapPaint.setARGB(100, 57, 73,171);
        formatter.setLowerCapPaint(lowerCapPaint);

        Paint risingBodyStrokePaint = new Paint();
        risingBodyStrokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        risingBodyStrokePaint.setColor(Color.TRANSPARENT);
        formatter.setRisingBodyStrokePaint(risingBodyStrokePaint);

        Paint fallingBodyFillPaint = new Paint();
        fallingBodyFillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        fallingBodyFillPaint.setARGB(100, 66, 165, 245);
        formatter.setFallingBodyFillPaint(fallingBodyFillPaint);

        //CandlestickFormatter formatter = new CandlestickFormatter(context, R.xml.candlestick_formatter);

        // draw candlestick bodies as triangles instead of squares:
        // triangles will point up for items that closed higher than they opened
        // and down for those that closed lower:
        formatter.setBodyStyle(CandlestickFormatter.BodyStyle.SQUARE);
        formatter.setBodyWidth(10.0f);
        formatter.setUpperCapWidth(0.0f);
        formatter.setLowerCapWidth(0.0f);


        // add the candlestick series data to the plot:
        CandlestickMaker.make(plot, formatter, candlestickSeries);

        // setup the range tick label formatting, etc:

        double rangeUpperLimit = Collections.max(Arrays.asList(integerHPSeries));
        Paint p = new Paint();
        p.setARGB(50, 0, 0, 255);
        plot.getGraph().setRangeGridLinePaint(p);

        rangeUpperLimit = rangeUpperLimit * 1.1;
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, rangeUpperLimit / 3.0);
        plot.setRangeUpperBoundary(rangeUpperLimit, BoundaryMode.FIXED);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);


        plot.redraw();

        plot.addSeries(candlestickSeries.getCloseSeries(), lpfH);
        plot.addSeries(candlestickSeries.getLowSeries(), lpfL);


        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(i);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
        plot.redraw();
    }

    static class MyBarFormatter extends BarFormatter {

        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer doGetRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    static class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if (PlotUtilsSports.selection != null &&
                    PlotUtilsSports.selection.second == series &&
                    PlotUtilsSports.selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }


}
