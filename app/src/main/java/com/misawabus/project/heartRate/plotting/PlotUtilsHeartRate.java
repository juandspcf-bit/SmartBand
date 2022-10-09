package com.misawabus.project.heartRate.plotting;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.androidplot.Plot;
import com.androidplot.PlotListener;
import com.androidplot.ui.Anchor;
import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.TextOrientation;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.misawabus.project.heartRate.fragments.summaryFragments.SummaryFragment;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class PlotUtilsHeartRate {



    public static void plotHeartRateDoubleIntervalsData(String[] domainLabels,
                                                        Double[] rangeDouble,
                                                        XYPlot plot) {
        plot.clear();

        XYSeries series1 = new SimpleXYSeries(Arrays.asList(rangeDouble), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        LineAndPointFormatter formatterForSeriesPlotRepresentation =
                new LineAndPointFormatter(Color.rgb(255, 100, 100),
                        Color.rgb(255, 100, 100),
                        Color.TRANSPARENT,
                        null);
        formatterForSeriesPlotRepresentation.getLinePaint().setStrokeWidth(3f);
        formatterForSeriesPlotRepresentation.getVertexPaint().setStrokeWidth(3f);



        formatterForSeriesPlotRepresentation.setInterpolationParams(
                new CatmullRomInterpolator.Params(2, CatmullRomInterpolator.Type.Centripetal));
        plot.addSeries(series1, formatterForSeriesPlotRepresentation);

        double rangeUpperLimit = Collections.max(Arrays.asList(rangeDouble));

        rangeUpperLimit = rangeUpperLimit * 1.1;
        PlotUtils.setRangeMargins((int) rangeUpperLimit, plot);
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

    public static void plotTemperatureDoubleIntervalsData(String[] domainLabels,
                                                          Double[] rangeDouble,
                                                          @NonNull XYPlot plot) {
        plot.clear();

        XYSeries series1 = new SimpleXYSeries(Arrays.asList(rangeDouble), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        LineAndPointFormatter formatterForSeriesPlotRepresentation =
                new LineAndPointFormatter(Color.rgb(255, 100, 100),
                        Color.rgb(255, 100, 100),
                        Color.TRANSPARENT,
                        null);
        formatterForSeriesPlotRepresentation.getLinePaint().setStrokeWidth(3f);
        formatterForSeriesPlotRepresentation.getVertexPaint().setStrokeWidth(3f);



        formatterForSeriesPlotRepresentation.setInterpolationParams(
                new CatmullRomInterpolator.Params(2, CatmullRomInterpolator.Type.Centripetal));
        plot.addSeries(series1, formatterForSeriesPlotRepresentation);

        double rangeUpperLimit = Collections.max(Arrays.asList(rangeDouble));

        rangeUpperLimit = rangeUpperLimit * 1.1;
        PlotUtils.setRangeMargins((int) rangeUpperLimit, plot);
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
}
