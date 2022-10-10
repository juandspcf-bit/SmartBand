package com.misawabus.project.heartRate.device.readData;

import static com.misawabus.project.heartRate.fragments.daysFragments.DayFragment.getContainerDoubleStream;
import static com.misawabus.project.heartRate.fragments.summaryFragments.SummaryFragment.*;

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
import com.misawabus.project.heartRate.plotting.XYDataArraysForPlotting;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.TemptureData;
import com.veepoo.protocol.model.datas.TimeData;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class HealthsReadDataController {

    public static final String TAG = HealthsReadDataController.class.getSimpleName();


    static void processOriginData3List(List<OriginData3> originData3List,
                                       Handler mHandler,
                                       DashBoardViewModel dashBoardViewModel,
                                       AppCompatActivity activity,
                                       DeviceViewModel deviceViewModel) {
        HealthsData.databaseWriteExecutor.execute(() -> {
            DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer = HealthsReadDataGeneratorsForDB.computeSportsDataFiveMinAVR(originData3List,
                    HealthsReadDataGeneratorsForDB.functionToSetFieldsInSportsOrigin3(),
                    new SportsData5MinAvgDataContainer());
            DataFiveMinAvgDataContainer heartRateDataFiveMinAvgDataContainer = HealthsReadDataGeneratorsForDB.computeHearRateDataFiveMinAVR(originData3List,
                    HealthsReadDataGeneratorsForDB.functionToSetFieldsInPpgs(),
                    new HeartRateData5MinAvgDataContainer());
            DataFiveMinAvgDataContainer bloodPressureDataFiveMinAvgDataContainer = HealthsReadDataGeneratorsForDB.computeBloodPressureDataFiveMinAVR(originData3List,
                    HealthsReadDataGeneratorsForDB.functionToSetFieldsInBloodPressure(),
                    new BloodPressureDataFiveMinAvgDataContainer());
            DataFiveMinAvgDataContainer sop2DataFiveMinAvgDataContainer = HealthsReadDataGeneratorsForDB.computeSop2hDataFiveMinAVR(originData3List,
                    HealthsReadDataGeneratorsForDB.functionToSetFieldsInSop2(),
                    new Sop2HData5MinAvgDataContainer());

            Map<String, Double> mapSummary = getSummarySportsMap(sportsDataFiveMinAvgDataContainer);

            Map<String, DataFiveMinAvgDataContainer> mapDataForExcel = new HashMap<>();

            mapDataForExcel.put(SportsData5MinAvgDataContainer.class.getSimpleName(), sportsDataFiveMinAvgDataContainer);
            mapDataForExcel.put(HeartRateData5MinAvgDataContainer.class.getSimpleName(), heartRateDataFiveMinAvgDataContainer);
            mapDataForExcel.put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName(), bloodPressureDataFiveMinAvgDataContainer);
            mapDataForExcel.put(Sop2HData5MinAvgDataContainer.class.getSimpleName(), sop2DataFiveMinAvgDataContainer);

            XYDataArraysForPlotting sportsArraysForPlotting = HealthsReadDataGeneratorsForPlotting.getSportsArraysForPlotting(sportsDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting heartRateArraysForPlotting = HealthsReadDataGeneratorsForPlotting.getHearRateArraysForPlotting(heartRateDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting highBPArraysForPlotting = HealthsReadDataGeneratorsForPlotting.getHighBPArraysForPlotting(bloodPressureDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting lowBPArraysForPlotting = HealthsReadDataGeneratorsForPlotting.getLowBPArraysForPlotting(bloodPressureDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting spo2PArraysForPlotting = HealthsReadDataGeneratorsForPlotting.getSpo2PArraysForPlotting(sop2DataFiveMinAvgDataContainer);


            Map<String, XYDataArraysForPlotting> arraysMap;
            arraysMap = HealthsReadDataGeneratorsForPlotting.getStringXYDataArraysForPlottingMap(sportsArraysForPlotting,
                    heartRateArraysForPlotting,
                    highBPArraysForPlotting,
                    lowBPArraysForPlotting,
                    spo2PArraysForPlotting);

            Map<String, String> summaryTitlesMap = getOrigin3StringSummaryTitlesMap(sportsArraysForPlotting,
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
                    dashBoardViewModel.setTodaySummaryTitles(summaryTitlesMap);
                    dashBoardViewModel.setTodayFullData5MinAvgAllIntervals(mapDataForExcel);
                } else if (localDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
                    dashBoardViewModel.setYesterdaySummary(mapSummary);
                    dashBoardViewModel.setYesterdayArray5MinAvgAllIntervals(arraysMap);
                    dashBoardViewModel.setYesterdaySummaryTitles(summaryTitlesMap);
                    dashBoardViewModel.setYesterdayFullData5MinAvgAllIntervals(mapDataForExcel);
                } else if (localDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
                    dashBoardViewModel.setPastYesterdaySummary(mapSummary);
                    dashBoardViewModel.setPastYesterdayArray5MinAvgAllIntervals(arraysMap);
                    dashBoardViewModel.setPastYesterdaySummaryTitles(summaryTitlesMap);
                    dashBoardViewModel.setPastYesterdayFullData5MinAvgAllIntervals(mapDataForExcel);
                }

                if (heartRateDataFiveMinAvgDataContainer.getDoubleMap() != null
                        && heartRateDataFiveMinAvgDataContainer.getDoubleMap().size() != 0) {
                    DBops.updateHeartRateRow(IdTypeDataTable.HeartRateFiveMin,
                            heartRateDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                            heartRateDataFiveMinAvgDataContainer.getStringDate(),
                            deviceViewModel.getMacAddress(),
                            activity
                    );
                }

                if (sportsDataFiveMinAvgDataContainer.getDoubleMap() != null
                        && sportsDataFiveMinAvgDataContainer.getDoubleMap().size() != 0) {
                    DBops.updateSportsRow(IdTypeDataTable.SportsFiveMin,
                            sportsDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                            sportsDataFiveMinAvgDataContainer.getStringDate(),
                            deviceViewModel.getMacAddress(),
                            activity);
                }

                if (bloodPressureDataFiveMinAvgDataContainer.getDoubleMap() != null
                        && bloodPressureDataFiveMinAvgDataContainer.getDoubleMap().size() != 0) {
                DBops.updateBloodPressureRow(IdTypeDataTable.BloodPressure,
                        bloodPressureDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                        bloodPressureDataFiveMinAvgDataContainer.getStringDate(),
                        deviceViewModel.getMacAddress(),
                        activity);
                }


                if (sop2DataFiveMinAvgDataContainer.getDoubleMap() != null
                        && sop2DataFiveMinAvgDataContainer.getDoubleMap().size() != 0) {
                DBops.updateSpo2Row(sop2DataFiveMinAvgDataContainer.getDoubleMap().toString(),
                        sop2DataFiveMinAvgDataContainer.getStringDate(),
                        deviceViewModel.getMacAddress(),
                        activity);
                }

            });
        });
    }

    @NonNull
    private static Map<String, String> getOrigin3StringSummaryTitlesMap(XYDataArraysForPlotting sportsArraysForPlotting,
                                                                        XYDataArraysForPlotting heartRateArraysForPlotting,
                                                                        XYDataArraysForPlotting highBPArraysForPlotting,
                                                                        XYDataArraysForPlotting lowBPArraysForPlotting,
                                                                        XYDataArraysForPlotting spo2PArraysForPlotting) {
        Map<String, String> summaryTitlesMap = new HashMap<>();

        Double[] rangeDouble = sportsArraysForPlotting.getSeriesDoubleAVR();
        Double collect = Arrays.stream(rangeDouble).mapToDouble(Double::doubleValue).sum();
        String formattedMaxValue = String.format(Locale.getDefault(), "Total: %d steps", collect.longValue());
        summaryTitlesMap
                .put(SportsData5MinAvgDataContainer.class.getSimpleName(),
                        formattedMaxValue);

        rangeDouble = heartRateArraysForPlotting.getSeriesDoubleAVR();
        Optional<ContainerDouble> optionalMaxIndex =
                getContainerDoubleStream(rangeDouble, rangeDouble.length)
                        .max(Comparator.comparing(ContainerDouble::getValue));
        Double maxValue = optionalMaxIndex.orElse(new ContainerDouble(0.0, 0)).getValue();
        formattedMaxValue = String.format(Locale.getDefault(), "Max value: %.1f bpm", maxValue);
        summaryTitlesMap
                .put(HeartRateData5MinAvgDataContainer.class.getSimpleName(),
                        formattedMaxValue);


        rangeDouble = highBPArraysForPlotting.getSeriesDoubleAVR();
        optionalMaxIndex =
                getContainerDoubleStream(rangeDouble, rangeDouble.length)
                        .max(Comparator.comparing(ContainerDouble::getValue));
        int index = optionalMaxIndex.orElse(new ContainerDouble(0.0, 0)).getIndex();
        Double higValue = highBPArraysForPlotting.getSeriesDoubleAVR()[index];
        Double lowValue = lowBPArraysForPlotting.getSeriesDoubleAVR()[index];
        formattedMaxValue = String.format(Locale.getDefault(), "Max value:\n%.1f/%.1f mmHg", higValue, lowValue);
        summaryTitlesMap
                .put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName() + "High",
                        formattedMaxValue);

        rangeDouble = spo2PArraysForPlotting.getSeriesDoubleAVR();
        optionalMaxIndex =
                getContainerDoubleStream(rangeDouble, rangeDouble.length)
                        .min(Comparator.comparing(ContainerDouble::getValue));
        Double minValue = optionalMaxIndex.orElse(new ContainerDouble(0.0, 0)).getValue();
        String formattedMinValue = String.format(Locale.getDefault(), "Min value: %.1f%s", minValue, "%");
        summaryTitlesMap
                .put(Sop2HData5MinAvgDataContainer.class.getSimpleName(),
                        formattedMinValue);
        return summaryTitlesMap;
    }

    static void processOriginDataList(List<OriginData> list5Min,
                                      Handler mHandler,
                                      DashBoardViewModel dashBoardViewModel,
                                      DeviceViewModel deviceViewModel,
                                      AppCompatActivity activity,
                                      String date) {

        HealthsData.databaseWriteExecutor.execute(() -> {
            DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer = HealthsReadDataGeneratorsForDB.computeSportsDataFiveMinOrigin(list5Min,
                    HealthsReadDataGeneratorsForDB.functionToSetFieldsInSportsOrigin(),
                    new SportsData5MinAvgDataContainer()
            );
            sportsDataFiveMinAvgDataContainer.setStringDate(date);
            DataFiveMinAvgDataContainer heartRateDataFiveMinAvgDataContainer = HealthsReadDataGeneratorsForDB.computeHeartRateDataFiveMinOrigin(list5Min,
                    HealthsReadDataGeneratorsForDB.functionToSetFieldsInRateValueOrigin(),
                    new HeartRateData5MinAvgDataContainer());
            heartRateDataFiveMinAvgDataContainer.setStringDate(date);
            DataFiveMinAvgDataContainer bloodPressureDataFiveMinAvgDataContainer = HealthsReadDataGeneratorsForDB.computeBloodPressureDataFiveMinOrigin(list5Min,
                    HealthsReadDataGeneratorsForDB.functionToSetFieldsInBloodPressureOrigin(),
                    new BloodPressureDataFiveMinAvgDataContainer());
            bloodPressureDataFiveMinAvgDataContainer.setStringDate(date);

            Map<String, Double> mapSummary = getSummarySportsMap(sportsDataFiveMinAvgDataContainer);

            Map<String, DataFiveMinAvgDataContainer> mapDataForExcel = new HashMap<>();

            mapDataForExcel.put(SportsData5MinAvgDataContainer.class.getSimpleName(), sportsDataFiveMinAvgDataContainer);
            mapDataForExcel.put(HeartRateData5MinAvgDataContainer.class.getSimpleName(), heartRateDataFiveMinAvgDataContainer);
            mapDataForExcel.put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName(), bloodPressureDataFiveMinAvgDataContainer);


            XYDataArraysForPlotting sportsArraysForPlotting = HealthsReadDataGeneratorsForPlotting.getSportsArraysForPlotting(sportsDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting heartRateArraysForPlotting = HealthsReadDataGeneratorsForPlotting.getHearRateArraysForPlotting(heartRateDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting highBPArraysForPlotting = HealthsReadDataGeneratorsForPlotting.getHighBPArraysForPlotting(bloodPressureDataFiveMinAvgDataContainer);
            XYDataArraysForPlotting lowBPArraysForPlotting = HealthsReadDataGeneratorsForPlotting.getLowBPArraysForPlotting(bloodPressureDataFiveMinAvgDataContainer);


            Map<String, XYDataArraysForPlotting> arraysMap;
            arraysMap = HealthsReadDataGeneratorsForPlotting.getMapContainerXYOriginDataArraysForPlotting(sportsArraysForPlotting,
                    heartRateArraysForPlotting,
                    highBPArraysForPlotting,
                    lowBPArraysForPlotting);

            Map<String, String> summaryTitlesMap = getOriginStringSummaryTitlesMap(sportsArraysForPlotting,
                    heartRateArraysForPlotting,
                    highBPArraysForPlotting,
                    lowBPArraysForPlotting);

            mHandler.post(() -> {
                Date formattedDate = DateUtils.getFormattedDate(date, "-");
                LocalDate localDate = DateUtils.getLocalDate(formattedDate, "/");
                if (localDate.compareTo(LocalDate.now()) == 0) {
                    dashBoardViewModel.setTodaySummary(mapSummary);
                    dashBoardViewModel.setTodaySummaryTitles(summaryTitlesMap);
                    dashBoardViewModel.setTodayArray5MinAvgAllIntervals(arraysMap);
                    dashBoardViewModel.setTodayFullData5MinAvgAllIntervals(mapDataForExcel);
                } else if (localDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
                    dashBoardViewModel.setYesterdaySummary(mapSummary);
                    dashBoardViewModel.setYesterdaySummaryTitles(summaryTitlesMap);
                    dashBoardViewModel.setYesterdayArray5MinAvgAllIntervals(arraysMap);
                    dashBoardViewModel.setYesterdayFullData5MinAvgAllIntervals(mapDataForExcel);
                } else if (localDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
                    dashBoardViewModel.setPastYesterdaySummary(mapSummary);
                    dashBoardViewModel.setPastYesterdaySummaryTitles(summaryTitlesMap);
                    dashBoardViewModel.setPastYesterdayArray5MinAvgAllIntervals(arraysMap);
                    dashBoardViewModel.setPastYesterdayFullData5MinAvgAllIntervals(mapDataForExcel);
                }

                if (heartRateDataFiveMinAvgDataContainer.getDoubleMap() != null
                        && heartRateDataFiveMinAvgDataContainer.getDoubleMap().size() != 0) {
                    DBops.updateHeartRateRow(IdTypeDataTable.HeartRateFiveMin,
                            heartRateDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                            heartRateDataFiveMinAvgDataContainer.getStringDate(),
                            deviceViewModel.getMacAddress(),
                            activity
                    );
                }

                if (sportsDataFiveMinAvgDataContainer.getDoubleMap() != null
                        && sportsDataFiveMinAvgDataContainer.getDoubleMap().size() != 0) {
                    DBops.updateSportsRow(IdTypeDataTable.SportsFiveMin,
                            sportsDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                            sportsDataFiveMinAvgDataContainer.getStringDate(),
                            deviceViewModel.getMacAddress(),
                            activity);
                }

                if (bloodPressureDataFiveMinAvgDataContainer.getDoubleMap() != null
                        && bloodPressureDataFiveMinAvgDataContainer.getDoubleMap().size() != 0) {
                    DBops.updateBloodPressureRow(IdTypeDataTable.BloodPressure,
                            bloodPressureDataFiveMinAvgDataContainer.getDoubleMap().toString(),
                            bloodPressureDataFiveMinAvgDataContainer.getStringDate(),
                            deviceViewModel.getMacAddress(),
                            activity);
                }


            });

        });


    }

    @NonNull
    private static Map<String, String> getOriginStringSummaryTitlesMap(XYDataArraysForPlotting sportsArraysForPlotting,
                                                                       XYDataArraysForPlotting heartRateArraysForPlotting,
                                                                       XYDataArraysForPlotting highBPArraysForPlotting,
                                                                       XYDataArraysForPlotting lowBPArraysForPlotting) {
        Map<String, String> summaryTitlesMap = new HashMap<>();

        Double[] rangeDouble = sportsArraysForPlotting.getSeriesDoubleAVR();
        Double collect = Arrays.stream(rangeDouble).mapToDouble(Double::doubleValue).sum();
        String formattedMaxValue = String.format(Locale.getDefault(), "Total: %d steps", collect.longValue());
        summaryTitlesMap
                .put(SportsData5MinAvgDataContainer.class.getSimpleName(),
                        formattedMaxValue);

        rangeDouble = heartRateArraysForPlotting.getSeriesDoubleAVR();
        Optional<ContainerDouble> optionalMaxIndex =
                getContainerDoubleStream(rangeDouble, rangeDouble.length)
                        .max(Comparator.comparing(ContainerDouble::getValue));
        Double maxValue = optionalMaxIndex.orElse(new ContainerDouble(0.0, 0)).getValue();
        formattedMaxValue = String.format(Locale.getDefault(), "Max value: %.1f bpm", maxValue);
        summaryTitlesMap
                .put(HeartRateData5MinAvgDataContainer.class.getSimpleName(),
                        formattedMaxValue);


        rangeDouble = highBPArraysForPlotting.getSeriesDoubleAVR();
        optionalMaxIndex =
                getContainerDoubleStream(rangeDouble, rangeDouble.length)
                        .max(Comparator.comparing(ContainerDouble::getValue));
        int index = optionalMaxIndex.orElse(new ContainerDouble(0.0, 0)).getIndex();
        Double higValue = highBPArraysForPlotting.getSeriesDoubleAVR()[index];
        Double lowValue = lowBPArraysForPlotting.getSeriesDoubleAVR()[index];
        formattedMaxValue = String.format(Locale.getDefault(), "Max value:\n%.1f/%.1f mmHg", higValue, lowValue);
        summaryTitlesMap
                .put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName() + "High",
                        formattedMaxValue);
        return summaryTitlesMap;

    }


    @NonNull
    public static Map<String, Double> getSummarySportsMap(DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer) {
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


    public static void processTemperatureDataList(List<TemptureData> list,
                                                  Handler mHandler,
                                                  DashBoardViewModel dashBoardViewModel,
                                                  AppCompatActivity activity,
                                                  DeviceViewModel deviceViewModel) {

        HealthsData.databaseWriteExecutor.execute(() -> {
            DataFiveMinAvgDataContainer temperatureFiveMinAvgDataContainer = computeTemperatureDataFiveMinAVR(list,
                    HealthsReadDataGeneratorsForDB.functionToSetFieldsInTemperatureData(),
                    new Temperature5MinDataContainer());

            Log.d(TAG, "processTemperatureDataList: " + temperatureFiveMinAvgDataContainer.getDoubleMap());

            XYDataArraysForPlotting bodyTemperatureArraysForPlotting;
            bodyTemperatureArraysForPlotting = HealthsReadDataGeneratorsForPlotting.getBodyTemperatureArraysForPlotting(temperatureFiveMinAvgDataContainer);
            XYDataArraysForPlotting skinTemperatureArraysForPlotting;
            skinTemperatureArraysForPlotting = HealthsReadDataGeneratorsForPlotting.getBodyTemperatureArraysForPlotting(temperatureFiveMinAvgDataContainer);

            Map<String, XYDataArraysForPlotting> arraysMap = new HashMap<>();
            arraysMap
                    .put(Temperature5MinDataContainer.class.getSimpleName()+":body",
                            bodyTemperatureArraysForPlotting);
            arraysMap
                    .put(Temperature5MinDataContainer.class.getSimpleName()+":skin",
                            skinTemperatureArraysForPlotting);

            mHandler.post(() -> {
                TimeData timeData = list.get(0).getmTime();
                LocalDate localDate = DateUtils.getLocalDateFromVeepooTimeDateObj(timeData.toString());
                if (localDate.compareTo(LocalDate.now()) == 0) {
                    dashBoardViewModel.setTodayArrayTempAllIntervals(arraysMap);
                } else if (localDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
                    dashBoardViewModel.setYesterdayArrayTempAllIntervals(arraysMap);
                } else if (localDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
                    dashBoardViewModel.setPastYesterdayArrayTempAllIntervals(arraysMap);
                }
            });

        });

    }

    public static DataFiveMinAvgDataContainer computeTemperatureDataFiveMinAVR(List<TemptureData> list,
                                                                               List<BiConsumer<Map<String, Double>, TemptureData>> functionToSetFieldsInTemperatureData,
                                                                               Temperature5MinDataContainer temperature5MinDataContainer) {

        computeDataFiveMinTemperature(list,
                functionToSetFieldsInTemperatureData,
                temperature5MinDataContainer);

        return temperature5MinDataContainer;

    }

    public static void computeDataFiveMinTemperature(List<TemptureData> list,
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
