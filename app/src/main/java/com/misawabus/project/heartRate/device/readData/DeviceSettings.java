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
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.enums.ESex;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeviceSettings {
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


    public DeviceSettings(Context context, AppCompatActivity activity) {
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


    static class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {

        }
    }

}