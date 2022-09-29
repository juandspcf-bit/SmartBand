package com.misawabus.project.heartRate.plotting;

import android.graphics.Color;
import android.graphics.Paint;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CandlestickFormatter;
import com.androidplot.xy.CandlestickMaker;
import com.androidplot.xy.CandlestickSeries;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Collections;

public class PlotUtilsBloodPressure {


    public static void plotBloodPressureDoubleIntervalsData(String[] domainLabels,
                                                            Double[] integerHPSeries,
                                                            Double[] integerLPSeries,
                                                            XYPlot plot) {
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
        plot.setDomainStep(StepMode.SUBDIVIDE, Math.min(integerHPSeries.length, 20));
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
}
