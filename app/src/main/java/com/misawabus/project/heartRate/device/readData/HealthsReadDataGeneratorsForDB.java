package com.misawabus.project.heartRate.device.readData;

import static java.util.Arrays.stream;

import android.util.Log;

import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.summaryFragments.utils.UtilsSummaryFrag;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.TemptureData;
import com.veepoo.protocol.model.datas.TimeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.BiConsumer;

public class HealthsReadDataGeneratorsForDB {
    public static DataFiveMinAvgDataContainer computeSportsDataFiveMinAVR(List<OriginData3> originData3List,
                                                                          List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList,
                                                                          DataFiveMinAvgDataContainer fieldDataFiveMinAVGAllIntervals) {
        computeDataFiveMinAVGOrigin3(originData3List, biConsumerList, fieldDataFiveMinAVGAllIntervals);

        return fieldDataFiveMinAVGAllIntervals;

    }

    public static DataFiveMinAvgDataContainer computeSportsDataFiveMinOrigin(List<OriginData> originDataList,
                                                                             List<BiConsumer<Map<String, Double>, OriginData>> biConsumerList,
                                                                             DataFiveMinAvgDataContainer fieldDataFiveMinAllIntervals) {
        computeDataFiveMinOrigin(originDataList,
                biConsumerList,
                fieldDataFiveMinAllIntervals);

        return fieldDataFiveMinAllIntervals;

    }

    public static DataFiveMinAvgDataContainer computeHeartRateDataFiveMinOrigin(List<OriginData> originDataList,
                                                                                List<BiConsumer<Map<String, Double>, OriginData>> biConsumerList,
                                                                                DataFiveMinAvgDataContainer fieldDataFiveMinAllIntervals) {
        computeDataFiveMinOrigin(originDataList, biConsumerList, fieldDataFiveMinAllIntervals);

        return fieldDataFiveMinAllIntervals;

    }

    public static DataFiveMinAvgDataContainer computeBloodPressureDataFiveMinOrigin(List<OriginData> originDataList,
                                                                                    List<BiConsumer<Map<String, Double>, OriginData>> biConsumerList,
                                                                                    DataFiveMinAvgDataContainer fieldDataFiveMinAllIntervals) {
        computeDataFiveMinOrigin(originDataList, biConsumerList, fieldDataFiveMinAllIntervals);

        return fieldDataFiveMinAllIntervals;

    }

    public static DataFiveMinAvgDataContainer computeHearRateDataFiveMinAVR(List<OriginData3> originData3List,
                                                                            List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList,
                                                                            DataFiveMinAvgDataContainer fieldDataFiveMinAVGAllIntervals) {

        computeDataFiveMinAVGOrigin3(originData3List, biConsumerList, fieldDataFiveMinAVGAllIntervals);
        return fieldDataFiveMinAVGAllIntervals;

    }

    public static DataFiveMinAvgDataContainer computeBloodPressureDataFiveMinAVR(List<OriginData3> originData3List,
                                                                                 List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList, DataFiveMinAvgDataContainer fieldDataFiveMinAVGAllIntervals) {
        computeDataFiveMinAVGOrigin3(originData3List, biConsumerList, fieldDataFiveMinAVGAllIntervals);
        return fieldDataFiveMinAVGAllIntervals;
    }

    public static DataFiveMinAvgDataContainer computeSop2hDataFiveMinAVR(List<OriginData3> originData3List,
                                                                         List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList, DataFiveMinAvgDataContainer fieldDataFiveMinAVGAllIntervals) {

        computeDataFiveMinAVGOrigin3(originData3List, biConsumerList, fieldDataFiveMinAVGAllIntervals);
        return fieldDataFiveMinAVGAllIntervals;
    }

    public static void computeDataFiveMinAVGOrigin3(List<OriginData3> originData3List, List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList, DataFiveMinAvgDataContainer fieldDataFiveMinAVGAllIntervals) {
        fieldDataFiveMinAVGAllIntervals.setStringDate(originData3List.get(0).getDate());
        originData3List
                .forEach(data -> {
                    TimeData timeData = data.getmTime();
                    int interval = IntervalUtils.getInterval5Min(timeData.getHour(),
                            timeData.getMinute());
                    Map<String, Double> mapValues = new HashMap<>();
                    biConsumerList.forEach(bi -> bi.accept(mapValues, data));
                    fieldDataFiveMinAVGAllIntervals.getDoubleMap().put(interval, mapValues);
                });
    }

    public static void computeDataFiveMinOrigin(List<OriginData> originDataList, List<BiConsumer<Map<String, Double>, OriginData>> biConsumerList, DataFiveMinAvgDataContainer fieldDataFiveMinAllIntervals) {
        originDataList
                .forEach(data -> {
                    TimeData timeData = data.getmTime();
                    if (timeData == null) return;
                    int interval = IntervalUtils.getInterval5Min(timeData.getHour(),
                            timeData.getMinute());
                    Map<String, Double> mapValues = new HashMap<>();
                    biConsumerList.forEach(bi -> bi.accept(mapValues, data));
                    fieldDataFiveMinAllIntervals.getDoubleMap().put(interval, mapValues);
                });
    }

    public static OptionalDouble get5MinAVG(int[] results) {
        return stream(results)
                .filter(value -> value > 0)
                .average();
    }

    public static OptionalDouble get5MinAVGSpo2(int[] results) {
        return stream(results)
                .filter(value -> (value > 0 && value < 50))
                .average();
    }

    public static List<BiConsumer<Map<String, Double>, OriginData3>> functionToSetFieldsInSop2() {

        List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList = new ArrayList<>();

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("apneaResult",
                get5MinAVG(originData3.getApneaResults()).orElse(0)));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("oxygenValue",
                get5MinAVG(originData3.getOxygens()).orElse(0)));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("respirationRate",
                get5MinAVGSpo2(originData3.getResRates()).orElse(0)));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("isHypoxia",
                get5MinAVG(originData3.getHypoxiaTimes()).orElse(0)));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("cardiacLoad",
                get5MinAVG(originData3.getCardiacLoads()).orElse(0)));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("SleepActivity",
                get5MinAVG(originData3.getSleepStates()).orElse(0)));


        return biConsumerList;
    }

    public static List<BiConsumer<Map<String, Double>, OriginData3>> functionToSetFieldsInPpgs() {

        List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList = new ArrayList<>();

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("Ppgs",
                get5MinAVG(originData3.getPpgs()).orElse(0)));

        // TODO: 2022/09/02 add the activity zone to the excel file
        biConsumerList.add((doubleMap, originData3) -> {
            double v = get5MinAVG(originData3.getPpgs()).orElse(0);

            UtilsSummaryFrag.ZoneObject zoneObject = UtilsSummaryFrag.testZone(0, v);
            doubleMap.put("Activity",
                    (double) zoneObject.getZoneInteger());
        });
        return biConsumerList;
    }

    public static List<BiConsumer<Map<String, Double>, OriginData>> functionToSetFieldsInRateValueOrigin() {

        List<BiConsumer<Map<String, Double>, OriginData>> biConsumerList = new ArrayList<>();

        biConsumerList.add((doubleMap, originData) -> doubleMap.put("Ppgs",
                (double) originData.getRateValue()));

        biConsumerList.add((doubleMap, originData) -> {
            double v = originData.getRateValue();

            UtilsSummaryFrag.ZoneObject zoneObject = UtilsSummaryFrag.testZone(0, v);
            doubleMap.put("Activity",
                    (double) zoneObject.getZoneInteger());
        });
        return biConsumerList;
    }

    public static List<BiConsumer<Map<String, Double>, OriginData3>> functionToSetFieldsInSportsOrigin3() {

        List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList = new ArrayList<>();

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("stepValue",
                (double) originData3.getStepValue()));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("calValue",
                originData3.getCalValue()));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("disValue",
                originData3.getDisValue()));

        return biConsumerList;
    }

    public static List<BiConsumer<Map<String, Double>, TemptureData>> functionToSetFieldsInTemperatureData() {

        List<BiConsumer<Map<String, Double>, TemptureData>> biConsumerList = new ArrayList<>();

        biConsumerList.add((doubleMap, temperatureData) -> doubleMap.put("skinTemperature",
                (double) temperatureData.getBaseTempture()));

        biConsumerList.add((doubleMap, temperatureData) -> doubleMap.put("bodyTemperature",
                (double) temperatureData.getTempture()));

        return biConsumerList;
    }

    public static List<BiConsumer<Map<String, Double>, OriginData>> functionToSetFieldsInSportsOrigin() {

        List<BiConsumer<Map<String, Double>, OriginData>> biConsumerList = new ArrayList<>();

        biConsumerList.add((doubleMap, originData) -> doubleMap.put("stepValue",
                (double) originData.getStepValue()));

        biConsumerList.add((doubleMap, originData) -> doubleMap.put("calValue",
                originData.getCalValue()));

        biConsumerList.add((doubleMap, originData) -> doubleMap.put("disValue",
                originData.getDisValue()));

        return biConsumerList;
    }

    public static List<BiConsumer<Map<String, Double>, OriginData3>> functionToSetFieldsInBloodPressure() {

        List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList = new ArrayList<>();

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("highValue",
                (double) originData3.getHighValue()));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("lowVaamlue",
                (double) originData3.getLowValue()));
        return biConsumerList;
    }

    public static List<BiConsumer<Map<String, Double>, OriginData>> functionToSetFieldsInBloodPressureOrigin() {

        List<BiConsumer<Map<String, Double>, OriginData>> biConsumerList = new ArrayList<>();

        biConsumerList.add((doubleMap, originData) -> doubleMap.put("highValue",
                (double) originData.getHighValue()));

        biConsumerList.add((doubleMap, originData) -> doubleMap.put("lowVaamlue",
                (double) originData.getLowValue()));
        return biConsumerList;
    }
}
