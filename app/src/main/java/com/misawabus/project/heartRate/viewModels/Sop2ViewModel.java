package com.misawabus.project.heartRate.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.Database.entities.Sop2;
import com.misawabus.project.heartRate.datasource.Sop2Repository;

import java.util.Date;

public class Sop2ViewModel extends AndroidViewModel {
    public static Sop2Repository sop2Repository;

    public Sop2ViewModel(@NonNull Application application) {
        super(application);
        sop2Repository = new Sop2Repository(application);
    }

    public static void insertSingleRow(Sop2 sop2){
        sop2Repository.insertSingleRow(sop2);
    }

    public static LiveData<Sop2> getSingleRowForU(Date date, String macAddress){
        return sop2Repository.getSingleRow(date, macAddress);
    }



    public static void updateSingleRow(Sop2 sop2){
        sop2Repository.updateSingleRow(sop2);
    }

    public static void deleteSingleRow(Sop2 sop2){
        sop2Repository.deleteSingleRow(sop2);
    }

    public static void deleteAllData(){
        sop2Repository.deleteAllData();
    }
}
