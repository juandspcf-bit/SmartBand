package com.misawabus.project.heartRate.device.readData;

import static com.misawabus.project.heartRate.plotting.PlotUtils.getSubArrayWithReplacedZeroValuesAsAvg;
import static java.util.Arrays.stream;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.Utils.DBops;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.device.DataContainers.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.Temperature5MinDataContainer;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.summaryFragments.utils.UtilsSummaryFrag;
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.TemptureData;
import com.veepoo.protocol.model.datas.TimeData;

import org.jetbrains.annotations.Contract;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.BiConsumer;

public class HealthsReadDataUtils {

    public static final String TAG = HealthsReadDataUtils.class.getSimpleName();


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

    private static void computeDataFiveMinAVGOrigin3(List<OriginData3> originData3List, List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList, DataFiveMinAvgDataContainer fieldDataFiveMinAVGAllIntervals) {
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


    private static void computeDataFiveMinOrigin(List<OriginData> originDataList, List<BiConsumer<Map<String, Double>, OriginData>> biConsumerList, DataFiveMinAvgDataContainer fieldDataFiveMinAllIntervals) {
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

    private static OptionalDouble get5MinAVG(int[] results) {
        return stream(results)
                .filter(value -> value > 0)
                .average();
    }

    private static OptionalDouble get5MinAVGSpo2(int[] results) {
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


    static void processOriginData3List(List<OriginData3> originData3List,
                                       Handler mHandler,
                                       DashBoardViewModel dashBoardViewModel,
                                       AppCompatActivity activity,
                                       DeviceViewModel deviceViewModel) {
        HealthsData.databaseWriteExecutor.execute(() -> {
            DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer = computeSportsDataFiveMinAVR(originData3List,
                    functionToSetFieldsInSportsOrigin3(),
                    new SportsData5MinAvgDataContainer());
            DataFiveMinAvgDataContainer heartRateDataFiveMinAvgDataContainer = computeHearRateDataFiveMinAVR(originData3List,
                    functionToSetFieldsInPpgs(),
                    new HeartRateData5MinAvgDataContainer());
            DataFiveMinAvgDataContainer bloodPressureDataFiveMinAvgDataContainer = computeBloodPressureDataFiveMinAVR(originData3List,
                    functionToSetFieldsInBloodPressure(),
                    new BloodPressureDataFiveMinAvgDataContainer());
            DataFiveMinAvgDataContainer sop2DataFiveMinAvgDataContainer = computeSop2hDataFiveMinAVR(originData3List,
                    functionToSetFieldsInSop2(),
                    new Sop2HData5MinAvgDataContainer());

            Map<String, Double> mapSummary = getSummarySportsMap(sportsDataFiveMinAvgDataContainer);

            Map<String, DataFiveMinAvgDataContainer> mapDataForExcel = new HashMap<>();

            mapDataForExcel.put(SportsData5MinAvgDataContainer.class.getSimpleName(), sportsDataFiveMinAvgDataContainer);
            mapDataForExcel.put(HeartRateData5MinAvgDataContainer.class.getSimpleName(), heartRateDataFiveMinAvgDataContainer);
            mapDataForExcel.put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName(), bloodPressureDataFiveMinAvgDataContainer);
            mapDataForExcel.put(Sop2HData5MinAvgDataContainer.class.getSimpleName(), sop2DataFiveMinAvgDataContainer);

            XYDataArraysForPlotting sportsArraysForPlotting = getSportsArraysForPlotting(sportsDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting heartRateArraysForPlotting = getHearRateArraysForPlotting(heartRateDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting highBPArraysForPlotting = getHighBPArraysForPlotting(bloodPressureDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting lowBPArraysForPlotting = getLowBPArraysForPlotting(bloodPressureDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting spo2PArraysForPlotting = getSpo2PArraysForPlotting(sop2DataFiveMinAvgDataContainer);


            Map<String, XYDataArraysForPlotting> arraysMap;
            arraysMap = getStringXYDataArraysForPlottingMap(sportsArraysForPlotting,
                    heartRateArraysForPlotting,
                    highBPArraysForPlotting,
                    lowBPArraysForPlotting,
                    spo2PArraysForPlotting);

            mHandler.post(() -> {

                String stringDate = sportsDataFiveMinAvgDataContainer.getStringDate();
                Date formattedDate = DateUtils.getFormattedDate(stringDate, "-");
                LocalDate localDate = DateUtils.getLocalDate(formattedDate, "/");
                if (localDate.compareTo(LocalDate.now()) == 0) {
                    dashBoardViewModel.setTodaySummary(mapSummary);
                    dashBoardViewModel.setTodayArray5MinAvgAllIntervals(arraysMap);
                    dashBoardViewModel.setTodayFullData5MinAvgAllIntervals(mapDataForExcel);
                } else if (localDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
                    dashBoardViewModel.setYesterdaySummary(mapSummary);
                    dashBoardViewModel.setYesterdayArray5MinAvgAllIntervals(arraysMap);
                    dashBoardViewModel.setYesterdayFullData5MinAvgAllIntervals(mapDataForExcel);
                } else if (localDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
                    dashBoardViewModel.setPastYesterdaySummary(mapSummary);
                    dashBoardViewModel.setPastYesterdayArray5MinAvgAllIntervals(arraysMap);
                    dashBoardViewModel.setPastYesterdayFullData5MinAvgAllIntervals(mapDataForExcel);
                }

                DBops.updateHeartRateRow(IdTypeDataTable.HeartRateFiveMin,
                        heartRateDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                        heartRateDataFiveMinAvgDataContainer.getStringDate(),
                        deviceViewModel.getMacAddress(),
                        activity
                );
                DBops.updateSportsRow(IdTypeDataTable.SportsFiveMin,
                        sportsDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                        sportsDataFiveMinAvgDataContainer.getStringDate(),
                        deviceViewModel.getMacAddress(),
                        activity);

                DBops.updateBloodPressureRow(IdTypeDataTable.BloodPressure,
                        bloodPressureDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                        bloodPressureDataFiveMinAvgDataContainer.getStringDate(),
                        deviceViewModel.getMacAddress(),
                        activity);

                DBops.updateSpo2Row(sop2DataFiveMinAvgDataContainer.getDoubleMap().toString(),
                        sop2DataFiveMinAvgDataContainer.getStringDate(),
                        deviceViewModel.getMacAddress(),
                        activity);

            });
        });
    }

    static void processOriginDataList(List<OriginData> list5Min,
                                      Handler mHandler,
                                      DashBoardViewModel dashBoardViewModel,
                                      DeviceViewModel deviceViewModel,
                                      AppCompatActivity activity,
                                      String date) {

        HealthsData.databaseWriteExecutor.execute(() -> {
            DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer = computeSportsDataFiveMinOrigin(list5Min,
                    functionToSetFieldsInSportsOrigin(),
                    new SportsData5MinAvgDataContainer()
            );
            sportsDataFiveMinAvgDataContainer.setStringDate(date);
            DataFiveMinAvgDataContainer heartRateDataFiveMinAvgDataContainer = computeHeartRateDataFiveMinOrigin(list5Min,
                    functionToSetFieldsInRateValueOrigin(),
                    new HeartRateData5MinAvgDataContainer());
            heartRateDataFiveMinAvgDataContainer.setStringDate(date);
            DataFiveMinAvgDataContainer bloodPressureDataFiveMinAvgDataContainer = computeBloodPressureDataFiveMinOrigin(list5Min,
                    functionToSetFieldsInBloodPressureOrigin(),
                    new BloodPressureDataFiveMinAvgDataContainer());
            bloodPressureDataFiveMinAvgDataContainer.setStringDate(date);

            Map<String, Double> mapSummary = getSummarySportsMap(sportsDataFiveMinAvgDataContainer);

            Map<String, DataFiveMinAvgDataContainer> mapDataForExcel = new HashMap<>();

            mapDataForExcel.put(SportsData5MinAvgDataContainer.class.getSimpleName(), sportsDataFiveMinAvgDataContainer);
            mapDataForExcel.put(HeartRateData5MinAvgDataContainer.class.getSimpleName(), heartRateDataFiveMinAvgDataContainer);
            mapDataForExcel.put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName(), bloodPressureDataFiveMinAvgDataContainer);


            XYDataArraysForPlotting sportsArraysForPlotting = getSportsArraysForPlotting(sportsDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting heartRateArraysForPlotting = getHearRateArraysForPlotting(heartRateDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting highBPArraysForPlotting = getHighBPArraysForPlotting(bloodPressureDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting lowBPArraysForPlotting = getLowBPArraysForPlotting(bloodPressureDataFiveMinAvgDataContainer);


            Map<String, XYDataArraysForPlotting> arraysMap;
            arraysMap = getStringXYOriginDataArraysForPlottingMap(sportsArraysForPlotting,
                    heartRateArraysForPlotting,
                    highBPArraysForPlotting,
                    lowBPArraysForPlotting);

            mHandler.post(() -> {

                Log.d(TAG, "processOriginDataList: stringDate  " + date);
                Date formattedDate = DateUtils.getFormattedDate(date, "-");
                LocalDate localDate = DateUtils.getLocalDate(formattedDate, "/");
                if (localDate.compareTo(LocalDate.now()) == 0) {
                    dashBoardViewModel.setTodaySummary(mapSummary);
                    dashBoardViewModel.setTodayArray5MinAvgAllIntervals(arraysMap);
                    dashBoardViewModel.setTodayFullData5MinAvgAllIntervals(mapDataForExcel);
                } else if (localDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
                    dashBoardViewModel.setYesterdaySummary(mapSummary);
                    dashBoardViewModel.setYesterdayArray5MinAvgAllIntervals(arraysMap);
                    dashBoardViewModel.setYesterdayFullData5MinAvgAllIntervals(mapDataForExcel);
                } else if (localDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
                    dashBoardViewModel.setPastYesterdaySummary(mapSummary);
                    dashBoardViewModel.setPastYesterdayArray5MinAvgAllIntervals(arraysMap);
                    dashBoardViewModel.setPastYesterdayFullData5MinAvgAllIntervals(mapDataForExcel);
                }

                DBops.updateHeartRateRow(IdTypeDataTable.HeartRateFiveMin,
                        heartRateDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                        heartRateDataFiveMinAvgDataContainer.getStringDate(),
                        deviceViewModel.getMacAddress(),
                        activity
                );
                DBops.updateSportsRow(IdTypeDataTable.SportsFiveMin,
                        sportsDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                        sportsDataFiveMinAvgDataContainer.getStringDate(),
                        deviceViewModel.getMacAddress(),
                        activity);

                DBops.updateBloodPressureRow(IdTypeDataTable.BloodPressure,
                        bloodPressureDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                        bloodPressureDataFiveMinAvgDataContainer.getStringDate(),
                        deviceViewModel.getMacAddress(),
                        activity);


            });

        });


    }


    @NonNull
    private static Map<String, XYDataArraysForPlotting> getStringXYDataArraysForPlottingMap(XYDataArraysForPlotting sportsArraysForPlotting,
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
    private static Map<String, XYDataArraysForPlotting> getStringXYOriginDataArraysForPlottingMap(XYDataArraysForPlotting sportsArraysForPlotting,
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
    private static Map<String, Double> getSummarySportsMap(DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer) {
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        dataIntervalsMap = sportsDataFiveMinAvgDataContainer.getDoubleMap();
        if (dataIntervalsMap.size() == 0) {
            Map<String, Double> mapSummary = new HashMap<>();
            mapSummary.put("stepCount", 0.0);
            mapSummary.put("caloriesCount", 0.0);
            mapSummary.put("distanceCount", 0.0);
            return mapSummary;
        }

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        Map<String, List<Double>> dataGroupByFieldsWith30MinSumValues;
        dataGroupByFieldsWith30MinSumValues = FragmentUtil
                .getSportsMapFieldsWith30MinCountValues(dataIntervalsList);

        double stepsCount = 0.0;
        List<Double> stepValue = dataGroupByFieldsWith30MinSumValues.get("stepValue");
        if (stepValue != null)
            stepsCount = stepValue.stream().mapToDouble(Double::doubleValue).sum();

        double caloriesCount = 0.0;
        List<Double> calValue = dataGroupByFieldsWith30MinSumValues.get("calValue");
        if (calValue != null)
            caloriesCount = calValue.stream().mapToDouble(Double::doubleValue).sum();

        double distancesCount = 0.0;
        List<Double> disValue = dataGroupByFieldsWith30MinSumValues.get("disValue");
        if (disValue != null)
            distancesCount = disValue.stream().mapToDouble(Double::doubleValue).sum();

        Map<String, Double> mapSummary = new HashMap<>();
        mapSummary.put("stepCount", stepsCount);
        mapSummary.put("caloriesCount", caloriesCount);
        mapSummary.put("distanceCount", distancesCount);
        return mapSummary;
    }

    @NonNull
    @Contract("null -> new")
    private static XYDataArraysForPlotting getSportsArraysForPlotting(DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer) {
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
    private static XYDataArraysForPlotting getHearRateArraysForPlotting(DataFiveMinAvgDataContainer dataIntervalsMapContainer) {
        if (dataIntervalsMapContainer == null) return new XYDataArraysForPlotting();
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        if (dataIntervalsMap == null || dataIntervalsMap.size() == 0 || dataIntervalsMap.size() < 3)
            return new XYDataArraysForPlotting();

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        Double[] subArrayWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(dataIntervalsList, "Ppgs");
        int lengthSubArray = subArrayWithReplacedZeroValuesAsAvg.length;
        if (lengthSubArray < 3) {
            return new XYDataArraysForPlotting();
        }
        String[] timeAxisSubArray = IntervalUtils.getStringFiveMinutesIntervals(lengthSubArray);

        XYDataArraysForPlotting xyDataArraysForPlotting;
        xyDataArraysForPlotting = new XYDataArraysForPlotting(timeAxisSubArray,
                subArrayWithReplacedZeroValuesAsAvg);
        return xyDataArraysForPlotting;
    }

    @NonNull
    @Contract("null -> new")
    private static XYDataArraysForPlotting getHighBPArraysForPlotting(DataFiveMinAvgDataContainer dataIntervalsMapContainer) {
        if (dataIntervalsMapContainer == null) return new XYDataArraysForPlotting();
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        if (dataIntervalsMap == null || dataIntervalsMap.size() == 0 || dataIntervalsMap.size() < 3)
            return new XYDataArraysForPlotting();

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        List<Map<String, Double>> bloodPressureMapFieldsForEach30Min;
        bloodPressureMapFieldsForEach30Min = FragmentUtil.getBloodPressureMapFieldWith30MinAVGValues(dataIntervalsList, "highValue");
        Double[] subArrayWithReplacedZeroValuesAsAvg;
        subArrayWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(bloodPressureMapFieldsForEach30Min,
                "highValue");
        String[] timeAxisSubArrayHP = Arrays.copyOfRange(IntervalUtils.intervalLabels30Min, 0, subArrayWithReplacedZeroValuesAsAvg.length);

        XYDataArraysForPlotting xyDataArraysForPlotting;
        xyDataArraysForPlotting = new XYDataArraysForPlotting(timeAxisSubArrayHP,
                subArrayWithReplacedZeroValuesAsAvg);

        return xyDataArraysForPlotting;
    }

    @NonNull
    @Contract("null -> new")
    private static XYDataArraysForPlotting getLowBPArraysForPlotting(DataFiveMinAvgDataContainer dataIntervalsMapContainer) {
        if (dataIntervalsMapContainer == null) return new XYDataArraysForPlotting();
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        if (dataIntervalsMap == null || dataIntervalsMap.size() == 0 || dataIntervalsMap.size() < 3)
            return new XYDataArraysForPlotting();

        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        List<Map<String, Double>> bloodPressureMapFieldsWith30Min;
        bloodPressureMapFieldsWith30Min = FragmentUtil.getBloodPressureMapFieldWith30MinAVGValues(dataIntervalsList, "lowVaamlue");
        Double[] subArrayWithReplacedZeroValuesAsAvg;
        subArrayWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(bloodPressureMapFieldsWith30Min,
                "lowVaamlue");
        String[] timeAxisSubArrayHP = Arrays.copyOfRange(IntervalUtils.intervalLabels30Min, 0, subArrayWithReplacedZeroValuesAsAvg.length);


        XYDataArraysForPlotting xyDataArraysForPlotting;
        xyDataArraysForPlotting = new XYDataArraysForPlotting(timeAxisSubArrayHP,
                subArrayWithReplacedZeroValuesAsAvg);

        return xyDataArraysForPlotting;
    }

    @NonNull
    @Contract("null -> new")
    private static XYDataArraysForPlotting getSpo2PArraysForPlotting(DataFiveMinAvgDataContainer dataIntervalsMapContainer) {
        if (dataIntervalsMapContainer == null) return new XYDataArraysForPlotting();
        Map<Integer, Map<String, Double>> dataIntervalsMap;
        dataIntervalsMap = dataIntervalsMapContainer.getDoubleMap();
        if (dataIntervalsMap.size() == 0 || dataIntervalsMap.size() < 3) {
            return new XYDataArraysForPlotting();
        }


        List<Map<String, Double>> dataIntervalsList;
        dataIntervalsList = FragmentUtil.mapToList(dataIntervalsMap);

        Double[] subArrayWithReplacedZeroValuesAsAvg = getSubArrayWithReplacedZeroValuesAsAvg(dataIntervalsList, "oxygenValue");
        int lengthSubArray = subArrayWithReplacedZeroValuesAsAvg.length;
        if (lengthSubArray < 3) {
            return new XYDataArraysForPlotting();
        }
        String[] timeAxisSubArray = IntervalUtils.getStringFiveMinutesIntervals(lengthSubArray);
        XYDataArraysForPlotting xyDataArraysForPlotting;
        xyDataArraysForPlotting = new XYDataArraysForPlotting(timeAxisSubArray,
                subArrayWithReplacedZeroValuesAsAvg);

        return xyDataArraysForPlotting;
    }

    public static void processTemperatureDataList(List<TemptureData> list,
                                                  Handler mHandler,
                                                  DashBoardViewModel dashBoardViewModel,
                                                  AppCompatActivity activity,
                                                  DeviceViewModel deviceViewModel) {

        DataFiveMinAvgDataContainer temperatureFiveMinAvgDataContainer = computeTemperatureDataFiveMinAVR(list,
                functionToSetFieldsInTemperatureData(),
                new Temperature5MinDataContainer());

        Log.d(TAG, "processTemperatureDataList: " + temperatureFiveMinAvgDataContainer.getDoubleMap());

    }

    private static DataFiveMinAvgDataContainer computeTemperatureDataFiveMinAVR(List<TemptureData> list,
                                                                                List<BiConsumer<Map<String, Double>, TemptureData>> functionToSetFieldsInTemperatureData,
                                                                                Temperature5MinDataContainer temperature5MinDataContainer) {

        computeDataFiveMinTemperature(list,
                functionToSetFieldsInTemperatureData,
                temperature5MinDataContainer);

        return temperature5MinDataContainer;

    }

    private static void computeDataFiveMinTemperature(List<TemptureData> list,
                                                      List<BiConsumer<Map<String, Double>, TemptureData>> biConsumerList,
                                                      Temperature5MinDataContainer temperature5MinDataContainer) {
        list
            .forEach(data -> {
                TimeData timeData = data.getmTime();
                if (timeData == null) return;
                int interval = IntervalUtils.getInterval5Min(timeData.getHour(),
                        timeData.getMinute());
                Map<String, Double> mapValues = new HashMap<>();
                biConsumerList.forEach(bi -> bi.accept(mapValues, data));
                temperature5MinDataContainer.getDoubleMap().put(interval, mapValues);
            });
    }
}
