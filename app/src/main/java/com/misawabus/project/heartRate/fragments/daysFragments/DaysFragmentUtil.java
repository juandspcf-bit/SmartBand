package com.misawabus.project.heartRate.fragments.daysFragments;

import static com.misawabus.project.heartRate.plotting.PlotUtils.getSubArrayWithReplacedZeroValuesAsAvg;

import android.content.Context;
import android.view.View;

import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.databinding.FragmentDataSummaryV2Binding;
import com.misawabus.project.heartRate.device.entities.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.fragmentUtils.SetDataInViews;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;

import java.util.List;
import java.util.Map;

public class DaysFragmentUtil {

    private static final String TAG = DaysFragmentUtil.class.getSimpleName();

    static void plotSports(Map<String, DataFiveMinAvgDataContainer> fullDataFiveMinAVGAllIntervalsMap, FragmentDataSummaryV2Binding binding) {

        DataFiveMinAvgDataContainer avgDataForFiveMinContainer;
        avgDataForFiveMinContainer = fullDataFiveMinAVGAllIntervalsMap
                .get(SportsData5MinAvgDataContainer
        .class.getSimpleName());
        Map<Integer, Map<String, Double>> avgDataForFiveMinForAllIntervals;
        if(avgDataForFiveMinContainer==null)return;
        avgDataForFiveMinForAllIntervals = avgDataForFiveMinContainer.getDoubleMap();
        Map<String, Double> map = avgDataForFiveMinForAllIntervals.get(1);
        if(map == null || map.isEmpty()) return;

        List<Map<String, Double>> heartRavgDataForFiveMinForAllIntervalsList;
        heartRavgDataForFiveMinForAllIntervalsList = FragmentUtil.mapToList(avgDataForFiveMinForAllIntervals);

        Map<String, List<Double>> dataGroupByFieldsWith30MinSumValues;
        dataGroupByFieldsWith30MinSumValues = FragmentUtil
                .getSportsMapFieldsWith30MinCountValues(heartRavgDataForFiveMinForAllIntervalsList);
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
        DataFiveMinAvgDataContainer heartRateData5MinAVGAllIntervals
                = stringDataFiveMinAVGAllIntervalsMap.get(HeartRateData5MinAvgDataContainer
                .class.getSimpleName());
        Map<Integer, Map<String, Double>> heartRateData;
        if(heartRateData5MinAVGAllIntervals==null)return;
        heartRateData = heartRateData5MinAVGAllIntervals.getDoubleMap();
        Map<String, Double> map = heartRateData.get(1);
        if (map == null || map.isEmpty()) return;

        List<Map<String, Double>> heartRateDataMap;
        heartRateDataMap = FragmentUtil.mapToList(heartRateData);

        Double[] subArrayWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(heartRateDataMap, "Ppgs");
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
        DataFiveMinAvgDataContainer bloodPressureDataFiveMinAvgDataContainer
                = stringDataFiveMinAVGAllIntervalsMap.get(BloodPressureDataFiveMinAvgDataContainer
                .class.getSimpleName());
        Map<Integer, Map<String, Double>> bloodPressure;
        if(bloodPressureDataFiveMinAvgDataContainer==null)return;
        bloodPressure = bloodPressureDataFiveMinAvgDataContainer.getDoubleMap();
        Map<String, Double> map = bloodPressure.get(1);
        if(map == null || map.isEmpty()) return;

        List<Map<String, Double>> bloodPressureMap;
        bloodPressureMap = FragmentUtil.mapToList(bloodPressure);

        List<Map<String, Double>> bloodPressureMapFieldsWith30Min;
        bloodPressureMapFieldsWith30Min = FragmentUtil.getBloodPressureMapFieldsWith30MinAVGValues(bloodPressureMap);

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
        DataFiveMinAvgDataContainer spo2hData5MinAvgAllIntervals
                = stringDataFiveMinAVGAllIntervalsMap.get(Sop2HData5MinAvgDataContainer
                .class.getSimpleName());
        Map<Integer, Map<String, Double>> spo2hData;
        if(spo2hData5MinAvgAllIntervals==null)return;
        spo2hData = spo2hData5MinAvgAllIntervals.getDoubleMap();
        Map<String, Double> map = spo2hData.get(1);
        if (map == null || map.isEmpty()) return;

        List<Map<String, Double>> mapsSop2;
        mapsSop2 = FragmentUtil.mapToList(spo2hData);

        Double[] subArrayWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(mapsSop2, "oxygenValue");
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
