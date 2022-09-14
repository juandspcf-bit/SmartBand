package com.misawabus.project.heartRate.fragments.daysFragments;

import static com.misawabus.project.heartRate.plotting.PlotUtils.getSubArrayWithReplacedZeroValuesAsAvg;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.device.DataContainers.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.fragmentUtils.SetDataInViews;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;

import java.util.List;
import java.util.Map;

public class DaysFragmentUtil {

    private static final String TAG = DaysFragmentUtil.class.getSimpleName();

    static void plotSports(Map<String, DataFiveMinAvgDataContainer> fullDataFiveMinAVGAllIntervalsMap, FragmentDataSummaryV2Binding binding) {
        DataFiveMinAvgDataContainer dataIntervalsMapContainer
                = fullDataFiveMinAVGAllIntervalsMap.get(SportsData5MinAvgDataContainer
                        .class.getSimpleName());
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        if(dataIntervalsMapContainer==null)return;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        Log.d(TAG, "plotSports: " + dataIntervalsMap);
        Map<String, Double> singleFieldMap = dataIntervalsMap.get(1);
        if(singleFieldMap == null || singleFieldMap.isEmpty()) return;

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        Map<String, List<Double>> dataGroupByFieldsWith30MinSumValues;
        dataGroupByFieldsWith30MinSumValues = FragmentUtil
                .getSportsMapFieldsWith30MinCountValues(dataIntervalsList);
        List<Double> stepValueList = dataGroupByFieldsWith30MinSumValues.get("stepValue");
        if (stepValueList == null) return;
        Double[] seriesSteps = stepValueList.toArray(new Double[0]);
        String[] domainLabels = IntervalUtils.hoursInterval;

        XYDataArraysForPlotting xyDataArraysForPlotting;
        xyDataArraysForPlotting = new XYDataArraysForPlotting(domainLabels,
                seriesSteps);

        binding.fragmentPlot.setVisibility(View.VISIBLE);
        binding.flowNoStepsData.setVisibility(View.GONE);
        SetDataInViews.plotStepsData(xyDataArraysForPlotting,
                binding.fragmentPlot);

    }

    public static void plotHeartRate(Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap, FragmentDataSummaryV2Binding binding, Context context) {
        DataFiveMinAvgDataContainer dataIntervalsMapContainer
                = stringDataFiveMinAVGAllIntervalsMap.get(HeartRateData5MinAvgDataContainer
                .class.getSimpleName());
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        if(dataIntervalsMapContainer==null)return;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        Map<String, Double> singleFieldMap = dataIntervalsMap.get(1);
        if (singleFieldMap == null || singleFieldMap.isEmpty() || dataIntervalsMap.size()<3) return;

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        Double[] subArrayWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(dataIntervalsList, "Ppgs");
        int lengthSubArray = subArrayWithReplacedZeroValuesAsAvg.length;
        String[] timeAxisSubArray =IntervalUtils.getStringFiveMinutesIntervals(lengthSubArray);

        XYDataArraysForPlotting hrXYDataArraysForPlotting;
        hrXYDataArraysForPlotting = new XYDataArraysForPlotting(timeAxisSubArray,
                subArrayWithReplacedZeroValuesAsAvg);

        binding.fragmentRatePlot.setVisibility(View.VISIBLE);
        binding.flowNoHeartRateData.setVisibility(View.GONE);
        SetDataInViews.plotHeartRateData(hrXYDataArraysForPlotting,
                binding.fragmentRatePlot,
                context);
    }

    public static void plotBloodPressure(Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap, FragmentDataSummaryV2Binding binding, Context context) {
        DataFiveMinAvgDataContainer dataIntervalsMapContainer
                = stringDataFiveMinAVGAllIntervalsMap.get(BloodPressureDataFiveMinAvgDataContainer
                .class.getSimpleName());
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        if(dataIntervalsMapContainer==null)return;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        Map<String, Double> singleFieldMap = dataIntervalsMap.get(1);
        if (singleFieldMap == null || singleFieldMap.isEmpty() || dataIntervalsMap.size()<3) return;

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        List<Map<String, Double>> bloodPressureMapFieldsWith30Min;
        bloodPressureMapFieldsWith30Min = FragmentUtil.getBloodPressureMapFieldsWith30MinAVGValues(dataIntervalsList);

        Double[] subArrayHPWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(bloodPressureMapFieldsWith30Min, "highValue");
        String[] timeAxisSubArrayHP =IntervalUtils.hoursInterval;
        Double[] subArrayLPWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(bloodPressureMapFieldsWith30Min, "lowVaamlue");
        String[] timeAxisSubArrayLP =IntervalUtils.hoursInterval;

        XYDataArraysForPlotting highValueArrays;
        highValueArrays = new XYDataArraysForPlotting(timeAxisSubArrayHP,
                subArrayHPWithReplacedZeroValuesAsAvg);
        XYDataArraysForPlotting lowVaamlueArrays;
        lowVaamlueArrays = new XYDataArraysForPlotting(timeAxisSubArrayLP,
                subArrayLPWithReplacedZeroValuesAsAvg);

        binding.fragmentBloodPressurePlot.setVisibility(View.VISIBLE);
        binding.flowNoBPData.setVisibility(View.GONE);
        SetDataInViews.plotBloodPressureData(highValueArrays,
                lowVaamlueArrays,
                binding.fragmentBloodPressurePlot,
                context);
    }

    public static void plotSpO2(Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap, FragmentDataSummaryV2Binding binding, Context context) {
        DataFiveMinAvgDataContainer dataIntervalsMapContainer
                = stringDataFiveMinAVGAllIntervalsMap.get(Sop2HData5MinAvgDataContainer
                .class.getSimpleName());
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        if(dataIntervalsMapContainer==null)return;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        Map<String, Double> singleFieldMap = dataIntervalsMap.get(1);
        if (singleFieldMap == null || singleFieldMap.isEmpty() || dataIntervalsMap.size()<3) return;

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        Double[] subArrayWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(dataIntervalsList, "oxygenValue");
        int lengthSubArray = subArrayWithReplacedZeroValuesAsAvg.length;
        String[] timeAxisSubArray =IntervalUtils.getStringFiveMinutesIntervals(lengthSubArray);

        XYDataArraysForPlotting sop2XYDataArraysForPlotting;
        sop2XYDataArraysForPlotting = new XYDataArraysForPlotting(timeAxisSubArray,
                subArrayWithReplacedZeroValuesAsAvg);

        if (binding.fragmentSop2Plot != null && binding.flowNoSop2Data != null) {
            binding.fragmentSop2Plot.setVisibility(View.VISIBLE);
            binding.flowNoSop2Data.setVisibility(View.GONE);
        }
        SetDataInViews.plotSop2Data(sop2XYDataArraysForPlotting,
                binding.fragmentSop2Plot,
                context);
    }
}
