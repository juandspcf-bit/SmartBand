package com.misawabus.project.heartRate.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.misawabus.project.heartRate.Database.entities.Device;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.device.DataContainers.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.DataContainers.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.fragments.fragmentUtils.FragmentUtil;
import com.misawabus.project.heartRate.fragments.summaryFragments.utils.UtilsSummaryFrag;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class ExcelConversionUtils {

    private static final List<String> stringsIntervals = Arrays.asList(
            "00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30", "04:00",
            "04:30", "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00", "08:30",
            "9:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00",
            "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30",
            "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00",
            "22:30", "23:00", "23:30");
    private static final String TAG = ExcelConversionUtils.class.getSimpleName();


    public static void createWorkbook(Context context,
                                      FragmentActivity fragmentActivity,
                                      String macAddress,
                                      Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap,
                                      List<SleepDataUI> sleepDataListMaps,
                                      Device device) {

        Workbook workbook = new HSSFWorkbook();
        String date = stringDataFiveMinAVGAllIntervalsMap
                .get(SportsData5MinAvgDataContainer.class.getSimpleName())
                .getStringDate();

        Sheet sheet = workbook
                .createSheet("data for " + date);
        createHeadersForSheet1(sheet);


        Map<String, List<Double>> mapSportsFieldsWith30MinValues = getSportsData(stringDataFiveMinAVGAllIntervalsMap);
        if (mapSportsFieldsWith30MinValues == null) return;
        int lastRowNumWithData = sheet.getLastRowNum();
        createSportsRows(macAddress, date, sheet, mapSportsFieldsWith30MinValues, lastRowNumWithData);


        Map<String, List<Double>> mapHeartRateFieldsWith30MinValues = getHeartRateData(stringDataFiveMinAVGAllIntervalsMap);
        mapHeartRateFieldsWith30MinValues.forEach((key, value)->{
            Log.d(TAG, "key-value: " + key + " : " + value);
        });

        lastRowNumWithData = sheet.getLastRowNum();
        createHeartRateRows(macAddress, date, sheet, mapHeartRateFieldsWith30MinValues, lastRowNumWithData);


        Map<String, List<Double>> mapBloodPressureFieldsWith30MinValues = getBloodPressureData(stringDataFiveMinAVGAllIntervalsMap);
        lastRowNumWithData = sheet.getLastRowNum();
        createBloodPressureRows(macAddress, date, sheet, mapBloodPressureFieldsWith30MinValues, lastRowNumWithData);

        int summaryRowNumber = sheet.getLastRowNum() + 5;
        createCountSummaryHeaders(sheet, summaryRowNumber, "Summary value", "Count");
        summaryRowNumber = sheet.getLastRowNum();
        
        createSportsCount(sheet, mapSportsFieldsWith30MinValues, summaryRowNumber);


        summaryRowNumber = sheet.getLastRowNum() + 2;
        createCountSummaryHeaders(sheet, summaryRowNumber, "Summary value", "Average");
        summaryRowNumber = sheet.getLastRowNum();

        createHeartRateAVG(sheet, mapHeartRateFieldsWith30MinValues, summaryRowNumber);
        summaryRowNumber = sheet.getLastRowNum();
        createBloodPressureAVGRows(sheet, mapBloodPressureFieldsWith30MinValues, summaryRowNumber);


        int accountRowNumber = sheet.getLastRowNum() + 5;
        createPersonalInfoRows(device, sheet, accountRowNumber);

        createSleepSheet(macAddress, sleepDataListMaps, workbook, date);
        createECGData(context, fragmentActivity, workbook);




    }

    private static void createSleepSheet(String macAddress, List<SleepDataUI> sleepDataListMaps, Workbook workbook, String date) {
        Sheet sheetSleep = workbook.createSheet("Sleep data for " + date);
        final int sleepRowNumber = 3;
        if (sleepDataListMaps != null) {
            Row rowSleepAxis = sheetSleep.createRow(sleepRowNumber);

            for (int i = 0, b = 0; i <= sleepDataListMaps.size() - 1; i++, b += 8) {
                SleepDataUI sleepDataUImap = sleepDataListMaps.get(i);
                List<Integer> sleepDataValues = FragmentUtil.getSleepDataForExcel(sleepDataUImap.getData());

                String sleepDownTimeData = sleepDataUImap.getSleepDown();
                sleepDownTimeData = sleepDownTimeData.substring(sleepDownTimeData.lastIndexOf("[") + 1, sleepDownTimeData.lastIndexOf("]"));
                String sleepDownTime = sleepDownTimeData.split(" ")[1];
                String[] sleepDownTimeSplit = sleepDownTime.split(":");
                LocalTime sleepDownLocalTime = LocalTime.of(Integer.parseInt(sleepDownTimeSplit[0]),
                        Integer.parseInt(sleepDownTimeSplit[1]),
                        Integer.parseInt(sleepDownTimeSplit[2]));


                String sleepUpTimeData = sleepDataUImap.getSleepUp();
                sleepUpTimeData = sleepUpTimeData.substring(sleepUpTimeData.lastIndexOf("[") + 1, sleepUpTimeData.lastIndexOf("]"));
                String sleepUpTime = sleepUpTimeData.split(" ")[1];
                String[] sleepUpTimeSplit = sleepUpTime.split(":");

                LocalTime sleepUpTimeLocalTime = LocalTime.of(Integer.parseInt(sleepUpTimeSplit[0]),
                        Integer.parseInt(sleepUpTimeSplit[1]),
                        Integer.parseInt(sleepUpTimeSplit[2]));

                List<LocalTime> axisTimes = new ArrayList<>();
                LocalTime difference = sleepUpTimeLocalTime.minusMinutes(sleepDownLocalTime.getMinute());
                difference = difference.minusHours(sleepDownLocalTime.getHour());
                difference = difference.minusSeconds(sleepDownLocalTime.getSecond());
                double time = difference.getHour() * 60.0 + difference.getMinute() + difference.getSecond() / 60.0;
                double stepsSampling = Math.abs(time / sleepDataUImap.getData().length());

                if (stepsSampling > 1) {
                    long minutesPlus = Math.round(stepsSampling);
                    Stream.iterate(0, j -> ++j).limit(sleepDataValues.size())
                            .forEach(index -> axisTimes.add(sleepDownLocalTime.plusMinutes(minutesPlus * index)));

                } else {
                    long secondsPLus = Math.round(stepsSampling * 60.0);
                    Stream.iterate(0, j -> ++j).limit(sleepDataValues.size())
                            .forEach(index -> axisTimes.add(sleepDownLocalTime.plusSeconds(secondsPLus * index)));
                }


                rowSleepAxis.createCell(b).setCellValue("Sleep Quality");
                rowSleepAxis.createCell(1 + b).setCellValue(macAddress);
                rowSleepAxis.createCell(2 + b).setCellValue(date);

                rowSleepAxis.createCell(3 + b).setCellValue("time");
                rowSleepAxis.createCell(4 + b).setCellValue("Sleep Values");
                rowSleepAxis.createCell(5 + b).setCellValue("Sleep Deepness");

                for (int index = 1; index < sleepDataValues.size(); index++) {
                    Row row = sheetSleep.getRow(sleepRowNumber + index);
                    if (row == null) {
                        row = sheetSleep.createRow(sleepRowNumber + index);
                    }
                    row.createCell(3 + b).setCellValue(axisTimes.get(index).toString());
                    row.createCell(4 + b).setCellValue(sleepDataValues.get(index));
                    if (sleepDataValues.get(index) == 2) {
                        row.createCell(5 + b).setCellValue("light sleep");
                    } else if (sleepDataValues.get(index) == 1) {
                        row.createCell(5 + b).setCellValue("deep sleep");
                    } else {
                        row.createCell(5 + b).setCellValue("wake up");
                    }

                }

            }
        }
    }

    private static void createPersonalInfoRows(Device device, Sheet sheet, int accountRowNumber) {
        if (device == null) device = new Device();

        String[] fieldsDevice = {"Name", "Gender", "Birthday", "Weight", "Height"};
        String[] valuesDevice = {device.getName() != null ? device.getName() : "",
                device.getGender() != null ? device.getGender() : "",
                device.getBirthDate() != null ? DateFormat
                        .getDateInstance(DateFormat.SHORT, Locale.JAPAN)
                        .format(device.getBirthDate()) : "",
                device.getWeight() != null ? device.getWeight() : "",
                device.getHeight() != null ? device.getHeight() : ""};

        for (int i = 0; i < fieldsDevice.length; i++) {
            createCountSummaryHeaders(sheet, accountRowNumber + i, fieldsDevice[i], valuesDevice[i]);
        }
    }

    private static void createCountSummaryHeaders(Sheet sheet, int summaryRowNumber, String s, String count) {
        Row summaryRow = sheet.createRow(summaryRowNumber);
        summaryRow.createCell(0).setCellValue(s);
        summaryRow.createCell(1).setCellValue(count);
    }

    private static void createSportsCount(Sheet sheet, Map<String, List<Double>> mapFieldsWith30MinValues, int summaryRowNumber) {



        List<String> listFields = Arrays.asList("stepValue", "calValue", "disValue");
        List<String> listFieldsLabels = Arrays.asList("Steps Count", "Calories count", "Distances count");

        Stream.iterate(1, i -> ++i).limit(listFields.size()).forEach(index ->{
            List<Double> doubleList = mapFieldsWith30MinValues.get(listFields.get(index-1));
            double count = doubleList.stream().mapToDouble(Double::doubleValue).sum();

            Row row = sheet.createRow(summaryRowNumber + index);
            row.createCell(0).setCellValue(listFieldsLabels.get(index-1));
            row.createCell(1).setCellValue(count);

        });
    }


    private static void createHeartRateAVG(Sheet sheet, Map<String, List<Double>> mapFieldsWith30MinValues, int summaryRowNumber) {


        List<String> listFields = List.of("Ppgs");
        List<String> listFieldsLabels = List.of("Heart Rate");

        Stream.iterate(1, i -> ++i).limit(listFields.size()).forEach(index ->{
            List<Double> doubleList = mapFieldsWith30MinValues.get(listFields.get(index-1));
            double count = doubleList.stream().filter(data->data>0.0).mapToDouble(Double::doubleValue).average().orElse(0.0);

            Row row = sheet.createRow(summaryRowNumber + index);
            row.createCell(0).setCellValue(listFieldsLabels.get(index-1));
            row.createCell(1).setCellValue(count);

        });
    }

    private static void createBloodPressureAVGRows(Sheet sheet, Map<String, List<Double>> mapFieldsWith30MinValues, int summaryRowNumber) {


        List<String> listFields = Arrays.asList("highValue", "lowVaamlue");
        List<String> listFieldsLabels = Arrays.asList("High Pressure Average", "Low Pressure Average");

        Stream.iterate(1, i -> ++i).limit(listFields.size()).forEach(index ->{
            List<Double> doubleList = mapFieldsWith30MinValues.get(listFields.get(index-1));
            double count = doubleList.stream().filter(data->data>0.0).mapToDouble(Double::doubleValue).average().orElse(0.0);

            Row row = sheet.createRow(summaryRowNumber + index);
            row.createCell(0).setCellValue(listFieldsLabels.get(index-1));
            row.createCell(1).setCellValue(count);

        });
    }


    private static void createBloodPressureRows(String macAddress, String date, Sheet sheet, Map<String, List<Double>> mapFieldsWith30MinValues, int lastRowNumWithData) {
        List<String> listFields = Arrays.asList("highValue", "lowVaamlue");
        Stream.iterate(1, i -> ++i).limit(listFields.size()).forEach(index ->{
            Row rowFieldInfo = sheet.createRow(lastRowNumWithData + index);
            rowFieldInfo.createCell(0).setCellValue(listFields.get(index-1));
            rowFieldInfo.createCell(1).setCellValue(macAddress);
            rowFieldInfo.createCell(2).setCellValue(date);

            List<Double> doubleList = mapFieldsWith30MinValues.get(listFields.get(index-1));
            for (int i = 0; i < doubleList.size(); i++) {
                rowFieldInfo.createCell(i + 3);
                rowFieldInfo.getCell(i + 3).setCellValue(doubleList.get(i));
            }

        });
    }

    private static Map<String, List<Double>> getBloodPressureData(Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap) {
        DataFiveMinAvgDataContainer bloodPressureDataFiveMinAvgDataContainer
                = stringDataFiveMinAVGAllIntervalsMap.get(BloodPressureDataFiveMinAvgDataContainer
                .class.getSimpleName());
        Map<Integer, Map<String, Double>> bloodPressure = bloodPressureDataFiveMinAvgDataContainer.getDoubleMap();

        List<Map<String, Double>> bloodPressureDataMap = FragmentUtil.parse5MinFieldData(bloodPressure.toString());
        return FragmentUtil.getBPDataGroupByFieldsWith30MinSumValuesForExcel(bloodPressureDataMap);
    }

    private static Map<String, List<Double>> getHeartRateData(Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap) {
        DataFiveMinAvgDataContainer heartRateData5MinAVGAllIntervals
                = stringDataFiveMinAVGAllIntervalsMap.get(HeartRateData5MinAvgDataContainer
                .class.getSimpleName());
        Map<Integer, Map<String, Double>> heartRateData = heartRateData5MinAVGAllIntervals.getDoubleMap();
        List<Map<String, Double>> heartRateDataMap = FragmentUtil.parse5MinFieldData(heartRateData.toString());
        Log.d(TAG, "getHeartRateData: " + heartRateData);
        return FragmentUtil.getHeartRateDataGroupByFieldsWith30MinSumValues(heartRateDataMap);
    }

    @Nullable
    private static Map<String, List<Double>> getSportsData(Map<String, DataFiveMinAvgDataContainer> stringDataFiveMinAVGAllIntervalsMap) {
        DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer
                = stringDataFiveMinAVGAllIntervalsMap.get(SportsData5MinAvgDataContainer
                .class.getSimpleName());
        Map<Integer, Map<String, Double>> sportsData = sportsDataFiveMinAvgDataContainer.getDoubleMap();


        String stringData = sportsData.toString();
        List<Map<String, Double>> sportsDataMap = FragmentUtil.parse5MinFieldData(stringData);

        if(sportsDataMap.get(0).isEmpty()) return null;
        return FragmentUtil.getSportsMapFieldsWith30MinCountValues(sportsDataMap);
    }

    private static void createSportsRows(String macAddress, String date, Sheet sheet, Map<String, List<Double>> mapFieldsWith30MinValues, int lastRowNumWithData) {
        List<String> listFields = Arrays.asList("stepValue", "calValue", "disValue");
        Stream.iterate(1, i -> ++i).limit(listFields.size()).forEach(index ->{
            Row rowFieldInfo = sheet.createRow(lastRowNumWithData + index);
            rowFieldInfo.createCell(0).setCellValue(listFields.get(index-1));
            rowFieldInfo.createCell(1).setCellValue(macAddress);
            rowFieldInfo.createCell(2).setCellValue(date);

            List<Double> doubleList = mapFieldsWith30MinValues.get(listFields.get(index-1));
            for (int i = 0; i < doubleList.size(); i++) {
                rowFieldInfo.createCell(i + 3);
                rowFieldInfo.getCell(i + 3).setCellValue(doubleList.get(i));
            }

        });
    }

    private static void createHeartRateRows(String macAddress, String date, Sheet sheet, Map<String, List<Double>> mapFieldsWith30MinValues, int lastRowNumWithData) {
        List<String> listFields = List.of("Ppgs", "Activity");
        List<String> listFieldsLabels = List.of("Heart Rate", "Activity Zone");


        Stream.iterate(1, i -> ++i).limit(listFields.size()).forEach(index ->{
            Row rowFieldInfo = sheet.createRow(lastRowNumWithData + index);
            rowFieldInfo.createCell(0).setCellValue(listFieldsLabels.get(index-1));
            rowFieldInfo.createCell(1).setCellValue(macAddress);
            rowFieldInfo.createCell(2).setCellValue(date);


            //TODO improve this section to avoid if() checking in each iteration

            List<Double> doubleList = mapFieldsWith30MinValues.get(listFields.get(index-1));
            for (int i = 0; i < doubleList.size(); i++) {
                rowFieldInfo.createCell(i + 3);
                if(listFieldsLabels.get(index-1).equals("Activity Zone")){
                    double aDouble = doubleList.get(i);
                    String zone = UtilsSummaryFrag.activityZonesMapping.get((int) aDouble);
                    rowFieldInfo.getCell(i + 3).setCellValue(zone);
                }else{
                    rowFieldInfo.getCell(i + 3).setCellValue(doubleList.get(i));
                }

            }

        });
    }

    private static void createHeadersForSheet1(Sheet sheet) {
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("Field");
        sheet.setColumnWidth(0, 18 * 256);
        row0.createCell(1).setCellValue("Mac Address");


        sheet.setColumnWidth(1, 20 * 256);

        row0.createCell(2).setCellValue("Date");
        sheet.setColumnWidth(2, 15 * 256);

        for (int i = 0; i < stringsIntervals.size(); i++) {
            row0.createCell(i + 3);
            row0.getCell(i + 3).setCellValue(stringsIntervals.get(i));
        }
    }

    private static void createECGData(Context context, FragmentActivity fragmentActivity, Workbook workbook) {
        Sheet sheetEcgPPT = workbook.createSheet("PPT data for " + LocalDate.now());
        final int pptRowNumber = 3;


        DashBoardViewModel dashBoardViewModel = new ViewModelProvider(fragmentActivity).get(DashBoardViewModel.class);
        List<int[]> dataEcg = dashBoardViewModel.getDataEcg();


        final ExecutorService excelWriteExecutor =
                Executors.newFixedThreadPool(4);

        if (dataEcg != null) {

            Row titlePPT = sheetEcgPPT.createRow(pptRowNumber);
            titlePPT.createCell(3).setCellValue("PPT signal at " + LocalTime.now());

            excelWriteExecutor.execute(() -> {
                for (int i = 0; i < dataEcg.size() - 1; i++) {
                    int[] ints = dataEcg.get(i);

                    for (int j = 1; j < ints.length; j++) {
                        Row rowPPTpAxis = sheetEcgPPT.createRow(sheetEcgPPT.getLastRowNum() + 1);
                        rowPPTpAxis.createCell(3).setCellValue(ints[j]);
                    }
                }
                createFiles(context, workbook);
            });

        } else {
            excelWriteExecutor.execute(() -> createFiles(context, workbook));

        }
    }

    private static void createFiles(Context context, Workbook workbook) {
        File fileExcel = new File(context.getExternalFilesDir(null), "fullData1.xls");

        try (FileOutputStream fileExcelOutputStream = new FileOutputStream(fileExcel)) {

            workbook.write(fileExcelOutputStream);

            Uri apkURI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", fileExcel);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, "data");
            intent.putExtra(Intent.EXTRA_STREAM, apkURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("STORED", "Unsuccessfully written");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception_E", "Unsuccessfully written");
        }
    }


}
