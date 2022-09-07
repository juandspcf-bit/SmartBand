package com.misawabus.project.heartRate.fragments.fragmentUtils;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.androidplot.xy.XYPlot;
import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.device.entities.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.plotting.PlotUtils;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SetDataInViews {

    private static final String TAG = SetDataInViews.class.getSimpleName();


    public static void setSportsValues(Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap, FragmentDataSummaryV2Binding binding, Context context) {
        DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer
                = stringDataFiveMinAVGAllIntervalsMap.get(SportsData5MinAvgDataContainer
                .class.getSimpleName());
        Map<Integer, Map<String, Double>> sportsData = sportsDataFiveMinAvgDataContainer.getDoubleMap();
        List<Map<String, Double>> sportsDataMap = FragmentUtil.parse5MinFieldData(sportsData.toString());
        if(sportsDataMap.get(0).isEmpty()) return;
        Map<String, List<Double>> mapFieldsWith30MinValues = FragmentUtil.getSportsMapFieldsWith30MinCountValues(sportsDataMap);


        long stepsCount = (long) mapFieldsWith30MinValues.get("stepValue").stream().mapToDouble(Double::doubleValue).sum();
        double caloriesCount =  mapFieldsWith30MinValues.get("calValue").stream().mapToDouble(Double::doubleValue).sum();
        double distancesCount = mapFieldsWith30MinValues.get("disValue").stream().mapToDouble(Double::doubleValue).sum();

        String stepsS = String.valueOf(stepsCount);
        String caloriesS = String.format(Locale.getDefault(), "%.1f Kcal", caloriesCount);
        String distance = String.format(Locale.getDefault(), "%.1f Km", distancesCount);

        binding.fragmentCounterSteps.setText(stepsS);
        binding.textViewCaloriesCounter.setText(caloriesS);
        binding.textViewDistanceCounter.setText(distance);
    }

    public static void setSleepValues(List<Integer> collect0,
                                      List<Integer> collect1,
                                      List<Integer> collect2,
                                      @NonNull XYPlot plot,
                                      SleepDataUI dataMap,
                                      FragmentDataSummaryV2Binding binding) {
        binding.fragmentSleepPlot.setVisibility(View.VISIBLE);
        binding.flowNoSleepData.setVisibility(View.GONE);
        PlotUtils plotUtils = PlotUtils.getInstance();

        plotUtils.processingStringIntervalsSleep(collect0, collect1, collect2, Objects.requireNonNull(plot), dataMap);

    }

    public static void plotSop2Data( XYDataArraysForPlotting xyDataArraysForPlotting, XYPlot plot, Context context) {
        PlotUtils plotUtils = PlotUtils.getInstance();
        plotUtils.plotHeartRateDoubleIntervalsData(xyDataArraysForPlotting.getPeriodIntervalsArray(),
                xyDataArraysForPlotting.getSeriesDoubleAVR(),
                plot,
                context
        );
    }

    public static void plotHeartRateData( XYDataArraysForPlotting xyDataArraysForPlotting, XYPlot plot, Context context) {
        PlotUtils plotUtils = PlotUtils.getInstance();
        plotUtils.plotHeartRateDoubleIntervalsData(xyDataArraysForPlotting.getPeriodIntervalsArray(),
                xyDataArraysForPlotting.getSeriesDoubleAVR(),
                plot,
                context
        );

    }

    public static void plotStepsData( XYDataArraysForPlotting xyDataArraysForPlotting, XYPlot plot) {
        PlotUtils plotUtils = PlotUtils.getInstance();
        plotUtils.plotStepsDoubleIntervalsData(xyDataArraysForPlotting.getPeriodIntervalsArray(),
                xyDataArraysForPlotting.getSeriesDoubleAVR(),
                plot
        );
    }

    public static void plotBloodPressureData( XYDataArraysForPlotting hpXYDataArraysForPlotting, XYDataArraysForPlotting lpXYDataArraysForPlotting, XYPlot plot, Context contex) {
        PlotUtils plotUtils = PlotUtils.getInstance();
        plotUtils.processingDoublesIntervalsBP(hpXYDataArraysForPlotting.getPeriodIntervalsArray(),
                hpXYDataArraysForPlotting.getSeriesDoubleAVR(),
                lpXYDataArraysForPlotting.getSeriesDoubleAVR(),
                plot,
                contex
        );
    }

}
