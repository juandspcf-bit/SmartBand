package com.misawabus.project.heartRate.device.readData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.misawabus.project.heartRate.Intervals.IntervalUtils;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.orhanobut.logger.Logger;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IOriginData3Listener;
import com.veepoo.protocol.listener.data.IOriginDataListener;
import com.veepoo.protocol.listener.data.IOriginProgressListener;
import com.veepoo.protocol.listener.data.ITemptureDataListener;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.SleepPrecisionData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.datas.TemptureData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.settings.ReadOriginSetting;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    public HealthsData(Context context, AppCompatActivity activity) {
        this.context = context;
        this.dashBoardViewModel = new ViewModelProvider(activity).get(DashBoardViewModel.class);
        this.deviceViewModel = new ViewModelProvider(activity).get(DeviceViewModel.class);
        this.activity = activity;
    }

    private static void testSleepData(SleepData sleepData) {
        if (sleepData instanceof SleepPrecisionData)
            Log.d(TAG, "testSleepData: It is indeed SleepPrecisionData");
    }

    public void readOriginData() {
        IOriginProgressListener originDataListener = new IOriginDataListener() {


            @Override
            public void onReadOriginProgressDetail(int day, String date, int allPackage, int currentPackage) {

            }

            @Override
            public void onReadOriginProgress(float progress) {

            }

            @Override
            public void onReadOriginComplete() {


                HealthsReadDataController.processOriginDataList(List.copyOf(todayList5Min),
                        mHandler,
                        HealthsData.this.dashBoardViewModel,
                        HealthsData.this.deviceViewModel,
                        HealthsData.this.activity,
                        DateUtils.getLocalDate(DateUtils.getTodayFormattedDate(), "/").toString());
                HealthsReadDataController.processOriginDataList(List.copyOf(yesterdayList5Min),
                        mHandler,
                        HealthsData.this.dashBoardViewModel,
                        HealthsData.this.deviceViewModel,
                        HealthsData.this.activity,
                        DateUtils.getLocalDate(DateUtils.getYesterdayFormattedDate(), "/").toString());
                HealthsReadDataController.processOriginDataList(List.copyOf(pastYesterdayList5Min),
                        mHandler,
                        HealthsData.this.dashBoardViewModel,
                        HealthsData.this.deviceViewModel,
                        HealthsData.this.activity,
                        DateUtils.getLocalDate(DateUtils.getPastYesterdayFormattedDate(), "/").toString());

                todayList5Min = Stream.generate(OriginData::new).limit(288)
                        .collect(Collectors.toList());

                yesterdayList5Min = Stream.generate((OriginData::new)).limit(288)
                        .collect(Collectors.toList());

                pastYesterdayList5Min = Stream.generate((OriginData::new)).limit(288)
                        .collect(Collectors.toList());

                mHandler.post(() -> readSleepData());
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
                    todayList5Min.set(interval - 1, originData);
                } else if (localDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
                    yesterdayList5Min.set(interval - 1, originData);
                } else if (localDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
                    pastYesterdayList5Min.set(interval - 1, originData);
                }
            }

            @Override
            public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {

            }
        };
        IOriginProgressListener originData3Listener = new IOriginData3Listener() {
            @Override
            public void onOriginFiveMinuteListDataChange(List<OriginData3> originData3List) {
                String stringDate = originData3List.get(0).getDate();
                Date formattedDate = DateUtils.getFormattedDate(stringDate, "-");
                LocalDate localDate = DateUtils.getLocalDate(formattedDate, "/");
                if (localDate.compareTo(LocalDate.now()) == 0) {
                    originData3List.forEach(originData3 -> {
                        Log.d(TAG, "onOriginFiveMinuteListDataChange: " + originData3.getmTime() + " : " + originData3.getTempTwo());
                    });

                }
                HealthsReadDataController.processOriginData3List(originData3List,
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
                Logger.t(HealthsReadDataController.TAG).i(message);

                mHandler.post(() -> readSleepData());


            }
        };


        Map<String, Boolean> deviceFeatures = deviceViewModel.getDeviceFeatures().getValue();
        if (deviceFeatures != null) {
            Boolean originProtocolVersion = deviceFeatures.get("OriginProtcolVersion");

            if (originProtocolVersion != null && originProtocolVersion) {
                VPOperateManager.getMangerInstance(context).readOriginData(writeResponse, originData3Listener, dashBoardViewModel.getWatchData());
            } else if (originProtocolVersion != null) {
                //VPOperateManager.getMangerInstance(context).readOriginData(writeResponse, originDataListener, dashBoardViewModel.getWatchData());
                readHealthData();
            }
        }

    }

    public void readSleepData() {
        VPOperateManager
                .getMangerInstance(context)
                .readSleepData(writeResponse,
                        new MyISleepDataListener(dashBoardViewModel, deviceViewModel, activity),
                        dashBoardViewModel.getWatchData());
    }

    public void getSmartWatchDataSingleDay(int day) {
        Log.d(TAG, "getSmartWatchDataSingleDay: ");
        IOriginProgressListener originDataListener = new IOriginDataListener() {
            @Override
            public void onOringinFiveMinuteDataChange(OriginData originData) {
                String stringDate = originData.getDate();
                Date formattedDate = DateUtils.getFormattedDate(stringDate, "-");
                LocalDate localDate = DateUtils.getLocalDate(formattedDate, "/");

                TimeData timeData = originData.getmTime();
                int interval = IntervalUtils.getInterval5Min(timeData.getHour(),
                        timeData.getMinute());

                if (localDate.compareTo(LocalDate.now()) == 0) {
                    todayList5Min.set(interval - 1, originData);
                } else if (localDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
                    yesterdayList5Min.set(interval - 1, originData);
                } else if (localDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
                    pastYesterdayList5Min.set(interval - 1, originData);
                }

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

                HealthsReadDataController.processOriginDataList(List.copyOf(todayList5Min),
                        mHandler,
                        HealthsData.this.dashBoardViewModel,
                        HealthsData.this.deviceViewModel,
                        HealthsData.this.activity,
                        DateUtils.getLocalDate(DateUtils.getTodayFormattedDate(), "/").toString());
                HealthsReadDataController.processOriginDataList(List.copyOf(yesterdayList5Min),
                        mHandler,
                        HealthsData.this.dashBoardViewModel,
                        HealthsData.this.deviceViewModel,
                        HealthsData.this.activity,
                        DateUtils.getLocalDate(DateUtils.getYesterdayFormattedDate(), "/").toString());
                HealthsReadDataController.processOriginDataList(List.copyOf(pastYesterdayList5Min),
                        mHandler,
                        HealthsData.this.dashBoardViewModel,
                        HealthsData.this.deviceViewModel,
                        HealthsData.this.activity,
                        DateUtils.getLocalDate(DateUtils.getPastYesterdayFormattedDate(), "/").toString());

                todayList5Min = Stream.generate(OriginData::new).limit(288)
                        .collect(Collectors.toList());

                yesterdayList5Min = Stream.generate((OriginData::new)).limit(288)
                        .collect(Collectors.toList());

                pastYesterdayList5Min = Stream.generate((OriginData::new)).limit(288)
                        .collect(Collectors.toList());

                //mHandler.post(() -> readSleepData());
                mHandler.post(() -> readSingleDaySleepData(day));


            }
        };

        IOriginProgressListener originData3Listener = new IOriginData3Listener() {
            @Override
            public void onOriginFiveMinuteListDataChange(List<OriginData3> originData3List) {
                HealthsReadDataController.processOriginData3List(originData3List,
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
                Logger.t(HealthsReadDataController.TAG).i(message);
            }

            @Override
            public void onReadOriginComplete() {
                //mHandler.post(() ->readSleepData());
                mHandler.post(() -> readSingleDaySleepData(day));
            }
        };
        Map<String, Boolean> deviceFeatures = deviceViewModel.getDeviceFeatures().getValue();
        if (deviceFeatures != null) {
            Boolean originProtocolVersion = deviceFeatures.get("OriginProtcolVersion");

            if (originProtocolVersion != null && originProtocolVersion) {
                VPOperateManager.getMangerInstance(context).readOriginDataSingleDay(writeResponse, originData3Listener, day, 1, dashBoardViewModel.getWatchData());
            } else if (originProtocolVersion != null) {
                VPOperateManager.getMangerInstance(context).readOriginDataSingleDay(writeResponse, originDataListener, day, 1, dashBoardViewModel.getWatchData());
            }
        }
    }


    public void readTemperature() {
        VPOperateManager.getMangerInstance(context).readTemptureDataBySetting(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        }, new ITemptureDataListener() {
            @Override
            public void onTemptureDataListDataChange(List<TemptureData> list) {
                Log.d(TAG, "onTemptureDataListDataChange: "+list);

                HealthsReadDataController.processTemperatureDataList(list,
                        mHandler,
                        dashBoardViewModel,
                        activity,
                        deviceViewModel);
            }

            @Override
            public void onReadOriginProgressDetail(int i, String s, int i1, int i2) {

            }

            @Override
            public void onReadOriginProgress(float v) {

            }

            @Override
            public void onReadOriginComplete() {
                dashBoardViewModel.setIsEnableFeatures(true);
                dashBoardViewModel.setIsTodayFragmentRefreshing(false);
            }
        }, new ReadOriginSetting(0,
                1,
                false, dashBoardViewModel.getWatchData()
        ));
    }

    public void readSingleDaySleepData(int day) {
        VPOperateManager
                .getMangerInstance(context)
                .readSleepDataSingleDay(writeResponse,
                        new MyISleepDataListener(dashBoardViewModel, deviceViewModel, activity),
                        day,
                        dashBoardViewModel.getWatchData());


    }

    public void readHealthData() {
        VPOperateManager
                .getMangerInstance(context)
                .readAllHealthData(new MyIAllHealthDataListener(dashBoardViewModel,
                                deviceViewModel,
                                activity),
                        dashBoardViewModel.getWatchData());
    }


    static class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {

        }
    }


}
