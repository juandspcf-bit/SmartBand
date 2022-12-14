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

import com.misawabus.project.heartRate.device.readData.HealthsData;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
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
    private static final String TAG = DeviceConfig.class.getSimpleName();
    private static final Boolean readingStarted = false;
    private static Message msg;
    private static Context context;
    private static DashBoardViewModel dashBoardViewModel;
    private static DeviceViewModel deviceViewModel;
    private static AppCompatActivity activity;
    private static volatile int count = 0;

    static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String s = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    Log.d("DEVICE_INFO", s);
                    dashBoardViewModel.setDeviceInfo(s);
                    //customizeDevice();
                    HealthsData healthsData = new HealthsData(context, activity);
                    healthsData.readOriginData();


                    break;
                case 2:

                    Map<String, Boolean> availableFunctionsMap = new HashMap<>();
                    String substring = s.substring(1, s.length() - 1);
                    String[] split1 = substring.split(", ");
                    Arrays.stream(split1).forEach(keyVal -> {
                        final String[] splitKeyVal = keyVal.split("=");
                        availableFunctionsMap.put(splitKeyVal[0], Boolean.parseBoolean(splitKeyVal[1]));
                    });
                    deviceViewModel.setDeviceFeatures(availableFunctionsMap);
                    dashBoardViewModel.getRealTimeTesterClass().readSportSteps();
                    break;

            }
        }
    };

    public static void enableDevice(Context context, AppCompatActivity activity, HealthsData healthsData) {
        DeviceConfig.context = context;
        dashBoardViewModel = new ViewModelProvider(activity).get(DashBoardViewModel.class);
        deviceViewModel = new ViewModelProvider(activity).get(DeviceViewModel.class);
        DeviceConfig.activity = activity;

        boolean is24Hourmodel = true;
        VPOperateManager.getMangerInstance(context).confirmDevicePwd(writeResponse, new IPwdDataListener() {
                    @Override
                    public void onPwdDataChange(PwdData pwdData) {
                        mHandler.postDelayed(healthsData::readOriginData,5000);

                    }
                },
                new IDeviceFuctionDataListener() {
                    @Override
                    public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                        functionSupport.getOriginProtcolVersion();

                        try {
                            final Map<String, Boolean> supportedFeatures = getSupportedFeatures(functionSupport);
                            int watchDataDay = functionSupport.getWathcDay();
                            dashBoardViewModel.setWatchData(watchDataDay);
                            Log.d(TAG, "onFunctionSupportDataChange: " + functionSupport);
                            sendMsg(supportedFeatures.toString(), 2);

                        } catch (InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }


                        int contactMsgLength = functionSupport.getContactMsgLength();
                        int allMsgLenght = functionSupport.getAllMsgLength();
                        boolean isSleepPrecision = functionSupport.getPrecisionSleep() == SUPPORT;


                    }
                },
                new ISocialMsgDataListener() {
                    @Override
                    public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                        String message = "FunctionSocailMsgData:\n" + socailMsgData.toString();
                        //Logger.t(TAG).i(message);
                        sendMsg(message, 3);
                    }

                    @Override
                    public void onSocialMsgSupportDataChange2(FunctionSocailMsgData functionSocailMsgData) {

                    }
                },
                new ICustomSettingDataListener() {
                    @Override
                    public void OnSettingDataChange(CustomSettingData customSettingData) {
                        Log.d(TAG, "OnSettingDataChange: " + customSettingData);
                        deviceViewModel.setCustomSettingsDataObject(customSettingData);
                    }
                }, "0000", is24Hourmodel);


    }

    public static void disconnectDevice(Context context) {
        VPOperateManager.getMangerInstance(context).disconnectWatch(writeResponse);
    }

    public static Map<String, Boolean> getSupportedFeatures(FunctionDeviceSupportData functionSupport) throws InvocationTargetException, IllegalAccessException {
        Map<String, Boolean> availableFunctionsMap = new HashMap<>();
        Arrays.stream(FunctionDeviceSupportData.class.getDeclaredMethods())
                .forEach(method -> {
                    if (method.getName().contains("get") && method.getReturnType() == EFunctionStatus.class) {
                        try {
                            EFunctionStatus newFeature = (EFunctionStatus) method.invoke(functionSupport);
                            boolean isAvailable = newFeature != null && newFeature.equals(SUPPORT);
                            availableFunctionsMap.put(method.getName().substring(3).toUpperCase(), isAvailable);


                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    int originProtcolVersion = functionSupport.getOriginProtcolVersion();
                    Log.d("originProtcolVersion", String.valueOf(originProtcolVersion));
                    availableFunctionsMap.put("OriginProtcolVersion", originProtcolVersion >= 3);
                    int watchDataDay = functionSupport.getWathcDay();

                });

        return availableFunctionsMap;
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
