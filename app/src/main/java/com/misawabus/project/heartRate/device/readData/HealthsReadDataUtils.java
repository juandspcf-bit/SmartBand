package com.misawabus.project.heartRate.device.readData;

import static java.util.Arrays.stream;

import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.Utils.DBops;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.device.entities.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.Sop2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.summaryFragments.utils.UtilsSummaryFrag;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.TimeData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

public class HealthsReadDataUtils {

    public static final String TAG = HealthsData.class.getSimpleName();


    public static DataFiveMinAvgDataContainer computeSportsDataFiveMinAVR(List<OriginData3> originData3List,
                                                                          List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList, DataFiveMinAvgDataContainer fieldDataFiveMinAVGAllIntervals) {
        computeDataFiveMinAVG(originData3List, biConsumerList, fieldDataFiveMinAVGAllIntervals);

        return fieldDataFiveMinAVGAllIntervals;

    }

    public static DataFiveMinAvgDataContainer computeHearRateDataFiveMinAVR(List<OriginData3> originData3List,
                                                                            List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList,
                                                                            DataFiveMinAvgDataContainer fieldDataFiveMinAVGAllIntervals) {

        computeDataFiveMinAVG(originData3List, biConsumerList, fieldDataFiveMinAVGAllIntervals);
        return fieldDataFiveMinAVGAllIntervals;

    }

    public static DataFiveMinAvgDataContainer computeBloodPressureDataFiveMinAVR(List<OriginData3> originData3List,
                                                                                 List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList, DataFiveMinAvgDataContainer fieldDataFiveMinAVGAllIntervals) {
        computeDataFiveMinAVG(originData3List, biConsumerList, fieldDataFiveMinAVGAllIntervals);
        return fieldDataFiveMinAVGAllIntervals;
    }

    public static DataFiveMinAvgDataContainer computeSop2hDataFiveMinAVR(List<OriginData3> originData3List,
                                                                         List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList, DataFiveMinAvgDataContainer fieldDataFiveMinAVGAllIntervals) {

        computeDataFiveMinAVG(originData3List, biConsumerList, fieldDataFiveMinAVGAllIntervals);
        return fieldDataFiveMinAVGAllIntervals;
    }

    private static void computeDataFiveMinAVG(List<OriginData3> originData3List, List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList, DataFiveMinAvgDataContainer fieldDataFiveMinAVGAllIntervals) {
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



    private static OptionalDouble get5MinAVG(int[] results) {
        return stream(results)
                .filter(value -> value > 0)
                .average();
    }


    public static List<BiConsumer<Map<String, Double>, OriginData3>> functionToSetFieldsInSop2(){

        List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList = new ArrayList<>();

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("apneaResult",
                get5MinAVG(originData3.getApneaResults()).orElse(0)));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("oxygenValue",
                get5MinAVG(originData3.getOxygens()).orElse(0)));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("respirationRate",
                get5MinAVG(originData3.getResRates()).orElse(0)));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("isHypoxia",
                get5MinAVG(originData3.getHypoxiaTimes()).orElse(0)));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("cardiacLoad",
                get5MinAVG(originData3.getCardiacLoads()).orElse(0)));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("SleepActivity",
                get5MinAVG(originData3.getSleepStates()).orElse(0)));


        return biConsumerList;
    }

    public static List<BiConsumer<Map<String, Double>, OriginData3>> functionToSetFieldsInPpgs(){

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

    public static List<BiConsumer<Map<String, Double>, OriginData3>> functionToSetFieldsInSports(){

        List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList = new ArrayList<>();

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("stepValue",
                (double) originData3.getStepValue()));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("calValue",
                originData3.getCalValue()));

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("disValue",
                originData3.getDisValue()));

        return biConsumerList;
    }

    public static List<BiConsumer<Map<String, Double>, OriginData3>> functionToSetFieldsInBloodPressure(){

        List<BiConsumer<Map<String, Double>, OriginData3>> biConsumerList = new ArrayList<>();

        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("highValue",
                (double) originData3.getHighValue()));
        
        biConsumerList.add((doubleMap, originData3) -> doubleMap.put("lowVaamlue",
                (double) originData3.getLowValue()));
        return biConsumerList;
    }


    static void processOriginData3List(List<OriginData3> originData3List,
                                       ExecutorService databaseWriteExecutor,
                                       Handler mHandler,
                                       DashBoardViewModel dashBoardViewModel,
                                       AppCompatActivity activity,
                                       DeviceViewModel deviceViewModel) {
        databaseWriteExecutor.execute(() -> {
            DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer = computeSportsDataFiveMinAVR(originData3List,
                            functionToSetFieldsInSports(),
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

            Map<String, DataFiveMinAvgDataContainer> dataFiveMinAVGAllIntervalsMap = new HashMap<>();
            dataFiveMinAVGAllIntervalsMap
                    .put(SportsData5MinAvgDataContainer.class.getSimpleName(),
                            sportsDataFiveMinAvgDataContainer);
            dataFiveMinAVGAllIntervalsMap
                    .put(HeartRateData5MinAvgDataContainer.class.getSimpleName(),
                            heartRateDataFiveMinAvgDataContainer);
            dataFiveMinAVGAllIntervalsMap
                    .put(BloodPressureDataFiveMinAvgDataContainer.class.getSimpleName(),
                            bloodPressureDataFiveMinAvgDataContainer);
            dataFiveMinAVGAllIntervalsMap
                    .put(Sop2HData5MinAvgDataContainer.class.getSimpleName(),
                            sop2DataFiveMinAvgDataContainer);

            mHandler.post(() -> {

                String stringDate = heartRateDataFiveMinAvgDataContainer.getStringDate();
                Date formattedDate = DateUtils.getFormattedDate(stringDate, "-");
                LocalDate localDate = DateUtils.getLocalDate(formattedDate, "/");
                if (localDate.compareTo(LocalDate.now()) == 0) {
                    dashBoardViewModel.setTodayFullData5MinAvgAllIntervals(dataFiveMinAVGAllIntervalsMap);
                } else if (localDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
                    dashBoardViewModel.setYesterdayFullData5MinAvgAllIntervals(dataFiveMinAVGAllIntervalsMap);
                } else if (localDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
                    dashBoardViewModel.setPastYesterdayFullData5MinAvgAllIntervals(dataFiveMinAVGAllIntervalsMap);
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
}
