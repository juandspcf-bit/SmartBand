package com.misawabus.project.heartRate.device.config;

import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT;
import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT_CLOSE;
import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT_OPEN;
import static com.veepoo.protocol.model.enums.EFunctionStatus.UNSUPPORT;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.device.readData.HealthsData;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.orhanobut.logger.Logger;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.ETemperatureUnit;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.veepoo.protocol.shareprence.VpSpGetUtil;


import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeviceConfig {

    private static final WriteResponse writeResponse = new WriteResponse();
    private static Message msg;
    private static final String TAG = "FLOW";
    private static Context context;
    private static DashBoardViewModel dashBoardViewModel;
    private static DeviceViewModel deviceViewModel;
    private static AppCompatActivity activity;
    private static final Boolean readingStarted = false;
    private static volatile int count=0;

    static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String s = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    Log.d("DEVICE_INFO",s);
                    dashBoardViewModel.setDeviceInfo(s);

                    break;
                case 2:

                    Map<String, Boolean> availableFunctionsMap = new HashMap<>();
                    String substring = s.substring(1, s.length() - 1);
                    String[] split1 = substring.split(", ");
                    Arrays.stream(split1).forEach(keyVal ->{
                        final String[] splitKeyVal = keyVal.split("=");
                        availableFunctionsMap.put(splitKeyVal[0], Boolean.parseBoolean(splitKeyVal[1]));
                    });
                    deviceViewModel.setDeviceFeatures(availableFunctionsMap);
                    dashBoardViewModel.getRealTimeTesterClass().readSportSteps();

                    if(count==1 && Boolean.TRUE.equals(availableFunctionsMap.get("OriginProtcolVersion"))) {
                        Log.d("FULL_DATA_Origin", "...........");
                        HealthsData healthsData = new HealthsData(context, activity);
                        healthsData.readOriginData();
                    }else if(count==1){
                        HealthsData healthsData = new HealthsData(context, activity);
                        healthsData.readOriginData();
                    }

                    count++;



                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
            }
        }
    };

    public static void enableDevice(Context context, AppCompatActivity activity) {
        DeviceConfig.context = context;
        dashBoardViewModel = new ViewModelProvider(activity).get(DashBoardViewModel.class);
        deviceViewModel = new ViewModelProvider(activity).get(DeviceViewModel.class);
        DeviceConfig.activity = activity;

        boolean is24Hourmodel = false;
        VPOperateManager.getMangerInstance(context).confirmDevicePwd(writeResponse, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                String message = "PwdData:\n" + pwdData.toString();
                Log.d("PWD", pwdData.toString());
                sendMsg(message, 1);
            }
        }, new IDeviceFuctionDataListener() {
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                functionSupport.getOriginProtcolVersion();

                try {
                    final Map<String, Boolean> supportedFeatures = getSupportedFeatures(functionSupport);
                    int watchDataDay = functionSupport.getWathcDay();
                    dashBoardViewModel.setWatchData(watchDataDay);
                    sendMsg(supportedFeatures.toString(), 2);

                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }




                int contactMsgLength = functionSupport.getContactMsgLength();
                int allMsgLenght = functionSupport.getAllMsgLength();
                boolean isSleepPrecision = functionSupport.getPrecisionSleep() == SUPPORT;


            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                String message = "FunctionSocailMsgData:\n" + socailMsgData.toString();
                Logger.t(TAG).i(message);
                sendMsg(message, 3);
            }

            @Override
            public void onSocialMsgSupportDataChange2(FunctionSocailMsgData functionSocailMsgData) {

            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                String message = "FunctionCustomSettingData:\n" + customSettingData.toString();
                Logger.t(TAG).i(message);
                sendMsg(message, 4);
            }
        }, "0000", is24Hourmodel);


    }

    public static void disconnectDevice(Context context){
        VPOperateManager.getMangerInstance(context).disconnectWatch(writeResponse);
    }

    public static Map<String, Boolean> getSupportedFeatures(FunctionDeviceSupportData functionSupport) throws InvocationTargetException, IllegalAccessException {
        Map<String, Boolean> availableFunctionsMap = new HashMap<>();
        Arrays.stream(FunctionDeviceSupportData.class.getDeclaredMethods())
                .forEach(method -> {
                    if(method.getName().contains("get")  && method.getReturnType()==EFunctionStatus.class){
                        try {
                            EFunctionStatus newFeature = (EFunctionStatus) method.invoke(functionSupport);
                            boolean isAvailable = newFeature != null && newFeature.equals(SUPPORT);
                            availableFunctionsMap.put(method.getName().substring(3).toUpperCase(), isAvailable);


                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    int originProtcolVersion = functionSupport.getOriginProtcolVersion();
                    Log.d("originProtcolVersion", String.valueOf(originProtcolVersion));
                    availableFunctionsMap.put("OriginProtcolVersion", originProtcolVersion>=3);
                    int watchDataDay = functionSupport.getWathcDay();

                });

        return availableFunctionsMap;
    }

    public static void readSettings(){
        VPOperateManager.getMangerInstance(context).readCustomSetting(writeResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {

                String message = "个性化状态-公英制/时制(12/24)/5分钟测量开关(心率/血压)-读取:\n" + customSettingData.toString();
                customSettingData.setAutoHrv(SUPPORT_OPEN);
                customSettingData.setPpg(SUPPORT_OPEN);

                /*VPOperateManager.getMangerInstance(context).changeCustomSetting(writeResponse, new ICustomSettingDataListener() {
                    @Override
                    public void OnSettingDataChange(CustomSettingData customSettingData) {

                    }
                }, customSettingData);*/
                Logger.t(TAG).i(message);
                sendMsg(message, 1);
            }
        });
    }


    public static void customizeDevice(){
        boolean isHaveMetricSystem = true;
        boolean isMetric = true;
        boolean is24Hour = true;
        boolean isOpenAutoHeartDetect = true;
        boolean isOpenAutoBpDetect = true;
        boolean isCelsius = true;
        EFunctionStatus isOpenSportRemain = SUPPORT_OPEN;
        EFunctionStatus isOpenVoiceBpHeart = SUPPORT_OPEN;
        EFunctionStatus isOpenFindPhoneUI = SUPPORT_OPEN;
        EFunctionStatus isOpenStopWatch = SUPPORT_OPEN;
        EFunctionStatus isOpenSpo2hLowRemind = SUPPORT_OPEN;
        EFunctionStatus isOpenWearDetectSkin = SUPPORT_OPEN;
        EFunctionStatus isOpenAutoInCall = SUPPORT_OPEN;
        EFunctionStatus isOpenAutoHRV = SUPPORT_OPEN;
        EFunctionStatus isOpenDisconnectRemind = SUPPORT_OPEN;
        EFunctionStatus isAutoTemperatureDetect = SUPPORT_OPEN;
        boolean isSupportSettingsTemperatureUnit = VpSpGetUtil.getVpSpVariInstance(context).isSupportSettingsTemperatureUnit();//是否支持温度单位设置
        boolean isSupportSleep = VpSpGetUtil.getVpSpVariInstance(context).isSupportPreciseSleep();//是否支持精准睡眠

        boolean isCanReadTempture = VpSpGetUtil.getVpSpVariInstance(context).isSupportReadTempture();//是否支持读取温度
        boolean isCanDetectTempByApp = VpSpGetUtil.getVpSpVariInstance(context).isSupportCheckTemptureByApp();//是否可以通过app监测体温


        Logger.t(TAG).i("是否可以读取体温：" + isCanReadTempture + " 是否可以通过app自动检测体温");

        CustomSetting customSetting = new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect,
                isOpenAutoBpDetect, isOpenSportRemain, isOpenVoiceBpHeart, isOpenFindPhoneUI, isOpenStopWatch,
                isOpenSpo2hLowRemind, isOpenWearDetectSkin, isOpenAutoInCall, isOpenAutoHRV, isOpenDisconnectRemind
        );
        customSetting.setIsOpenLongClickLockScreen(SUPPORT_CLOSE);
        if (isSupportSettingsTemperatureUnit) {
            customSetting.setTemperatureUnit(VpSpGetUtil.getVpSpVariInstance(context).getTemperatureUnit()
                    == ETemperatureUnit.CELSIUS ? ETemperatureUnit.FAHRENHEIT : ETemperatureUnit.CELSIUS);
        } else {
            customSetting.setTemperatureUnit(ETemperatureUnit.NONE);
        }
        if (isCanDetectTempByApp) {
            boolean isOpenTempDetect = VpSpGetUtil.getVpSpVariInstance(context).isOpenTemperatureDetectByApp();
            customSetting.setIsOpenAutoTemperatureDetect(isOpenTempDetect ? SUPPORT_CLOSE : SUPPORT_OPEN);
        } else {
            customSetting.setIsOpenAutoTemperatureDetect(UNSUPPORT);
        }

        VPOperateManager.getMangerInstance(context).changeCustomSetting(writeResponse, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                String message = "settings changed:\n" + customSettingData.toString();
                Logger.t(TAG).i(message);
                sendMsg(message, 1);
            }
        }, customSetting);
    }




    private static void sendMsg(String message, int what) {
        msg = Message.obtain();
        msg.what = what;
        msg.obj = message;
        mHandler.sendMessage(msg);
    }


    static class WriteResponse implements IBleWriteResponse {

        @Override
        public void onResponse(int code) {
            Log.d("RESPONSE", String.valueOf(code));
        }
    }

}
