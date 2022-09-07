package com.misawabus.project.heartRate.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.datasource.HeartRateRepository;
import com.misawabus.project.heartRate.Database.entities.HeartRate;

import java.util.Date;
import java.util.List;

public class HeartRateViewModel extends AndroidViewModel {
    public static HeartRateRepository heartRateRepository;
    private static LiveData<List<HeartRate>> allHeartRateData;
    private static LiveData<Integer> countHeartRateData;

    public HeartRateViewModel(@NonNull Application application) {
        super(application);
        heartRateRepository = new HeartRateRepository(application);
        //allHeartRateData = heartRateRepository.getAllHeartRateData();
        //countHeartRateData = heartRateRepository.getCountHeartRateData();
    }

    public static LiveData<List<HeartRate>> getAllHeartRateData(){
        return allHeartRateData;
    }

    public static LiveData<Integer> getCountHeartRateData(){
        return countHeartRateData;
    }

    public static void insertSingleHeartRateRow(HeartRate heartRate){
        heartRateRepository.insertSingleHeartRateRow(heartRate);
    }

    public static LiveData<HeartRate> getSingleHeartRateRow(Date date, String macAddress){
        return heartRateRepository.getSingleHeartRateRow(date, macAddress);
    }
    public static LiveData<HeartRate> getSingleHeartRateRowForU(Date date, String macAddress, IdTypeDataTable idTable){
        return heartRateRepository.getSingleHeartRateRowForU(date, macAddress, idTable);
    }

    public static LiveData<HeartRate> getSinglePDayHeartRateRow(Date date, String macAddress){
        return heartRateRepository.getSinglePDayHeartRateRow(date, macAddress);
    }

    public static void updateSingleHeartRateRow(HeartRate heartRate){
        heartRateRepository.updateSingleHeartRateRow(heartRate);
    }

    public static void deleteSingleHeartRateRow(HeartRate heartRate){
        heartRateRepository.deleteSingleHeartRateRow(heartRate);
    }

    public static void deleteAllHeartRateData(){
        heartRateRepository.deleteAllHeartRateData();
    }


}
