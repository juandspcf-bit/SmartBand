package com.misawabus.project.heartRate.plotting;

import static com.misawabus.project.heartRate.fragments.summaryFragments.utils.UtilsSummaryFrag.interpolateSeries;
import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.toList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import androidx.annotation.NonNull;

import com.androidplot.Plot;
import com.androidplot.PlotListener;
import com.androidplot.Region;
import com.androidplot.ui.Anchor;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.SeriesBundle;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.TextOrientation;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.widget.TextLabelWidget;
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
import com.androidplot.xy.XYSeriesFormatter;
import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.R;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.Utils.Utils;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;

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
    private static Pair<Integer, XYSeries> selection;
    private static MyBarFormatter selectionFormatter;
    private static PlotUtils plotUtils;
    private static SimpleXYSeries seriesBarRepresentation;
    private static XYPlot xyPlotForSummarySteps;
    private static CustomPlot xyCustomPlotForSummarySteps;
    private static Double[] seriesStepsInit;
    private static SimpleXYSeries seriesPlotRepresentation;
    private TextLabelWidget selectionWidget;
    private Double[] seriesStepsInit3;

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
        String[] timeAxisSubArray =IntervalUtils.getStringFiveMinutesIntervals(lengthSubArray);

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
                .filter(value -> value!=null && value>20.0)
                .collect(averagingDouble(Double::doubleValue));

        subArray = Arrays.stream(subArray)
                .map(value -> value!=null && value>20.0 ? value : average)
                .collect(toList()).toArray(new Double[lengthSubArray]);

        Double[] numericalTimeAxisSubArray = new Double[lengthSubArray];
        var seriesList = new ArrayList<List<Double>>();
        if (lengthSubArray > 3)
            interpolateSeries(subArray, numericalTimeAxisSubArray, lengthSubArray, seriesList);

        return subArray;


    }


    private static int getIndexOfLastNonZeroElement(List<Map<String, Double>> data, String field) {
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

    public void initSeriesForSummarySteps(XYPlot plotI, Context context) {

        xyPlotForSummarySteps = plotI;
        int intervalsTotal = (int) Math.round(24.0 / Utils.INTERVAL_WIDTH_HALF);

        seriesStepsInit = new Double[intervalsTotal];
        Arrays.fill(seriesStepsInit, 0.0);
        List<Double> doubles = Arrays.asList(seriesStepsInit);



        MyBarFormatter formatterForSeriesBarRepresentation;
        int fillColorForBarRepresentation = Color.rgb(115, 8, 250);
        int borderColorForBarRepresentation = Color.rgb(115, 8, 250);
        formatterForSeriesBarRepresentation = new MyBarFormatter(fillColorForBarRepresentation,
                borderColorForBarRepresentation);
        seriesBarRepresentation = new SimpleXYSeries(List.copyOf(doubles), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");
        xyPlotForSummarySteps.addSeries(seriesBarRepresentation, formatterForSeriesBarRepresentation);

        LineAndPointFormatter formatterForSeriesPlotRepresentation = new LineAndPointFormatter(context, R.xml.line_point_formatter_for_summary_sports);
        formatterForSeriesPlotRepresentation.setInterpolationParams(
                new CatmullRomInterpolator.Params(2, CatmullRomInterpolator.Type.Uniform));
        seriesPlotRepresentation = new SimpleXYSeries(List.copyOf(doubles), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series3");
        xyPlotForSummarySteps.addSeries(seriesPlotRepresentation, formatterForSeriesPlotRepresentation);

        MyBarRenderer renderer = xyPlotForSummarySteps.getRenderer(MyBarRenderer.class);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(8));

        selectionWidget = new TextLabelWidget(xyPlotForSummarySteps.getLayoutManager(), "",
                new Size(
                        PixelUtils.dpToPix(0), SizeMode.ABSOLUTE,
                        0, SizeMode.RELATIVE),
                TextOrientation.HORIZONTAL);

        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(16));
        Paint p1 = new Paint();
        p1.setARGB(50, 0, 0, 255);
        xyPlotForSummarySteps.getGraph().setRangeGridLinePaint(p1);

        selectionWidget.getLabelPaint().setColor(Color.DKGRAY);
        Paint p = new Paint();
        p.setARGB(0, 245, 10, 223);
        selectionWidget.setBackgroundPaint(p);

        selectionWidget.position(
                PixelUtils.dpToPix(200), HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                PixelUtils.dpToPix(5), VerticalPositioning.ABSOLUTE_FROM_TOP,
                Anchor.TOP_MIDDLE);
        selectionWidget.pack();

        OnTouchListener listener = (view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                PlotUtils.this.onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()), xyPlotForSummarySteps, selectionWidget);

            }
            return true;
        };

        xyPlotForSummarySteps.setOnTouchListener(listener);

        xyPlotForSummarySteps.addListener(new PlotListener() {
            @Override
            public void onBeforeDraw(Plot plot, Canvas canvas) {

            }
            @Override
            public void onAfterDraw(Plot plot, Canvas canvas) {
                selectionWidget.setText("");
            }
        });


    }

    public void processingIntervalsSummarySteps(Double[] seriesSteps) {


        String[] domainLabels = IntervalUtils.hoursInterval;
        double rangeStepsUpperLimit = Collections.max(Arrays.asList(seriesSteps));
        int i1 = setRangeDomain(domainLabels, (int) rangeStepsUpperLimit, xyPlotForSummarySteps);
        setRangeMargins(domainLabels, i1, xyPlotForSummarySteps);


        int intervalsTotal = (int) Math.round(24.0 / Utils.INTERVAL_WIDTH_HALF);
        Stream.iterate(0, i -> ++i).limit(intervalsTotal - 1)
                .forEach(data -> {
                    seriesBarRepresentation.setY(seriesSteps[data], data);
                    seriesPlotRepresentation.setY(seriesSteps[data] * 1.3, data);
                });


        xyPlotForSummarySteps.redraw();

    }


    public void plotStepsDoubleIntervalsData(String[] domainLabels, Double[] seriesSteps, XYPlot plot) {
        plot.clear();
        double rangeStepsUpperLimit = Collections.max(Arrays.asList(seriesSteps));
        setRangeDomain(domainLabels, (int)rangeStepsUpperLimit, plot);
        setRangeMargins(domainLabels, (int) rangeStepsUpperLimit, plot);

        XYSeries series1 = new SimpleXYSeries(Arrays.asList(seriesSteps), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        MyBarFormatter formatter1 = new MyBarFormatter(Color.rgb(115, 8, 250), Color.rgb(115, 8, 250));
        plot.addSeries(series1, formatter1);

        MyBarRenderer renderer = plot.getRenderer(MyBarRenderer.class);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(8));
        plot.redraw();
    }

    private int setRangeDomain(String[] domainLabels, int rangeStepsUpperLimit, XYPlot plot) {
        if (rangeStepsUpperLimit > 1000) {

            double dRangeLimit = Math.floor(rangeStepsUpperLimit);
            rangeStepsUpperLimit = (int) (dRangeLimit * 1.2);
            plot.setRangeStep(StepMode.INCREMENT_BY_VAL, rangeStepsUpperLimit / 3.0);
        } else if (rangeStepsUpperLimit > 100) {
            double dRangeLimit = Math.floor(rangeStepsUpperLimit);
            rangeStepsUpperLimit = (int) (dRangeLimit * 1.2);
        } else if (rangeStepsUpperLimit == 0) {
            rangeStepsUpperLimit = 100;
        }
        Paint p = new Paint();
        p.setARGB(50, 0, 0, 255);
        plot.getGraph().setRangeGridLinePaint(p);

        plot.setRangeUpperBoundary(rangeStepsUpperLimit, BoundaryMode.FIXED);


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

        return  rangeStepsUpperLimit;

    }


    private void setRangeMargins(String[] domainLabels, int rangeStepsUpperLimit, XYPlot plot) {
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


    public void plotHeartRateDoubleIntervalsData(String[] domainLabels, Double[] rangeDouble, XYPlot plot, Context context) {
        plot.clear();

        XYSeries series1 = new SimpleXYSeries(Arrays.asList(rangeDouble), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        LineAndPointFormatter series1Format = new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels_hr_summary);

        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(2, CatmullRomInterpolator.Type.Centripetal));
        plot.addSeries(series1, series1Format);

        double rangeUpperLimit = Collections.max(Arrays.asList(rangeDouble));

        rangeUpperLimit = rangeUpperLimit * 1.1;
        setRangeMargins(domainLabels, (int) rangeUpperLimit, plot);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, rangeUpperLimit / 7.0);
        plot.setRangeUpperBoundary(rangeUpperLimit, BoundaryMode.FIXED);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
        Paint p = new Paint();
        p.setARGB(50, 0, 0, 255);
        plot.getGraph().setRangeGridLinePaint(p);

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

        plot.setVisibility(View.VISIBLE);
        plot.redraw();


    }

    public void plotSop2DoubleIntervalsData(String[] domainLabels, Double[] rangeDouble, XYPlot plot, Context context) {
        plot.clear();

        XYSeries series1 = new SimpleXYSeries(Arrays.asList(rangeDouble), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        LineAndPointFormatter series1Format = new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels_hr_summary);

        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(2, CatmullRomInterpolator.Type.Centripetal));
        plot.addSeries(series1, series1Format);


        double rangeUpperLimit = 100.0;
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, rangeUpperLimit / 10);
        plot.setRangeUpperBoundary(rangeUpperLimit, BoundaryMode.FIXED);
        plot.setRangeLowerBoundary(70, BoundaryMode.FIXED);
        Paint p = new Paint();
        p.setARGB(50, 0, 0, 255);
        plot.getGraph().setRangeGridLinePaint(p);

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
                Log.d(TAG, "format: i: " + i);
                return toAppendTo.append(i);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        plot.setVisibility(View.VISIBLE);
        plot.redraw();


    }


    public void processingDoublesIntervalsBP(String[] domainLabels, Double[] integerHPSeries, Double[] integerLPSeries, XYPlot plot, Context context) {
        plot.clear();
        CandlestickSeries candlestickSeries = new CandlestickSeries();
        SimpleXYSeries simpleHYSeries = new SimpleXYSeries(Arrays.asList(integerHPSeries), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "HP");
        SimpleXYSeries simpleLYSeries = new SimpleXYSeries(Arrays.asList(integerLPSeries), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "LP");

        LineAndPointFormatter lpfH = new LineAndPointFormatter(context, R.xml.line_point_formatter_high_pressure);
        LineAndPointFormatter lpfL = new LineAndPointFormatter(context, R.xml.line_point_formatter_low_pressure);

        candlestickSeries.setHighSeries(simpleHYSeries);
        candlestickSeries.setLowSeries(simpleLYSeries);
        candlestickSeries.setCloseSeries(simpleHYSeries);
        candlestickSeries.setOpenSeries(simpleLYSeries);


        CandlestickFormatter formatter = new CandlestickFormatter(context, R.xml.candlestick_formatter);

        // draw candlestick bodies as triangles instead of squares:
        // triangles will point up for items that closed higher than they opened
        // and down for those that closed lower:
        formatter.setBodyStyle(CandlestickFormatter.BodyStyle.SQUARE);
        formatter.setBodyWidth(0.0f);
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

    private void onPlotClicked(PointF point, XYPlot plot, TextLabelWidget selectionWidget) {

        // make sure the point lies within the graph area.  we use grid_rect
        // because it accounts for margins and padding as well.
        if (plot.containsPoint(point.x, point.y)) {
            selectionWidget.setSize(new Size(
                    PixelUtils.dpToPix(100), SizeMode.ABSOLUTE,
                    0, SizeMode.RELATIVE));
            Number x = plot.getXVal(point);
            Number y = plot.getYVal(point);

            selection = null;
            double xDistance = 0;
            double yDistance = 0;

            // find the closest value to the selection:
            for (SeriesBundle<XYSeries, ? extends XYSeriesFormatter> sfPair : plot
                    .getRegistry().getSeriesAndFormatterList()) {
                XYSeries series = sfPair.getSeries();
                for (int i = 0; i < series.size(); i++) {
                    Number thisX = series.getX(i);
                    Number thisY = series.getY(i);
                    if (thisX != null && thisY != null) {
                        double thisXDistance =
                                Region.measure(x, thisX).doubleValue();
                        double thisYDistance =
                                Region.measure(y, thisY).doubleValue();
                        if (selection == null) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance < xDistance) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance == xDistance &&
                                thisYDistance < yDistance &&
                                thisY.doubleValue() >= y.doubleValue()) {
                            selection = new Pair<>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        }
                    }
                }
            }



        } else {
            // if the press was outside the graph area, deselect:
            selection = null;

        }

        if (selection == null) {
            selectionWidget.setText("");
            selectionWidget.setSize(new Size(
                    PixelUtils.dpToPix(0), SizeMode.ABSOLUTE,
                    0, SizeMode.RELATIVE));

        } else {
            selectionWidget.position(
                    PixelUtils.dpToPix(200), HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                    PixelUtils.dpToPix(5), VerticalPositioning.ABSOLUTE_FROM_TOP,
                    Anchor.TOP_MIDDLE);
            int position = PlotUtils.selection.second.getX(selection.first).intValue();

            String dataS;
            if (position != (IntervalUtils.hoursInterval.length - 1)) {
                dataS = IntervalUtils.hoursInterval[position] + " - " + IntervalUtils.hoursInterval[position + 1];
            } else {
                dataS = IntervalUtils.hoursInterval[position] + " - " + "00:00";
            }
            selectionWidget.setText(selection.second.getY(selection.first) + " steps at " + "\n" + dataS);


        }
        plot.redraw();
    }

    public void processingStringIntervalsSleep(List<Integer> collect0,
                                               List<Integer> collect1,
                                               List<Integer> collect2,
                                               XYPlot plot,
                                               SleepDataUI sleepDataUI) {

        plot.clear();
        float rangeUpperLimit = 3.0f;


        plot.setRangeUpperBoundary(rangeUpperLimit, BoundaryMode.FIXED);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, rangeUpperLimit / 3.0);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);

        Paint p = new Paint();
        p.setARGB(50, 0, 0, 255);
        plot.getGraph().setRangeGridLinePaint(p);

        MyBarFormatter formatter0 = new MyBarFormatter(Color.rgb(187, 60, 230), Color.rgb(187, 60, 230));
        XYSeries series0 = new SimpleXYSeries(collect0, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series0, formatter0);

        MyBarFormatter formatter1 = new MyBarFormatter(Color.rgb(13, 33, 128), Color.rgb(13, 33, 128));
        XYSeries series1 = new SimpleXYSeries(collect1, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series1, formatter1);

        MyBarFormatter formatter2 = new MyBarFormatter(Color.rgb(116, 230, 59), Color.rgb(116, 230, 59));
        XYSeries series2 = new SimpleXYSeries(collect2, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        plot.addSeries(series2, formatter2);


        MyBarRenderer renderer = plot.getRenderer(MyBarRenderer.class);
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

        Stream<Integer> limit = Stream.iterate(0, j -> ++j).limit(collect0.size());
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
            if (selection != null &&
                    selection.second == series &&
                    selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }


}
