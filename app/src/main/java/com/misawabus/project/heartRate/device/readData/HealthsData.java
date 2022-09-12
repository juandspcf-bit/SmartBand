package com.misawabus.project.heartRate.device.readData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.misawabus.project.heartRate.Utils.DBops;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.device.readData.utils.SleepDataUtils;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.orhanobut.logger.Logger;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IOriginData3Listener;
import com.veepoo.protocol.listener.data.IOriginDataListener;
import com.veepoo.protocol.listener.data.IOriginProgressListener;
import com.veepoo.protocol.listener.data.ISleepDataListener;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;

import org.apache.logging.log4j.util.PropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HealthsData {

    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final String TAG = HealthsData.class.getSimpleName();
    private final Context context;
    private final DashBoardViewModel dashBoardViewModel;
    private final DeviceViewModel deviceViewModel;
    private final WriteResponse writeResponse = new WriteResponse();
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

    public HealthsData(Context context, AppCompatActivity activity) {
        this.context = context;
        this.dashBoardViewModel = new ViewModelProvider(activity).get(DashBoardViewModel.class);
        this.deviceViewModel = new ViewModelProvider(activity).get(DeviceViewModel.class);
        this.activity = activity;
    }

    public void readOriginData() {
        IOriginProgressListener originDataListener = new IOriginDataListener() {


            @Override
            public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
                String message = "健康数据[5分钟]-读取进度:currentPackage" + currentPackage + ",allPackage=" + allPackage + ",dates=" + date + ",day=" + day;
                Logger.t(TAG).i(message);
            }

            @Override
            public void onReadOriginProgress(float progress) {

            }

            @Override
            public void onReadOriginComplete() {

            }

            @Override
            public void onOringinFiveMinuteDataChange(OriginData originData) {

            }

            @Override
            public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {

            }
        };
        IOriginProgressListener originData3Listener = new IOriginData3Listener() {
            @Override
            public void onOriginFiveMinuteListDataChange(List<OriginData3> originData3List) {

                HealthsReadDataUtils.processOriginData3List(originData3List,
                        databaseWriteExecutor,
                        mHandler,
                        dashBoardViewModel,
                        activity,
                        deviceViewModel);

            }

            @Override
            public void onOriginHalfHourDataChange(OriginHalfHourData originHalfHourData) {

            }

            @Override
            public void onOriginHRVOriginListDataChange(List<HRVOriginData> originHrvDataList) {
                Logger.t(TAG).i("HRV DATA: " + originHrvDataList.toString());
            }

            @Override
            public void onOriginSpo2OriginListDataChange(List<Spo2hOriginData> originSpo2hDataList) {

            }

            @Override
            public void onReadOriginProgress(float progress) {

            }

            @Override
            public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
            }


            @Override
            public void onReadOriginComplete() {
                String message = "sports-complete";
                Logger.t(HealthsReadDataUtils.TAG).i(message);

                mHandler.post(() -> readSleepData());


            }
        };

        IOriginProgressListener originDataListenerX;
        boolean originProtcolVersion = Boolean.TRUE.equals(deviceViewModel.getDeviceFeatures().getValue().get("OriginProtcolVersion"));
        originDataListenerX = originProtcolVersion ? originData3Listener : originDataListener;

        VPOperateManager.getMangerInstance(context).readOriginData(writeResponse, originDataListenerX, dashBoardViewModel.getWatchData());
    }

    public void getSmartWatchDataSingleDay(int day) {
        IOriginProgressListener originDataListener = new IOriginDataListener() {
            @Override
            public void onOringinFiveMinuteDataChange(OriginData originData) {


            }

            @Override
            public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
            }

            @Override
            public void onReadOriginProgress(float progress) {
            }

            @Override
            public void onReadOriginProgressDetail(int date, String dates, int all, int num) {
            }

            @Override
            public void onReadOriginComplete() {
                mHandler.post(() -> dashBoardViewModel.setIsTodayFragmentRefreshing(false));
            }
        };

        IOriginProgressListener originData3Listener = new IOriginData3Listener() {
            @Override
            public void onOriginFiveMinuteListDataChange(List<OriginData3> originData3List) {
                HealthsReadDataUtils.processOriginData3List(originData3List,
                        databaseWriteExecutor,
                        mHandler,
                        dashBoardViewModel,
                        activity,
                        deviceViewModel);

            }

            @Override
            public void onOriginHalfHourDataChange(OriginHalfHourData originHalfHourData) {

            }

            @Override
            public void onOriginHRVOriginListDataChange(List<HRVOriginData> originHrvDataList) {
            }

            @Override
            public void onOriginSpo2OriginListDataChange(List<Spo2hOriginData> originSpo2hDataList) {

            }

            @Override
            public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {
            }

            @Override
            public void onReadOriginProgress(float progress) {
                String message = "健康数据[5分钟]-读取进度:" + progress;
                Logger.t(HealthsReadDataUtils.TAG).i(message);
            }

            @Override
            public void onReadOriginComplete() {
                mHandler.post(() ->readSleepData());
            }
        };
        IOriginProgressListener originDataListenerX;
        boolean originProtcolVersion = Boolean.TRUE.equals(deviceViewModel.getDeviceFeatures().getValue().get("OriginProtcolVersion"));
        originDataListenerX = originProtcolVersion? originData3Listener: originDataListener;
        VPOperateManager.getMangerInstance(context).readOriginDataSingleDay(writeResponse, originDataListenerX, day, 1, dashBoardViewModel.getWatchData());

    }


    public void readSleepData() {
        VPOperateManager.getMangerInstance(context).readSleepData(writeResponse, new ISleepDataListener() {
                    @Override
                    public void onSleepDataChange(String s, SleepData sleepData) {
                        String fullData = SleepDataUtils.processingSleepData(sleepData);
                        if (fullData.isEmpty()) return;

                        SleepDataUI sleepDataUIObject = SleepDataUtils.getSleepDataUIObject(fullData);
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
                    public void onSleepProgress(float progress) {

                    }

                    @Override
                    public void onSleepProgressDetail(String day, int packagenumber) {

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

                            dashBoardViewModel.setIsEnableFeatures(true);

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
                            dashBoardViewModel.setIsTodayFragmentRefreshing(false);

                        });


                    }
                }
                , dashBoardViewModel.getWatchData());
    }

    private int getMinutes(SleepDataUI a) {
        int indexOpenBraceA = a.getSleepUp().indexOf("[");
        String[] split = a.getSleepUp()
                .substring(indexOpenBraceA + 1, a.getSleepUp().length() - 1)
                .split(" ")[1]
                .split(":");

        return Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[0]);
    }

    static class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {

        }
    }


}
