package com.misawabus.project.heartRate.plotting;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

public class PlotUtilsSpo2 {



    public static void plotSpo2DoubleIntervalsData(String[] domainLabels,
                                                   Double[] rangeDouble,
                                                   XYPlot plot) {
        plot.clear();

        XYSeries series1 = new SimpleXYSeries(Arrays.asList(rangeDouble), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
        //LineAndPointFormatter series1Format = new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels_hr_summary);

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
