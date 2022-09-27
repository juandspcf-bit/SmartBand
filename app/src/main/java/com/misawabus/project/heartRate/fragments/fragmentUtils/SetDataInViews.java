package com.misawabus.project.heartRate.fragments.fragmentUtils;

import android.view.View;

import androidx.annotation.NonNull;

import com.androidplot.xy.XYPlot;
import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.plotting.PlotUtilsBloodPressure;
import com.misawabus.project.heartRate.plotting.PlotUtilsHeartRate;
import com.misawabus.project.heartRate.plotting.PlotUtilsSleep;
import com.misawabus.project.heartRate.plotting.PlotUtilsSpo2;
import com.misawabus.project.heartRate.plotting.PlotUtilsSports;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SetDataInViews {

    private static final String TAG = SetDataInViews.class.getSimpleName();


    public static void setSportsDashBoardSection(FragmentDataSummaryV2Binding binding, Map<String, List<Double>> mapFieldsWith30MinValues) {
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



    public static void plotSop2Data(XYDataArraysForPlotting xyDataArraysForPlotting, XYPlot plot) {
        PlotUtilsSpo2.plotSpo2DoubleIntervalsData(xyDataArraysForPlotting.getPeriodIntervalsArray(),
                xyDataArraysForPlotting.getSeriesDoubleAVR(),
                plot
        );
    }

    public static void plotHeartRateData(XYDataArraysForPlotting xyDataArraysForPlotting, XYPlot plot) {
        PlotUtilsHeartRate.plotHeartRateDoubleIntervalsData(xyDataArraysForPlotting.getPeriodIntervalsArray(),
                xyDataArraysForPlotting.getSeriesDoubleAVR(),
                plot
        );

    }

    public static void plotStepsData( XYDataArraysForPlotting xyDataArraysForPlotting, XYPlot plot) {
        PlotUtilsSports.plotStepsDoubleIntervalsData(xyDataArraysForPlotting.getPeriodIntervalsArray(),
                xyDataArraysForPlotting.getSeriesDoubleAVR(),
                plot
        );
    }

    public static void plotBloodPressureData( XYDataArraysForPlotting hpXYDataArraysForPlotting, XYDataArraysForPlotting lpXYDataArraysForPlotting, XYPlot plot) {
        PlotUtilsBloodPressure.plotBloodPressureDoubleIntervalsData(hpXYDataArraysForPlotting.getPeriodIntervalsArray(),
                hpXYDataArraysForPlotting.getSeriesDoubleAVR(),
                lpXYDataArraysForPlotting.getSeriesDoubleAVR(),
                plot);
    }

    public static void setSleepValues(SleepDataUI sleepDataUI,
                                      List<Integer> lightSleep,
                                      List<Integer> deepSleep,
                                      List<Integer> wakeUp,
                                      @NonNull XYPlot plot,
                                      FragmentDataSummaryV2Binding binding) {
        binding.fragmentSleepPlot.setVisibility(View.VISIBLE);
        binding.flowNoSleepData.setVisibility(View.GONE);

        PlotUtilsSleep.plotSleepIntegerListData(sleepDataUI,
                lightSleep,
                deepSleep,
                wakeUp,
                plot
        );
    }

    public static void setSleepPrecisionValues(SleepDataUI sleepDataUI,
                                               List<Integer> deepSleep,
                                               List<Integer> lightSleep,
                                               List<Integer> rapidEyeMovement,
                                               List<Integer> insomnia,
                                               List<Integer> wakeUp,
                                               XYPlot fragmentSleepPlot,
                                               FragmentDataSummaryV2Binding binding) {
        binding.fragmentSleepPlot.setVisibility(View.VISIBLE);
        binding.flowNoSleepData.setVisibility(View.GONE);

        PlotUtilsSleep.plotSleepPrecisionIntegerListData(sleepDataUI,
                deepSleep,
                lightSleep,
                rapidEyeMovement,
                insomnia,
                wakeUp,
                fragmentSleepPlot);

    }
}
