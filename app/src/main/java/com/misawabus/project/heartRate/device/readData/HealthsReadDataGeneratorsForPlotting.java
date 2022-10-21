package com.misawabus.project.heartRate.device.readData;

import static com.misawabus.project.heartRate.plotting.PlotUtils.getSubArrayWithReplacedZeroValuesAsAvg;
import static com.misawabus.project.heartRate.plotting.PlotUtils.getSubArrayWithReplacedZeroValuesAsAvgHeartRateV2;
import static com.misawabus.project.heartRate.plotting.PlotUtils.getSubArrayWithReplacedZeroValuesAsAvgV2;

import androidx.annotation.NonNull;

import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.device.DataContainers.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.plotting.PlotUtils;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HealthsReadDataGeneratorsForPlotting {
    @NonNull
    public static Map<String, XYDataArraysForPlotting> getStringXYDataArraysForPlottingMap(XYDataArraysForPlotting sportsArraysForPlotting,
                                                                                           XYDataArraysForPlotting heartRateArraysForPlotting,
                                                                                           XYDataArraysForPlotting highBPArraysForPlotting,
                                                                                           XYDataArraysForPlotting lowBPArraysForPlotting,
                                                                                           XYDataArraysForPlotting spo2PArraysForPlotting) {
        Map<String, XYDataArraysForPlotting> arraysMap = new HashMap<>();
        arraysMap
                .put(SportsData5MinAvgDataContainer.class.getSimpleName(),
                        sportsArraysForPlotting);
        arraysMap
                .put(HeartRateData5MinAvgDataContainer.class.getSimpleName(),
                        heartRateArraysForPlotting);
        arraysMap
                .put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName() + "High",
                        highBPArraysForPlotting);
        arraysMap
                .put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName() + "Low",
                        lowBPArraysForPlotting);
        arraysMap
                .put(Sop2HData5MinAvgDataContainer.class.getSimpleName(),
                        spo2PArraysForPlotting);
        return arraysMap;
    }

    @NonNull
    public static Map<String, XYDataArraysForPlotting> getMapContainerXYOriginDataArraysForPlotting(XYDataArraysForPlotting sportsArraysForPlotting,
                                                                                                    XYDataArraysForPlotting heartRateArraysForPlotting,
                                                                                                    XYDataArraysForPlotting highBPArraysForPlotting,
                                                                                                    XYDataArraysForPlotting lowBPArraysForPlotting) {
        Map<String, XYDataArraysForPlotting> arraysMap = new HashMap<>();
        arraysMap
                .put(SportsData5MinAvgDataContainer.class.getSimpleName(),
                        sportsArraysForPlotting);
        arraysMap
                .put(HeartRateData5MinAvgDataContainer.class.getSimpleName(),
                        heartRateArraysForPlotting);
        arraysMap
                .put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName() + "High",
                        highBPArraysForPlotting);
        arraysMap
                .put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName() + "Low",
                        lowBPArraysForPlotting);

        return arraysMap;
    }

    @NonNull
    @Contract("null -> new")
    public static XYDataArraysForPlotting getSportsArraysForPlotting(DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer) {
        if (sportsDataFiveMinAvgDataContainer == null) return new XYDataArraysForPlotting();
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        dataIntervalsMap = sportsDataFiveMinAvgDataContainer.getDoubleMap();
        if (dataIntervalsMap == null || dataIntervalsMap.size() == 0)
            return new XYDataArraysForPlotting();

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        Map<String, List<Double>> dataGroupByFieldsWith30MinSumValues;
        dataGroupByFieldsWith30MinSumValues = FragmentUtil
                .getSportsMapFieldsWith30MinCountValues(dataIntervalsList);
        List<Double> stepValueList = dataGroupByFieldsWith30MinSumValues.get("stepValue");
        if (stepValueList == null) return new XYDataArraysForPlotting();
        Double[] seriesSteps = stepValueList.toArray(new Double[0]);
        String[] domainLabels = IntervalUtils.intervalLabels30Min;

        XYDataArraysForPlotting xyDataArraysForPlotting;
        xyDataArraysForPlotting = new XYDataArraysForPlotting(domainLabels,
                seriesSteps);
        return xyDataArraysForPlotting;
    }

    @NonNull
    @Contract("null -> new")
    public static XYDataArraysForPlotting getHearRateArraysForPlotting(DataFiveMinAvgDataContainer dataIntervalsMapContainer) {
        if (dataIntervalsMapContainer == null) return new XYDataArraysForPlotting();
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        if (dataIntervalsMap == null || dataIntervalsMap.size() == 0 || dataIntervalsMap.size() < 3)
            return new XYDataArraysForPlotting();

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        XYDataArraysForPlotting xyDataArraysForPlotting;
        PlotUtils.AxisClass ppgs = getSubArrayWithReplacedZeroValuesAsAvgHeartRateV2(dataIntervalsList, "Ppgs");
        xyDataArraysForPlotting = new XYDataArraysForPlotting(ppgs.getTimeAxis(),
                ppgs.getRangeAxis());
        if (ppgs.getRangeAxis().length < 3) {
            return new XYDataArraysForPlotting();
        }
        return xyDataArraysForPlotting;
    }

    @NonNull
    @Contract("null -> new")
    public static XYDataArraysForPlotting getHighBPArraysForPlotting(DataFiveMinAvgDataContainer dataIntervalsMapContainer) {
        if (dataIntervalsMapContainer == null) return new XYDataArraysForPlotting();
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        if (dataIntervalsMap == null || dataIntervalsMap.size() == 0 || dataIntervalsMap.size() < 3)
            return new XYDataArraysForPlotting();

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);
        List<Map<String, Double>> bloodPressureMapFieldsWith30Min;
        bloodPressureMapFieldsWith30Min = FragmentUtil.getBloodPressureMapFieldWith30MinAVGValues(dataIntervalsList, "highValue");

        XYDataArraysForPlotting xyDataArraysForPlotting;
        PlotUtils.AxisClass highValue = getSubArrayWithReplacedZeroValuesAsAvgV2(bloodPressureMapFieldsWith30Min, "highValue", IntervalUtils.intervalLabels30Min);
        xyDataArraysForPlotting = new XYDataArraysForPlotting(highValue.getTimeAxis(),
                highValue.getRangeAxis());
        if (highValue.getRangeAxis().length < 3) {
            return new XYDataArraysForPlotting();
        }
        return xyDataArraysForPlotting;
    }

    @NonNull
    @Contract("null -> new")
    public static XYDataArraysForPlotting getLowBPArraysForPlotting(DataFiveMinAvgDataContainer dataIntervalsMapContainer) {
        if (dataIntervalsMapContainer == null) return new XYDataArraysForPlotting();
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        if (dataIntervalsMap == null || dataIntervalsMap.size() == 0 || dataIntervalsMap.size() < 3)
            return new XYDataArraysForPlotting();

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);
        List<Map<String, Double>> bloodPressureMapFieldsWith30Min;
        bloodPressureMapFieldsWith30Min = FragmentUtil.getBloodPressureMapFieldWith30MinAVGValues(dataIntervalsList, "lowVaamlue");


        XYDataArraysForPlotting xyDataArraysForPlotting;
        PlotUtils.AxisClass lowVaamlue = getSubArrayWithReplacedZeroValuesAsAvgV2(bloodPressureMapFieldsWith30Min, "lowVaamlue", IntervalUtils.intervalLabels30Min);
        xyDataArraysForPlotting = new XYDataArraysForPlotting(lowVaamlue.getTimeAxis(),
                lowVaamlue.getRangeAxis());
        if (lowVaamlue.getRangeAxis().length < 3) {
            return new XYDataArraysForPlotting();
        }
        return xyDataArraysForPlotting;
    }

    @NonNull
    @Contract("null -> new")
    public static XYDataArraysForPlotting getSpo2PArraysForPlotting(DataFiveMinAvgDataContainer dataIntervalsMapContainer) {
        if (dataIntervalsMapContainer == null) return new XYDataArraysForPlotting();
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        if (dataIntervalsMap.size() == 0 || dataIntervalsMap.size() < 3) {
            return new XYDataArraysForPlotting();
        }

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        XYDataArraysForPlotting xyDataArraysForPlotting;
        PlotUtils.AxisClass oxygenValue = getSubArrayWithReplacedZeroValuesAsAvgV2(dataIntervalsList, "oxygenValue", IntervalUtils.intervalLabels5Min);
        xyDataArraysForPlotting = new XYDataArraysForPlotting(oxygenValue.getTimeAxis(),
                oxygenValue.getRangeAxis());
        if (oxygenValue.getRangeAxis().length < 3) {
            return new XYDataArraysForPlotting();
        }
        return xyDataArraysForPlotting;
    }

    public static XYDataArraysForPlotting getBodyTemperatureArraysForPlotting(DataFiveMinAvgDataContainer dataIntervalsMapContainer) {
        if (dataIntervalsMapContainer == null) return new XYDataArraysForPlotting();
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        if (dataIntervalsMap == null || dataIntervalsMap.size() == 0 || dataIntervalsMap.size() < 3)
            return new XYDataArraysForPlotting();

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        XYDataArraysForPlotting xyDataArraysForPlotting;
        PlotUtils.AxisClass bodyTemperature = getSubArrayWithReplacedZeroValuesAsAvgV2(dataIntervalsList, "bodyTemperature", IntervalUtils.intervalLabels5Min);
        xyDataArraysForPlotting = new XYDataArraysForPlotting(bodyTemperature.getTimeAxis(),
                bodyTemperature.getRangeAxis());
        if (bodyTemperature.getRangeAxis().length < 3) {
            return new XYDataArraysForPlotting();
        }
        return xyDataArraysForPlotting;
    }

}
