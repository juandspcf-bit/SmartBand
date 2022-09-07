package com.misawabus.project.heartRate.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.misawabus.project.heartRate.datasource.DeviceRepository;
import com.misawabus.project.heartRate.Database.entities.Device;

import java.util.List;
import java.util.Map;

public class DeviceViewModel extends AndroidViewModel {
    public static DeviceRepository deviceRepository;
    private static LiveData<List<Device>> allDeviceData;
    private final MutableLiveData<String> macAddress= new MutableLiveData<>();
    private final MutableLiveData<Map<String, Boolean>> deviceFeatures= new MutableLiveData<>();

    public DeviceViewModel(@NonNull Application application) {
        super(application);
        deviceRepository = new DeviceRepository(application);
        allDeviceData = deviceRepository.getAllDeviceData();
    }

    public static LiveData<List<Device>> getAllDeviceData(){
        return allDeviceData;
    }

    public static void insertSingleDeviceRow(Device device){
        deviceRepository.insertSingleDeviceRow(device);
    }

    public static LiveData<Device> getSingleDeviceRow(String macAddress){
        return deviceRepository.getSingleDeviceRow(macAddress);
    }
    public static void updateSingleDeviceRow(Device device){
        deviceRepository.updateSingleDeviceRow(device);
    }

    public static LiveData<String> getIsUpdatedRowLive(){
        return deviceRepository.getIsUpdatedRowLive();
    }

    public static LiveData<String> getIsInsertedRowLive(){
        return deviceRepository.getIsInsertedRowLive();
    }

    public String getMacAddress() {
        return macAddress.getValue();
    }

    public void setMacAddress(String macAddress) {
        this.macAddress.setValue(macAddress);
    }

    public LiveData<Map<String, Boolean>> getDeviceFeatures() {
        return deviceFeatures;
    }

    public void setDeviceFeatures(Map<String, Boolean> deviceFeatures) {
        this.deviceFeatures.setValue(deviceFeatures);
    }
}
