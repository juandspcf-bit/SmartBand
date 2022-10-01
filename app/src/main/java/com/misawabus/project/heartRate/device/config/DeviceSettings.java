package com.misawabus.project.heartRate.device.config;

import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT_OPEN;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;
import com.orhanobut.logger.Logger;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.enums.ESex;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class DeviceSettings {
    private static final String TAG = DeviceSettings.class.getSimpleName();
    private final Context context;
    private final DashBoardViewModel dashBoardViewModel;
    private final DeviceViewModel deviceViewModel;
    private final AppCompatActivity activity;
    private CustomSetting customSetting;

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

    public void readDeviceSettings(){
        VPOperateManager.getMangerInstance(context).readCustomSetting(writeResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {

                String message = "个性化状态-公英制/时制(12/24)/5分钟测量开关(心率/血压)-读取:\n" + customSettingData.toString();
                //customSettingData.setAutoHrv(SUPPORT_OPEN);

                Log.d(TAG, "OnSettingDataChange: " + customSettingData);
                customSetting= new CustomSetting(false, false, false, false, false);

                customSetting.setIs24Hour(customSettingData.isIs24Hour());
                customSetting.setMetricSystem(customSettingData.getMetricSystem()==SUPPORT_OPEN);
                customSetting.setOpenAutoHeartDetect(customSettingData.getAutoHeartDetect()==SUPPORT_OPEN);
                customSetting.setOpenAutoBpDetect(customSettingData.getAutoBpDetect()== SUPPORT_OPEN);
                customSetting.setIsOpenSpo2hLowRemind(customSettingData.getLowSpo2hRemain());
                customSetting.setIsOpenDisconnectRemind(customSettingData.getDisconnectRemind());
                customSetting.setIsOpenPPG(customSettingData.getPpg());
                customSetting.setIsOpenAutoTemperatureDetect(customSettingData.getAutoTemperatureDetect());
                customSetting.setTemperatureUnit(customSettingData.getTemperatureUnit());
                customSetting.setIsOpenVoiceBpHeart(SUPPORT_OPEN);
                customSetting.setIsOpenAutoHRV(SUPPORT_OPEN);

                setDeviceSettings(customSetting);

            }
        });
    }


    public void setDeviceSettings(CustomSetting customSetting){
        VPOperateManager.getMangerInstance(context).changeCustomSetting(writeResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                String message = "个性化状态-公英制/时制(12/24)/5分钟测量开关(心率/血压)-设置:\n" + customSettingData.toString();
                Logger.t(TAG).i(message);
            }
        }, customSetting);
    }

    static class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {

        }
    }


    public void disconnectDevice(Consumer<Integer> response){
        VPOperateManager.getMangerInstance(context).disconnectWatch(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {
                response.accept(i);
            }
        });
    }

}
