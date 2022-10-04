package com.misawabus.project.heartRate.plotting;

import static com.misawabus.project.heartRate.fragments.summaryFragments.utils.UtilsSummaryFrag.interpolateSeries;
import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.toList;

import androidx.annotation.NonNull;

import com.androidplot.ui.SeriesRenderer;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.misawabus.project.heartRate.Intervals.IntervalUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class PlotUtils {

    private static final String TAG = PlotUtils.class.getSimpleName();
    private static MyBarFormatter selectionFormatter;

    public PlotUtils() {

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
                .map(doubleMap -> doubleMap.getOrDefault(field, 0.0))
                .toArray(Double[]::new);
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
