package com.misawabus.project.heartRate.device.readData;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.Utils.DBops;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.device.readData.utils.SleepDataUtils;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.veepoo.protocol.listener.data.IAllHealthDataListener;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.TimeData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyIAllHealthDataListener implements IAllHealthDataListener {
    private static final String TAG = MyIAllHealthDataListener.class.getSimpleName();
    private final DashBoardViewModel dashBoardViewModel;
    private final DeviceViewModel deviceViewModel;
    private final AppCompatActivity activity;
    private final List<SleepDataUI> todaySleepDataList = new ArrayList<>();
    private final List<SleepDataUI> yesterdaySleepDataList = new ArrayList<>();
    private final List<SleepDataUI> pastYesterdaySleepDataList = new ArrayList<>();
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private List<OriginData> todayList5Min;
    private List<OriginData> yesterdayList5Min;
    private List<OriginData> pastYesterdayList5Min;

    {

        todayList5Min = Stream.generate(OriginData::new).limit(288)
                .collect(Collectors.toList());

        yesterdayList5Min = Stream.generate((OriginData::new)).limit(288)
                .collect(Collectors.toList());

        pastYesterdayList5Min = Stream.generate((OriginData::new)).limit(288)
                .collect(Collectors.toList());

    }

    public MyIAllHealthDataListener(DashBoardViewModel dashBoardViewModel,
                                    DeviceViewModel deviceViewModel,
                                    AppCompatActivity activity) {
        this.dashBoardViewModel = dashBoardViewModel;
        this.deviceViewModel = deviceViewModel;
        this.activity = activity;
    }

    @Override
    public void onProgress(float v) {

    }

    @Override
    public void onSleepDataChange(String day, SleepData sleepData) {
        Log.d(TAG, "onSleepDataChange: day-" + day + " : data-" + sleepData);

        SleepDataUI sleepDataUIObject =
                SleepDataUtils.getSleepDataUIObject(sleepData, deviceViewModel.getMacAddress());
        sleepDataUIObject.setMacAddress(deviceViewModel.getMacAddress());
        LocalDate sleepLocalDate = DateUtils.getLocalDate(sleepDataUIObject.dateData, "/");
        LocalDate localDateSleepDown = DateUtils.getLocalDateFromVeepooTimeDateObj(sleepDataUIObject.getSleepDown());
        LocalDate correctedDate;
        correctedDate= localDateSleepDown.compareTo(sleepLocalDate)>0?localDateSleepDown:sleepLocalDate;
        Date formattedDate = DateUtils.getFormattedDate(correctedDate.toString(), "-");
        sleepDataUIObject.setDateData(formattedDate);


       if (correctedDate.compareTo(LocalDate.now()) == 0) {
            todaySleepDataList.add(sleepDataUIObject);
        } else if (correctedDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
            yesterdaySleepDataList.add(sleepDataUIObject);
        } else if (correctedDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
            pastYesterdaySleepDataList.add(sleepDataUIObject);
        }
    }

    @Override
    public void onReadSleepComplete() {
        mHandler.post(() -> {

            List<SleepDataUI> sortedToday = todaySleepDataList.stream().sorted((a, b) -> {
                int minutesA = getMinutes(a);
                int minutesB = getMinutes(b);
                return Integer.compare(minutesA, minutesB);
            }).collect(Collectors.toList());

            List<SleepDataUI> sortedYesterday = yesterdaySleepDataList.stream().sorted((a, b) -> {
                int minutesA = getMinutes(a);
                int minutesB = getMinutes(b);
                return Integer.compare(minutesA, minutesB);
            }).collect(Collectors.toList());

            List<SleepDataUI> sortedPastYesterday = pastYesterdaySleepDataList.stream().sorted((a, b) -> {
                int minutesA = getMinutes(a);
                int minutesB = getMinutes(b);
                return Integer.compare(minutesA, minutesB);
            }).collect(Collectors.toList());


            dashBoardViewModel.setTodayUpdateSleepFullData(List.copyOf(sortedToday));
            dashBoardViewModel.setYesterdayUpdateSleepFullData(List.copyOf(sortedYesterday));
            dashBoardViewModel.setPastYesterdayUpdateSleepFullData(List.copyOf(sortedPastYesterday));



            if (sortedToday.size() != 0) {
                for (int i = 0; i < sortedToday.size(); i++) {
                    SleepDataUI sleepDataUI = sortedToday.get(i);
                    sleepDataUI.setIndex(i);
                    DBops.updateSleepData(sortedToday.get(i), deviceViewModel.getMacAddress(), activity, i);
                }
            }

            if (sortedYesterday.size() != 0) {
                for (int i = 0; i < sortedYesterday.size(); i++) {
                    SleepDataUI sleepDataUI = sortedYesterday.get(i);
                    sleepDataUI.setIndex(i);
                    DBops.updateSleepData(sortedYesterday.get(i), deviceViewModel.getMacAddress(), activity, i);
                }
            }
            if (sortedPastYesterday.size() != 0) {
                for (int i = 0; i < sortedPastYesterday.size(); i++) {
                    SleepDataUI sleepDataUI = sortedPastYesterday.get(i);
                    sleepDataUI.setIndex(i);
                    DBops.updateSleepData(sortedPastYesterday.get(i), deviceViewModel.getMacAddress(), activity, i);
                }
            }

            todaySleepDataList.clear();
            yesterdaySleepDataList.clear();
            pastYesterdaySleepDataList.clear();



        });
    }

    @Override
    public void onOringinFiveMinuteDataChange(OriginData originData) {
        String stringDate = originData.getDate();
        Date formattedDate = DateUtils.getFormattedDate(stringDate, "-");
        LocalDate localDate = DateUtils.getLocalDate(formattedDate, "/");

        TimeData timeData = originData.getmTime();
        int interval = IntervalUtils.getInterval5Min(timeData.getHour(),
                timeData.getMinute());

        if (localDate.compareTo(LocalDate.now()) == 0) {
            todayList5Min.set(interval-1, originData);
        } else if (localDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
            yesterdayList5Min.set(interval-1, originData);
        } else if (localDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
            pastYesterdayList5Min.set(interval-1, originData);
        }
    }

    @Override
    public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {

    }

    @Override
    public void onReadOriginComplete() {
        HealthsReadDataController.processOriginDataList(List.copyOf(todayList5Min),
                mHandler,
                dashBoardViewModel,
                deviceViewModel,
                activity,
                DateUtils.getLocalDate(DateUtils.getTodayFormattedDate(), "/").toString());
        HealthsReadDataController.processOriginDataList(List.copyOf(yesterdayList5Min),
                mHandler,
                dashBoardViewModel,
                deviceViewModel,
                activity,
                DateUtils.getLocalDate(DateUtils.getYesterdayFormattedDate(), "/").toString());
        HealthsReadDataController.processOriginDataList(List.copyOf(pastYesterdayList5Min),
                mHandler,
                dashBoardViewModel,
                deviceViewModel,
                activity,
                DateUtils.getLocalDate(DateUtils.getPastYesterdayFormattedDate(), "/").toString());

        todayList5Min = Stream.generate(OriginData::new).limit(288)
                .collect(Collectors.toList());

        yesterdayList5Min = Stream.generate((OriginData::new)).limit(288)
                .collect(Collectors.toList());

        pastYesterdayList5Min = Stream.generate((OriginData::new)).limit(288)
                .collect(Collectors.toList());

        dashBoardViewModel.setIsEnableFeatures(true);
        dashBoardViewModel.setIsTodayFragmentRefreshing(false);
    }


    private int getMinutes(SleepDataUI a) {
        int indexOpenBraceA = a.getSleepUp().indexOf("[");
        String[] split = a.getSleepUp()
                .substring(indexOpenBraceA + 1, a.getSleepUp().length() - 1)
                .split(" ")[1]
                .split(":");

        return Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[0]);
    }

}
