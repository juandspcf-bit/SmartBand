package com.misawabus.project.heartRate.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.datasource.SleepDataUIRepository;
import com.misawabus.project.heartRate.Database.entities.SleepDataUI;

import java.util.Date;
import java.util.List;

public class SleepDataUIViewModel extends AndroidViewModel {
    public static SleepDataUIRepository sleepDataUIRepository;


    public SleepDataUIViewModel(@NonNull Application application) {
        super(application);
        sleepDataUIRepository = new SleepDataUIRepository(application);

    }

    public static void insertSingleRow(SleepDataUI SleepDataUI){
        sleepDataUIRepository.insertSingleRow(SleepDataUI);
    }

    public static LiveData<List<SleepDataUI>> getListRows(Date date, String macAddress){
        return sleepDataUIRepository.getListRows(date, macAddress);
    }

    public static LiveData<SleepDataUI> getSingleRowForU(Date date, String macAddress, int index){
        return sleepDataUIRepository.getSingleRowForU(date, macAddress, index);
    }

    public static LiveData<SleepDataUI> getSinglePDayRow(Date date, String macAddress){
        return sleepDataUIRepository.getSinglePDayRow(date, macAddress);
    }

    public static void updateSingleRow(SleepDataUI SleepDataUI){
        sleepDataUIRepository.updateSingleRow(SleepDataUI);
    }

    public static void deleteSingleRow(SleepDataUI SleepDataUI){
        sleepDataUIRepository.deleteSingleRow(SleepDataUI);
    }

    public static void deleteAllData(){
        sleepDataUIRepository.deleteAllData();
    }
}
