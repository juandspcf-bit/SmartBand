package com.misawabus.project.heartRate.device.readData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.device.readData.utils.SleepDataUtils;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.orhanobut.logger.Logger;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IOriginData3Listener;
import com.veepoo.protocol.listener.data.IOriginDataListener;
import com.veepoo.protocol.listener.data.IOriginProgressListener;
import com.veepoo.protocol.model.datas.HRVOriginData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginData3;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.datas.Spo2hOriginData;
import com.veepoo.protocol.model.enums.ESex;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceReadData {
    private final Context context;
    private final DashBoardViewModel dashBoardViewModel;
    private final DeviceViewModel deviceViewModel;
    private final AppCompatActivity activity;

    private final WriteResponse writeResponse = new WriteResponse();
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
                case 1:
                    dashBoardViewModel.setRealTimeSportsData(fullData);
                    break;
                case 8:
                    dashBoardViewModel.setRealTimeHearRateData(fullData);
                    break;
                case 9:
                    dashBoardViewModel.setRealTimeBPData(fullData);
                    break;
                case 10:
                    String[] split4 = fullData.split("---");
                    SleepDataUI sleepDataUIObject = SleepDataUtils.getSleepDataUIObject(split4[0]);
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
                case 11:
                    if (fullData.equals("enable")) {
                        dashBoardViewModel.setIsEnableFeatures(true);
                    }
                    break;
                case 12:
                    dashBoardViewModel.setIsTodayFragmentRefreshing(false);
                    break;
                case 13:
/*                    String[] split13 = fullData.split("---");
                    String[] sportsSplit13 = split13[split13.length - 1].split("-");
                    LocalDate localDate13 = LocalDate.of(Integer.parseInt(sportsSplit13[0]), Integer.parseInt(sportsSplit13[1]), Integer.parseInt(sportsSplit13[2]));
                    if (localDate13.compareTo(LocalDate.now()) == 0) {
                        dashBoardViewModel.setTodayUpdateSportsFullData(fullData);
                    }*/
                    break;
            }
        }
    };


    public DeviceReadData(Context context, AppCompatActivity activity) {
        this.context = context;
        this.dashBoardViewModel = new ViewModelProvider(activity).get(DashBoardViewModel.class);
        this.deviceViewModel = new ViewModelProvider(activity).get(DeviceViewModel.class);
        this.activity = activity;
    }



    public void synchronizePersonalData(ESex sex, int height, int weight, int age, int stepAim){
        VPOperateManager.getMangerInstance(context).syncPersonInfo(writeResponse, EOprateStauts -> {
            String message = "同步个人信息:\n" + EOprateStauts.toString();
            Log.d("OPPP", message);

        }, new PersonInfoData(sex, height, weight, age, stepAim));
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
                sendMsg("stopRefreshing", 12);
            }
        };

        IOriginProgressListener originData3Listener = new IOriginData3Listener() {
            @Override
            public void onOriginFiveMinuteListDataChange(List<OriginData3> originData3List) {

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
                sendMsg("stopRefreshing", 12);
            }
        };
        IOriginProgressListener originDataListenerX;
        boolean originProtcolVersion = Boolean.TRUE.equals(deviceViewModel.getDeviceFeatures().getValue().get("OriginProtcolVersion"));
        originDataListenerX = originProtcolVersion? originData3Listener: originDataListener;
        VPOperateManager.getMangerInstance(context).readOriginDataSingleDay(writeResponse, originDataListenerX, day, 1, dashBoardViewModel.getWatchData());

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
