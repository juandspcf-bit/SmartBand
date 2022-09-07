package com.misawabus.project.heartRate.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.Database.entities.Sports;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.datasource.SportsRepository;

import java.util.Date;

public class SportsViewModel extends AndroidViewModel {
    public static SportsRepository sportsRepository;

    public SportsViewModel(@NonNull Application application) {
        super(application);
        sportsRepository = new SportsRepository(application);
    }

    public static void insertSingleRow(Sports sports){
        sportsRepository.insertSingleRow(sports);
    }

    public static LiveData<Sports> getSingleRowForU(Date date, String macAddress, IdTypeDataTable idTypeDataTable){
        return sportsRepository.getSingleRowForU(date, macAddress, idTypeDataTable);
    }

    public static LiveData<Sports> getSinglePDayRow(Date date, String macAddress){
        return sportsRepository.getSinglePDayRow(date, macAddress);
    }

    public static void updateSingleRow(Sports sports){
        sportsRepository.updateSingleRow(sports);
    }

    public static void deleteSingleRow(Sports sports){
        sportsRepository.deleteSingleRow(sports);
    }

    public static void deleteAllData(){
        sportsRepository.deleteAllData();
    }


}
