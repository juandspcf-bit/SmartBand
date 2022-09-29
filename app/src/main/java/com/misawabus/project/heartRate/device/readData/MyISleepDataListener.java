package com.misawabus.project.heartRate.device.readData;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.Utils.DBops;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.device.readData.utils.SleepDataUtils;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.veepoo.protocol.listener.data.ISleepDataListener;
import com.veepoo.protocol.model.datas.SleepData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyISleepDataListener implements ISleepDataListener {

    private static final String TAG = MyISleepDataListener.class.getSimpleName();
    private final List<SleepDataUI> todaySleepDataList = new ArrayList<>();
    private final List<SleepDataUI> yesterdaySleepDataList = new ArrayList<>();
    private final List<SleepDataUI> pastYesterdaySleepDataList = new ArrayList<>();
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private final DashBoardViewModel dashBoardViewModel;
    private final DeviceViewModel deviceViewModel;
    private final AppCompatActivity activity;

    public MyISleepDataListener(DashBoardViewModel dashBoardViewModel,
                                DeviceViewModel deviceViewModel,
                                AppCompatActivity activity) {
        this.dashBoardViewModel = dashBoardViewModel;
        this.deviceViewModel = deviceViewModel;
        this.activity = activity;
    }

    @Override
    public void onSleepDataChange(String day, SleepData sleepData) {
        Log.d(TAG, "onSleepDataChange: day-" + day + " : data-" + sleepData);
/*        String fullData = SleepDataUtils.processingSleepData(sleepData);
        if (fullData.isEmpty()) return;*/

        SleepDataUI sleepDataUIObject =
                SleepDataUtils.getSleepDataUIObject(sleepData, deviceViewModel.getMacAddress());
        sleepDataUIObject.setMacAddress(deviceViewModel.getMacAddress());
        LocalDate sleepLocalDate = DateUtils.getLocalDate(sleepDataUIObject.dateData, "/");

        if (sleepLocalDate.compareTo(LocalDate.now()) == 0) {
            todaySleepDataList.add(sleepDataUIObject);
        } else if (sleepLocalDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
            yesterdaySleepDataList.add(sleepDataUIObject);
        } else if (sleepLocalDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
            pastYesterdaySleepDataList.add(sleepDataUIObject);
        }
    }

    @Override
    public void onSleepProgress(float v) {

    }

    @Override
    public void onSleepProgressDetail(String s, int i) {

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

            dashBoardViewModel.setIsEnableFeatures(true);
            dashBoardViewModel.setIsTodayFragmentRefreshing(false);

        });

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
