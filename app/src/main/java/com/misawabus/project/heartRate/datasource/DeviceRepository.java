package com.misawabus.project.heartRate.datasource;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.misawabus.project.heartRate.Database.entities.Device;
import com.misawabus.project.heartRate.Database.rommdb.AppRoomDataBase;
import com.misawabus.project.heartRate.Database.rommdb.DeviceDao;

import java.util.List;

public class DeviceRepository {
    private static  DeviceDao deviceDao;
    private final MutableLiveData<String> isUpdatedRowLive =  new MutableLiveData<>();
    private final MutableLiveData<String> isInsertedRowLive =  new MutableLiveData<>();
    private final LiveData<List<Device>> allDeviceData;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override public void handleMessage(Message msg) {
            Log.d("STATUS_RESULT", "successfully");
            Bundle bundle = msg.getData();
            String resultOP = bundle.getString("RESULT_OPERATION");
            //Log.d("STATUS_RESULT", "successfully  " + isInserted);
            if(resultOP.contains("UPDATE")){
                isUpdatedRowLive.setValue(resultOP);
            }else if(resultOP.contains("INSERT")){
                isInsertedRowLive.setValue(resultOP);
            }

        }
    };

    public DeviceRepository(Application application){
        AppRoomDataBase db = AppRoomDataBase.getDatabase(application);
        deviceDao = db.getDeviceDao();
        allDeviceData = deviceDao.getAllDeviceData();
    }

    public LiveData<List<Device>> getAllDeviceData(){
        return allDeviceData;
    }

    public LiveData<Device> getSingleDeviceRow(String macAddress){
        return deviceDao.getSingleDeviceRow(macAddress);
    }
    public void insertSingleDeviceRow(Device device){
        AppRoomDataBase.databaseWriteExecutor.execute(() -> {
            deviceDao.insertSingleDeviceRow(device);
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("isInserted", "inserted");
            bundle.putString("RESULT_OPERATION", "INSERT---inserted");
            msg.setData(bundle);
            handler.sendMessage(msg);
        });
    }

    public void updateSingleDeviceRow(Device device){
        AppRoomDataBase.databaseWriteExecutor.execute(()-> {
            int updated = deviceDao.updateSingleDeviceRow(device);
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("RESULT_OPERATION", updated==1?"UPDATE---updated":"UPDATE---fail");
            msg.setData(bundle);
            handler.sendMessage(msg);
        });
    }

    public MutableLiveData<String> getIsUpdatedRowLive() {
        return isUpdatedRowLive;
    }

    public MutableLiveData<String> getIsInsertedRowLive() {
        return isInsertedRowLive;
    }

}
