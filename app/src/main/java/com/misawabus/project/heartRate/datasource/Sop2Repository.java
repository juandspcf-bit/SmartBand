package com.misawabus.project.heartRate.datasource;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.misawabus.project.heartRate.Database.entities.BloodPressure;
import com.misawabus.project.heartRate.Database.entities.Sop2;
import com.misawabus.project.heartRate.Database.rommdb.AppRoomDataBase;
import com.misawabus.project.heartRate.Database.rommdb.Sop2Dao;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;

import java.util.Date;

public class Sop2Repository {
    private final Sop2Dao sop2Dao;
    //private final LiveData<List<BloodPressure>> allData;

    public Sop2Repository(Application application) {
        AppRoomDataBase db = AppRoomDataBase.getDatabase(application);
        sop2Dao = db.getSop2Dao();
    }

    public LiveData<Sop2> getSingleRow(Date date, String macAddress) {
        return sop2Dao.getSingleRow(date, macAddress);
    }

    public void insertSingleRow(Sop2 sop2) {
        AppRoomDataBase.databaseWriteExecutor.execute(() -> sop2Dao.insertSingleRow(sop2));
    }

    public void updateSingleRow(Sop2 sop2) {
        AppRoomDataBase.databaseWriteExecutor.execute(() -> sop2Dao.updateSingleRow(sop2));
    }

    public void deleteSingleRow(Sop2 sop2) {
        AppRoomDataBase.databaseWriteExecutor.execute(() -> sop2Dao.deleteSingleRow(sop2));
    }

    public void deleteAllData() {
        AppRoomDataBase.databaseWriteExecutor.execute(sop2Dao::deleteAllData);
    }

}
