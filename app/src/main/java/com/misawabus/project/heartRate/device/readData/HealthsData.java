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
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.device.entities.BloodPressureDataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.DataFiveMinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.HeartRateData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.Spo2HData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.entities.SportsData5MinAvgDataContainer;
import com.misawabus.project.heartRate.device.readData.utils.SleepDataUtils;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.orhanobut.logger.Logger;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAllHealthDataListener;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HealthsData {

    private static final String TAG = HealthsData.class.getSimpleName();
    private final Context context;
    private final DashBoardViewModel dashBoardViewModel;
    private final DeviceViewModel deviceViewModel;
    private final WriteResponse writeResponse = new WriteResponse();
    private final AppCompatActivity activity;


    private final int countDays = 0;
    private final List<SleepDataUI> listToday = new ArrayList<>();
    private final List<SleepDataUI> listYesterday = new ArrayList<>();
    private final List<SleepDataUI> listPastYesterday = new ArrayList<>();
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    Message msg;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String fullData = msg.obj.toString();


            switch (msg.what) {
                case 0:

                    SleepDataUI sleepDataUIObject = SleepDataUtils.getSleepDataUIObject(fullData);
                    LocalDate sleepLocalDate = DateUtils.getLocalDate(sleepDataUIObject.dateData, "/");

                    if (sleepLocalDate.compareTo(LocalDate.now()) == 0) {
                        listToday.add(sleepDataUIObject);
                        dashBoardViewModel.setTodayUpdateSleepFullData(listToday);
                    } else if (sleepLocalDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
                        listYesterday.add(sleepDataUIObject);
                        dashBoardViewModel.setYesterdayUpdateSleepFullData(listYesterday);
                    } else if (sleepLocalDate.compareTo(LocalDate.now().minusDays(2)) == 0) {
                        listPastYesterday.add(sleepDataUIObject);
                        dashBoardViewModel.setPastYesterdayUpdateSleepFullData(listPastYesterday);
                    }
                    break;
                case 2:
                    if (fullData.equals("enable")) {
                        dashBoardViewModel.setIsEnableFeatures(true);
                    }
                    break;
                case 4:
                    readSleepData();
                    break;
            }
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

                databaseWriteExecutor.execute(() -> {
                    DataFiveMinAvgDataContainer sportsDataFiveMinAvgDataContainer = HealthsReadDataUtils
                            .computeSportsDataFiveMinAVR(originData3List,
                                    HealthsReadDataUtils.functionToSetFieldsInSports(),
                                    new SportsData5MinAvgDataContainer());
                    DataFiveMinAvgDataContainer heartRateDataFiveMinAvgDataContainer = HealthsReadDataUtils
                            .computeHearRateDataFiveMinAVR(originData3List,
                                    HealthsReadDataUtils.functionToSetFieldsInPpgs(),
                                    new HeartRateData5MinAvgDataContainer());
                    DataFiveMinAvgDataContainer bloodPressureDataFiveMinAvgDataContainer = HealthsReadDataUtils
                            .computeBloodPressureDataFiveMinAVR(originData3List,
                                    HealthsReadDataUtils.functionToSetFieldsInBloodPressure(),
                                    new BloodPressureDataFiveMinAvgDataContainer());
                    DataFiveMinAvgDataContainer spo2HData5MinAvgAllIntervals = HealthsReadDataUtils
                            .computeSpo2hDataFiveMinAVR(originData3List,
                                    HealthsReadDataUtils.functionToSetFieldsInSop2(),
                                    new Spo2HData5MinAvgDataContainer());

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
                            .put(Spo2HData5MinAvgDataContainer.class.getSimpleName(),
                                    spo2HData5MinAvgAllIntervals);

                    mHandler.post(()-> {

                        String stringDate = heartRateDataFiveMinAvgDataContainer.getStringDate();
                        Date formattedDate = DateUtils.getFormattedDate(stringDate, "-");
                        LocalDate localDate = DateUtils.getLocalDate(formattedDate, "/");
                        if (localDate.compareTo(LocalDate.now()) == 0) {
                            dashBoardViewModel.setTodayFullData5MinAvgAllIntervals(dataFiveMinAVGAllIntervalsMap);
                        }else if (localDate.compareTo(LocalDate.now().minusDays(1)) == 0) {
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


                    });
                });

            }

            @Override
            public void onOriginHalfHourDataChange(OriginHalfHourData originHalfHourData) {

            }

            @Override
            public void onOriginHRVOriginListDataChange(List<HRVOriginData> originHrvDataList) {

                //Logger.t(TAG).i("HRV DATA: " + originHrvDataList.toString());
            }

            @Override
            public void onOriginSpo2OriginListDataChange(List<Spo2hOriginData> originSpo2hDataList) {
                //Logger.t(TAG).i(originSpo2hDataList.toString());
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
                sendMsg(message, 4);
            }
        };

        IOriginProgressListener originDataListenerX;
        boolean originProtcolVersion = Boolean.TRUE.equals(deviceViewModel.getDeviceFeatures().getValue().get("OriginProtcolVersion"));
        originDataListenerX = originProtcolVersion? originData3Listener: originDataListener;

        VPOperateManager.getMangerInstance(context).readOriginData(writeResponse, originDataListenerX, dashBoardViewModel.getWatchData());
    }


    public void readSleepData() {
        VPOperateManager.getMangerInstance(context).readSleepData(writeResponse, new ISleepDataListener() {
                    @Override
                    public void onSleepDataChange(String s, SleepData sleepData) {
                        String fullData = SleepDataUtils.processingSleepData(sleepData, countDays);
                        if(fullData.isEmpty()) return;
                        sendMsg(fullData, 0);

                    }

                    @Override
                    public void onSleepProgress(float progress) {

                    }

                    @Override
                    public void onSleepProgressDetail(String day, int packagenumber) {

                    }

                    @Override
                    public void onReadSleepComplete() {
                        String message = "睡眠数据-读取结束";
                        Logger.t(HealthsReadDataUtils.TAG).i(message);
                        sendMsg("enable", 2);
                    }
                }
                ,dashBoardViewModel.getWatchData());
    }



    public static void readOOO(Context context, DashBoardViewModel dashBoardViewModel){
        VPOperateManager.getMangerInstance(context).readAllHealthData(new IAllHealthDataListener() {
            @Override
            public void onProgress(float v) {
                Log.d(TAG, "onOringinFiveMinuteDataChange: " + v);
            }

            @Override
            public void onSleepDataChange(String s, SleepData sleepData) {
                Log.d(TAG, "onOringinFiveMinuteDataChange: " + sleepData);
            }

            @Override
            public void onReadSleepComplete() {

            }

            @Override
            public void onOringinFiveMinuteDataChange(OriginData originData) {
                Log.d(TAG, "onOringinFiveMinuteDataChange: " + originData);

            }

            @Override
            public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                Log.d(TAG, "onOringinFiveMinuteDataChange: " + originHalfHourData);
            }

            @Override
            public void onReadOriginComplete() {

            }
        }, dashBoardViewModel.getWatchData());
    }





    private void sendMsg(String message, int what) {
        msg = Message.obtain();
        msg.what = what;
        msg.obj = message;
        mHandler.sendMessage(msg);
    }

    static class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {

        }
    }


}
