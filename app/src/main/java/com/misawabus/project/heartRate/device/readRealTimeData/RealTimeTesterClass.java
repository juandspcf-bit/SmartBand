package com.misawabus.project.heartRate.device.readRealTimeData;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.inuker.bluetooth.library.Code;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.misawabus.project.heartRate.viewModels.HeartRateViewModel;
import com.orhanobut.logger.Logger;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBPDetectDataListener;
import com.veepoo.protocol.listener.data.IBPSettingDataListener;
import com.veepoo.protocol.listener.data.IHeartDataListener;
import com.veepoo.protocol.listener.data.IHeartWaringDataListener;
import com.veepoo.protocol.listener.data.IRRIntervalProgressListener;
import com.veepoo.protocol.listener.data.ISportDataListener;
import com.veepoo.protocol.listener.data.ITemptureDataListener;
import com.veepoo.protocol.listener.data.ITemptureDetectDataListener;
import com.veepoo.protocol.model.DayState;
import com.veepoo.protocol.model.datas.BpData;
import com.veepoo.protocol.model.datas.BpSettingData;
import com.veepoo.protocol.model.datas.HeartData;
import com.veepoo.protocol.model.datas.HeartWaringData;
import com.veepoo.protocol.model.datas.RRIntervalData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.datas.TemptureData;
import com.veepoo.protocol.model.datas.TemptureDetectData;
import com.veepoo.protocol.model.enums.EBPDetectModel;
import com.veepoo.protocol.model.settings.BpSetting;
import com.veepoo.protocol.model.settings.HeartWaringSetting;
import com.veepoo.protocol.model.settings.ReadOriginSetting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RealTimeTesterClass {
    private static final String TAG = RealTimeTesterClass.class.getSimpleName();
    private final Context context;
    private final DashBoardViewModel dashBoardViewModel;
    private final DeviceViewModel deviceViewModel;
    private final WriteResponse writeResponse = new WriteResponse();

    private final Application application;
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
                case 2:
                    dashBoardViewModel.setRealTimeHearRateData(fullData);
                    break;
                case 3:
                    dashBoardViewModel.setRealTimeBPData(fullData);
                    break;


            }
        }
    };
    private HeartRateViewModel heartRateViewModel;


    public RealTimeTesterClass(Context context, AppCompatActivity activity, Application application) {
        this.context = context;
        this.application = application;
        dashBoardViewModel = new ViewModelProvider(activity).get(DashBoardViewModel.class);
        deviceViewModel = new ViewModelProvider(activity).get(DeviceViewModel.class);
    }


    public void readSportSteps() {
        VPOperateManager.getMangerInstance(context).readSportStep(writeResponse, new ISportDataListener() {
            @Override
            public void onSportDataChange(SportData sportData) {
                double calories = sportData.getKcal();
                double distance = sportData.getDis();
                int step = sportData.getStep();
                Log.d("TRIAXIAL", sportData.getTriaxialX() + " " + sportData.getTriaxialY() + " " + sportData.getTriaxialZ() + " ");
                String data = step + "---" + String.format("%.1f", distance) + "---" + String.format("%.1f", calories);
                sendMsg(data, 1);
            }
        });
    }

    public void readHeartRate() {
        VPOperateManager.getMangerInstance(context).startDetectHeart(writeResponse, new IHeartDataListener() {
            @Override
            public void onDataChange(HeartData heart) {
                Log.d("onDataChange(HeartData heart)", heart.toString());
                int data = heart.getData();
                String message = String.valueOf(data);
                sendMsg(message, 2);
            }
        });

    }

    public void stopReadHeartRate() {
        VPOperateManager.getMangerInstance(context).stopDetectHeart(writeResponse);
    }

    public void startBloodPressure() {
        VPOperateManager.getMangerInstance(context).startDetectBP(writeResponse, new IBPDetectDataListener() {
            @Override
            public void onDataChange(BpData bpData) {
                int highPressure = bpData.getHighPressure();
                int lowPressure = bpData.getLowPressure();
                int progress = bpData.getProgress();
                String message = highPressure + "---" + lowPressure + "---" + progress;
                Log.d("DATA_BP", message);
                sendMsg(message, 3);
            }
        }, EBPDetectModel.DETECT_MODEL_PUBLIC);
    }

    public void stopBloodPressure() {
        VPOperateManager.getMangerInstance(context).stopDetectBP(writeResponse, EBPDetectModel.DETECT_MODEL_PUBLIC);
    }

    public void configBloodPressureMeasurement() {
        boolean isOpenPrivateModel = false;
        boolean isAngioAdjuste = true;
        BpSetting bpSetting = new BpSetting(isOpenPrivateModel, 111, 88);

        //是否开启动态血压调整模式，功能标志位在密码验证的返回
        bpSetting.setAngioAdjuste(isAngioAdjuste);
        VPOperateManager.getMangerInstance(context).settingDetectBP(writeResponse, new IBPSettingDataListener() {
            @Override
            public void onDataChange(BpSettingData bpSettingData) {
                startBloodPressure();
                String message = "BpSettingData:\n" + bpSettingData.toString();
                Logger.t(TAG).i(message);
//                    sendMsg(message, 1);
            }
        }, bpSetting);
    }


    public void startTemperatureDetection(Consumer<Map<String, Double>> consumer) {
        VPOperateManager.getMangerInstance(context).startDetectTempture(writeResponse, new ITemptureDetectDataListener() {
            @Override
            public void onDataChange(TemptureDetectData temptureDetectData) {
                Map<String, Double> map = new HashMap<>();
                map.put("BodyTemp", (double) temptureDetectData.getTempture());
                map.put("SkinTemp", (double) temptureDetectData.getTemptureBase());
                map.put("Progress", (double) temptureDetectData.getProgress());
                consumer.accept(map);

            }
        });

    }

    public void stopTemperatureDetection() {
        VPOperateManager.getMangerInstance(context).stopDetectTempture(writeResponse, new ITemptureDetectDataListener() {
            @Override
            public void onDataChange(TemptureDetectData temptureDetectData) {
                String message = "stopDetectTempture temptureDetectData:\n" + temptureDetectData.toString();
                Logger.t(TAG).i(message);
            }
        });

    }

    public void setHeartRateAlert(int higValue, int lowValue, boolean isChecked, Consumer<HeartWaringData> code) {
        VPOperateManager.getMangerInstance(context).settingHeartWarning(writeResponse, new IHeartWaringDataListener() {
            @Override
            public void onHeartWaringDataChange(HeartWaringData heartWaringData) {
                String message = "心率报警-打开:\n" + heartWaringData.toString();
                Logger.t(TAG).i(message);
                code.accept(heartWaringData);
            }
        }, new HeartWaringSetting(higValue, lowValue, isChecked));
    }

    public void readHeartRateAlertSettings(@NonNull Consumer<Integer> receivingCode, @NonNull Consumer<HeartWaringData> receivingSettings) {
        VPOperateManager.getMangerInstance(context).readHeartWarning(receivingCode::accept, receivingSettings::accept);
    }

    public void readReadRRIntervalByDay() {
        VPOperateManager.getMangerInstance(context).readRRIntervalByDay(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {
                if (i != Code.REQUEST_SUCCESS)
                    Log.d(TAG, "onReadRRIntervalProgressChanged: " + "no success");
                ;
            }
        }, new IRRIntervalProgressListener() {
            @Override
            public void onReadRRIntervalProgressChanged(float v, RRIntervalData rrIntervalData) {
                Log.d(TAG, "onReadRRIntervalProgressChanged: " + rrIntervalData.toString());
            }

            @Override
            public void onReadRRIntervalComplete(DayState dayState, List<RRIntervalData> list) {
                Log.d(TAG, "onReadRRIntervalProgressChanged: " + list);
            }
        }, DayState.YESTERDAY, 1440);
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

