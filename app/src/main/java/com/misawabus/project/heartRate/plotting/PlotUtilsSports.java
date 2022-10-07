package com.misawabus.project.heartRate.plotting;

import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.androidplot.Plot;
import com.androidplot.PlotListener;
import com.androidplot.Region;
import com.androidplot.ui.Anchor;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.SeriesBundle;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.TextOrientation;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;
import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.Utils.Utils;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class PlotUtilsSports {


    private static final String TAG = PlotUtilsSports.class.getSimpleName();
    static Pair<Integer, XYSeries> selection;
    static SimpleXYSeries seriesBarRepresentation;
    static XYPlot xyPlotForSummarySteps;
    private static Double[] seriesStepsInit;
    static SimpleXYSeries seriesPlotRepresentation;
    private static TextLabelWidget selectionWidget;

    public static void initSeriesForSummarySteps(XYPlot plotI) {

        xyPlotForSummarySteps = plotI;
        int intervalsTotal = (int) Math.round(24.0 / Utils.INTERVAL_WIDTH_HALF);

        seriesStepsInit = new Double[intervalsTotal];
        Arrays.fill(seriesStepsInit, 0.0);
        List<Double> doubles = Arrays.asList(seriesStepsInit);


        PlotUtils.MyBarFormatter formatterForSeriesBarRepresentation;
        int fillColorForBarRepresentation = Color.rgb(115, 8, 250);
        int borderColorForBarRepresentation = Color.rgb(115, 8, 250);
        formatterForSeriesBarRepresentation = new PlotUtils.MyBarFormatter(fillColorForBarRepresentation,
                borderColorForBarRepresentation);
        seriesBarRepresentation = new SimpleXYSeries(List.copyOf(doubles), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");
        xyPlotForSummarySteps.addSeries(seriesBarRepresentation, formatterForSeriesBarRepresentation);

        LineAndPointFormatter formatterForSeriesPlotRepresentation =
                new LineAndPointFormatter(Color.rgb(251, 137, 137),
                        Color.rgb(255, 100, 100),
                        Color.TRANSPARENT,
                        null);
        formatterForSeriesPlotRepresentation.getLinePaint().setStrokeWidth(8.0f);
        formatterForSeriesPlotRepresentation.getVertexPaint().setStrokeWidth(8.0f);
        formatterForSeriesPlotRepresentation.setInterpolationParams(
                new CatmullRomInterpolator.Params(2, CatmullRomInterpolator.Type.Uniform));
        seriesPlotRepresentation = new SimpleXYSeries(List.copyOf(doubles), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series3");
        xyPlotForSummarySteps.addSeries(seriesPlotRepresentation, formatterForSeriesPlotRepresentation);

        PlotUtils.MyBarRenderer renderer = xyPlotForSummarySteps.getRenderer(PlotUtils.MyBarRenderer.class);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(8));

        Paint p1 = new Paint();
        p1.setARGB(50, 0, 0, 255);
        xyPlotForSummarySteps.getGraph().setRangeGridLinePaint(p1);

        selectionWidget = new TextLabelWidget(xyPlotForSummarySteps.getLayoutManager(),
                new Size(
                        PixelUtils.dpToPix(200), SizeMode.ABSOLUTE,
                        0, SizeMode.RELATIVE),
                TextOrientation.HORIZONTAL);
        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(15));
        selectionWidget.getLabelPaint().setColor(Color.rgb(0, 29, 53));
        Paint p = new Paint();
        p.setARGB(100, 255, 102, 102);
        selectionWidget.setBackgroundPaint(p);
        selectionWidget.pack();

        View.OnTouchListener listener = (view, motionEvent) -> {
            view.performClick();
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                onPlotClicked(new PointF(motionEvent.getX(),
                                        motionEvent.getY()),
                                xyPlotForSummarySteps,
                                selectionWidget);

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

    private static void onPlotClicked(PointF point, XYPlot plot, TextLabelWidget selectionWidget) {

        // make sure the point lies within the graph area.  we use grid_rect
        // because it accounts for margins and padding as well.
        if (plot.containsPoint(point.x, point.y)) {
/*            selectionWidget.setSize(new Size(
                    PixelUtils.dpToPix(100), SizeMode.ABSOLUTE,
                    0, SizeMode.RELATIVE));*/
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

            Paint p = new Paint();
            p.setARGB(0, 255, 255, 255);
            p.setFlags(Paint.ANTI_ALIAS_FLAG);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                p.setBlendMode(BlendMode.CLEAR);
            }
            selectionWidget.setBackgroundPaint(p);
            selectionWidget.setText("");

        } else {

            Paint p = new Paint();
            p.setARGB(100, 233, 239, 248);
            p.setFlags(Paint.ANTI_ALIAS_FLAG);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                p.setBlendMode(BlendMode.SRC);
            }
            selectionWidget.setBackgroundPaint(p);


            float X = plot.getWidth()/2.0f + plot.getLeft();
            float Y = plot.getBottom()-20.0f;
            Log.d(TAG, "onPlotClicked: " + plot.getGraph().getGridRect());
            selectionWidget.position(
                    X,
                    HorizontalPositioning.ABSOLUTE_FROM_LEFT,
                   Y,
                    VerticalPositioning.ABSOLUTE_FROM_TOP,
                    Anchor.CENTER);
            int position = selection.second.getX(selection.first).intValue();

            String dataS;
            if (position != (IntervalUtils.intervalLabels30Min.length - 1)) {
                dataS = IntervalUtils.intervalLabels30Min[position] + " - " + IntervalUtils.intervalLabels30Min[position + 1];
            } else {
                dataS = IntervalUtils.intervalLabels30Min[position] + " - " + "00:00";
            }

            selectionWidget.setText(selection.second.getY(selection.first).intValue() + " steps at " + "\n" + dataS);


        }
        plot.redraw();
    }

    public static void processingIntervalsSummarySteps(XYPlot plot, Double[] seriesSteps) {


        String[] domainLabels = IntervalUtils.intervalLabels30Min;
        double rangeStepsUpperLimit = Collections.max(Arrays.asList(seriesSteps));
        int updatedRangeStepsUpperLimit = setRangeDomain((int) rangeStepsUpperLimit, xyPlotForSummarySteps);
        plot.setRangeUpperBoundary(updatedRangeStepsUpperLimit, BoundaryMode.FIXED);
        PlotUtils.setRangeMargins(updatedRangeStepsUpperLimit, xyPlotForSummarySteps);


        int intervalsTotal = (int) Math.round(24.0 / Utils.INTERVAL_WIDTH_HALF);
        Stream.iterate(0, i -> ++i).limit(intervalsTotal - 1)
                .forEach(data -> {
                    seriesBarRepresentation.setY(seriesSteps[data], data);
                    seriesPlotRepresentation.setY(seriesSteps[data] * 1.3, data);
                });

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

        xyPlotForSummarySteps.redraw();
    }

    public static void plotStepsDoubleIntervalsData(String[] domainLabels,
                                                    Double[] seriesSteps,
                                                    @NonNull XYPlot plot) {
        plot.clear();
        double rangeStepsUpperLimit = Collections.max(Arrays.asList(seriesSteps));
        int updatedRangeStepsUpperLimit = setRangeDomain((int) rangeStepsUpperLimit, plot);
        plot.setRangeUpperBoundary(updatedRangeStepsUpperLimit, BoundaryMode.FIXED);
        PlotUtils.setRangeMargins(updatedRangeStepsUpperLimit, plot);

        XYSeries series1 = new SimpleXYSeries(Arrays.asList(seriesSteps), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        PlotUtils.MyBarFormatter formatter1 = new PlotUtils.MyBarFormatter(Color.rgb(115, 8, 250), Color.rgb(115, 8, 250));
        plot.addSeries(series1, formatter1);

        PlotUtils.MyBarRenderer renderer = plot.getRenderer(PlotUtils.MyBarRenderer.class);
        renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_WIDTH, PixelUtils.dpToPix(8));
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



        plot.redraw();
    }

    public static int setRangeDomain(int rangeStepsUpperLimit, XYPlot plot) {
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
        return rangeStepsUpperLimit;

    }
}
